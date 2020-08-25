package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import impl.model.dstl.Activity;
import impl.model.dstl.CallbackMethod;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.IActivityFileModifier;
import interfaces.IActivityFileProcessor;
import interfaces.INotificationService;
import interfaces.graphics.dsvl.IActivityViewService;
import interfaces.graphics.dsvl.ILifecycleNodeFactory;
import interfaces.graphics.dsvl.model.*;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActivityViewService implements IActivityViewService {
    private Optional<ToolWindow> activityViewHolder;
    private List<ActivityViewContents> activities;

    public ActivityViewService() {
        activityViewHolder = Optional.empty();
        activities = new ArrayList<>();
    }

    @Override
    public void setActivityViewHolder(
            Project project,
            ToolWindow toolWindow) {

        for (ActivityViewContents activity : activities) {
            toolWindow
                    .getContentManager()
                    .addContent(activity.content);
        }

        toolWindow
                .getContentManager()
                .addContentManagerListener(
                        new ContentManagerAdapter() {
                            @Override
                            public void selectionChanged(@NotNull ContentManagerEvent event) {
                                if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
                                    new OpenFileDescriptor(
                                            project,
                                            activities
                                                    .get(event.getIndex())
                                                    .activity
                                                    .getPsiElement()
                                                    .getContainingFile()
                                                    .getVirtualFile())
                                            .navigateInEditor(project, true);
                                }
                            }
                        });

        activityViewHolder = Optional.of(toolWindow);
    }

    @Override
    public void openOrReloadActivity(
            PsiFile activityFile) throws Exception {

        Optional<Activity> activity =
                ServiceManager
                        .getService(IActivityFileProcessor.class)
                        .process(activityFile);

        if (!activity.isPresent()) {
            ServiceManager
                    .getService(INotificationService.class)
                    .notifyError(
                            activityFile.getProject(),
                            new Exception("Expected to file Activity class in file " + activityFile.getName()));

            return;
        }

        Optional<Integer> contentsIndex =
                getActivityViewContents(activityFile);

        if (contentsIndex.isPresent()) {
            ActivityViewContents contents =
                    activities.get(contentsIndex.get());

            if (activityViewHolder.isPresent()) {
                String newContentsDigest =
                        calculatePsiFileDigest(activityFile);

                if (newContentsDigest.equals(contents.activityFileDigest)) {
                    activityViewHolder
                            .get()
                            .getContentManager()
                            .setSelectedContent(contents.content);
                } else {
                    activities.remove(contentsIndex.get());

                    activityViewHolder
                            .get()
                            .getContentManager()
                            .removeContent(contents.content, true);

                    createAndAddNewActivityViewContents(
                            activity.get(),
                            contentsIndex,
                            getSubtreeVisibility(Optional.of(contents.subtreeVisibility)),
                            getUnderlyingCallbackMethods(activity.get().getCallbackMethods()));
                }
            }
        } else {
            createAndAddNewActivityViewContents(
                    activity.get(),
                    Optional.empty(),
                    getSubtreeVisibility(Optional.empty()),
                    getUnderlyingCallbackMethods(activity.get().getCallbackMethods()));
        }
    }

    @Override
    public void closeActivity(
            PsiFile activityFile) {

        Optional<Integer> contentsIndex =
                getActivityViewContents(activityFile);

        if (!contentsIndex.isPresent()) {
            return;
        }

        ActivityViewContents contents =
                activities.get(contentsIndex.get());

        activities.remove(contents);

        if (activityViewHolder.isPresent()) {
            activityViewHolder
                    .get()
                    .getContentManager()
                    .removeContent(contents.content, true);
        }
    }

    private Optional<Integer> getActivityViewContents(
            PsiFile activityFile) {

        String thisActivityFilePath =
                activityFile
                        .getVirtualFile()
                        .getCanonicalPath();

        for (int i = 0; i < activities.size(); i++) {
            String thatActivityFilePath =
                    activities
                            .get(i)
                            .activity
                            .getPsiElement()
                            .getContainingFile()
                            .getVirtualFile()
                            .getCanonicalPath();

            if (thatActivityFilePath.equals(thisActivityFilePath)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private void createAndAddNewActivityViewContents(
            Activity activity,
            Optional<Integer> index,
            HashMap<String, Boolean> subtreeVisibility,
            HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods) throws Exception {

        ActivityViewContents newContents =
                createActivityViewContents(activity, subtreeVisibility, underlyingCallbackMethods);

        if (index.isPresent()) {
            activities.add(index.get(), newContents);
        } else {
            activities.add(newContents);
        }

        if (activityViewHolder.isPresent()) {
            if (index.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(newContents.content, index.get());
            } else {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(newContents.content);
            }

            activityViewHolder
                    .get()
                    .getContentManager()
                    .setSelectedContent(newContents.content, true);
        }
    }

    private ActivityViewContents createActivityViewContents(
            Activity activity,
            HashMap<String, Boolean> subtreeVisibility,
            HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods) throws Exception {

        LifecyclePanel panel = new LifecyclePanel();

        CallbackMethodNode graphRoot =
                buildLifecycleGraph(
                        activity.getPsiElement(),
                        activity.getCallbackMethods(),
                        node -> underlyingCallbackMethods.get(node.getName()).isPresent(),
                        node -> paintNode(node, subtreeVisibility, underlyingCallbackMethods),
                        node -> {
                            if (node instanceof CallbackMethodNode) {
                                toggleNodeVisibility((CallbackMethodNode) node, subtreeVisibility);
                            }
                            panel.revalidate();
                            panel.repaint();
                        },
                        node -> navigateTo(underlyingCallbackMethods.get(node.getName()).get().getPsiElement()),
                        node -> navigateTo(node.getResourceAcquisition().getPsiElement()),
                        node -> navigateTo(node.getResourceRelease().getPsiElement()),
                        node -> {
                            PsiMethod callbackMethodPsiElement =
                                    ServiceManager
                                            .getService(IActivityFileModifier.class)
                                            .createAndAddCallbackMethod(activity.getPsiElement(), node.getName());

                            underlyingCallbackMethods
                                    .replace(
                                            node.getName(),
                                            Optional.of(
                                                    new CallbackMethod(
                                                            callbackMethodPsiElement,
                                                            new ArrayList<>(),
                                                            new ArrayList<>())));

                            navigateTo(callbackMethodPsiElement);
                        });

        panel.setGraphRoot(graphRoot);

        Content activityContent =
                ContentFactory
                        .SERVICE
                        .getInstance()
                        .createContent(panel, activity.getPsiElement().getName(), false);

        return
                new ActivityViewContents(
                        activity,
                        panel,
                        activityContent,
                        calculatePsiFileDigest(activity.getPsiElement().getContainingFile()),
                        graphRoot,
                        subtreeVisibility,
                        underlyingCallbackMethods);
    }

    private String calculatePsiFileDigest(
            PsiFile file) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        digest.update(
                VfsUtil.loadBytes(file.getVirtualFile()));

        return Hex.encodeHexString(digest.digest());
    }

    private CallbackMethodNode buildLifecycleGraph(
            PsiClass activityClass,
            List<CallbackMethod> callbackMethods,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<LifecycleNode> onNodeClicked,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<ResourceAcquisitionLifecycleNode> goToResourceAcquisition,
            Consumer<ResourceReleaseLifecycleNode> goToResourceRelease,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        ILifecycleNodeFactory lifecycleNodeFactory =
                ServiceManager.getService(ILifecycleNodeFactory.class);

        CallbackMethodNode onCreate =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onCreate",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onCreate.setVisible(true);

        CallbackMethodNode onStart =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onStart",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onCreate.addNextNode(0, onStart);

        CallbackMethodNode onResume =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onResume",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onStart.addNextNode(0, onResume);

        CallbackMethodNode onPause =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onPause",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onResume.addNextNode(0, onPause);

        CallbackMethodNode onStop =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onStop",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onPause.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleNode(
                        activityClass,
                        onResume,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        goToNode,
                        onAddCallbackMethod));

        onPause.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleNode(
                        activityClass,
                        onCreate,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        goToNode,
                        onAddCallbackMethod));

        onPause.addNextNode(0, onStop);

        CallbackMethodNode onRestart =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onRestart",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onRestart.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleNode(
                        activityClass,
                        onStart,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        goToNode,
                        onAddCallbackMethod));

        CallbackMethodNode onDestroy =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onDestroy",
                        callbackMethods,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        onNodeClicked,
                        goToNode,
                        goToResourceAcquisition,
                        goToResourceRelease,
                        onAddCallbackMethod);

        onStop.addNextNode(0, onRestart);
        onStop.addNextNode(0, onDestroy);

        onStop.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleNode(
                        activityClass,
                        onCreate,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        goToNode,
                        onAddCallbackMethod));

        return onCreate;
    }

    private CallbackMethodNode buildLifecycleHandlerNode(
            PsiClass activityClass,
            String callbackMethodName,
            List<CallbackMethod> callbackMethods,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<LifecycleNode> onNodeClicked,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<ResourceAcquisitionLifecycleNode> goToResourceAcquisition,
            Consumer<ResourceReleaseLifecycleNode> goToResourceRelease,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        ILifecycleNodeFactory lifecycleNodeFactory =
                ServiceManager.getService(ILifecycleNodeFactory.class);

        Optional<CallbackMethod> handler =
                findByName(callbackMethods, callbackMethodName);

        CallbackMethodNode node =
                lifecycleNodeFactory.createCallbackMethodNode(
                        activityClass,
                        callbackMethodName,
                        handler,
                        nodeHasUnderlyingCallbackMethod,
                        paintNode,
                        callbackMethodNode -> onNodeClicked.accept(callbackMethodNode),
                        goToNode,
                        onAddCallbackMethod);

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addNextNode(
                        lifecycleNodeFactory
                                .createResourceAcquisitionLifecycleNode(resourceAcquisition, paintNode, goToResourceAcquisition));
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.addNextNode(
                        lifecycleNodeFactory
                                .createResourceReleaseLifecycleNode(resourceRelease, paintNode, goToResourceRelease));
            }
        }

        return node;
    }

    private void paintNode(
            LifecycleNode node,
            HashMap<String, Boolean> subtreeVisibility,
            HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods) {

        node.setToolTipText(node.getName());

        if (node instanceof CallbackMethodNode) {
            CallbackMethodNode callbackMethodNode = (CallbackMethodNode) node;

            callbackMethodNode.setIcon(
                    new ImageIcon(getClass().getClassLoader().getResource("handler.png")));

            if (underlyingCallbackMethods.get(node.getName()).isPresent()) {
                node.setBackground(Color.YELLOW);
            } else {
                node.setBackground(null);
            }

            if (subtreeVisibility.get(callbackMethodNode.getName())) {
                for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                    nextNode.setVisible(true);
                }
            } else {
                for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                    nextNode.setVisible(false);
                }
            }

        } else if (node instanceof CircularLifecycleNode) {
            CircularLifecycleNode circularLifecycleNode = (CircularLifecycleNode) node;

            circularLifecycleNode.setForeground(Color.GRAY);

            circularLifecycleNode.setIcon(
                    new ImageIcon(getClass().getClassLoader().getResource("circular_node.png")));

        } else if (node instanceof ResourceAcquisitionLifecycleNode) {
            ResourceAcquisitionLifecycleNode resourceAcquisitionLifecycleNode = (ResourceAcquisitionLifecycleNode) node;

            resourceAcquisitionLifecycleNode.setIcon(
                    new ImageIcon(getClass().getClassLoader().getResource("acquire.png")));

        } else if (node instanceof ResourceReleaseLifecycleNode) {
            ResourceReleaseLifecycleNode resourceReleaseLifecycleNode = (ResourceReleaseLifecycleNode) node;

            resourceReleaseLifecycleNode.setIcon(
                    new ImageIcon(getClass().getClassLoader().getResource("release.png")));
        }
    }

    private HashMap<String, Boolean> getSubtreeVisibility(
            Optional<HashMap<String, Boolean>> subtreeVisibility) {

        HashMap<String, Boolean> result = new HashMap<>();

        if (subtreeVisibility.isPresent()) {
            result.put("onCreate", subtreeVisibility.get().getOrDefault("onCreate", false));
            result.put("onStart", subtreeVisibility.get().getOrDefault("onStart", false));
            result.put("onResume", subtreeVisibility.get().getOrDefault("onResume", false));
            result.put("onPause", subtreeVisibility.get().getOrDefault("onPause", false));
            result.put("onStop", subtreeVisibility.get().getOrDefault("onStop", false));
            result.put("onRestart", subtreeVisibility.get().getOrDefault("onRestart", false));
            result.put("onDestroy", subtreeVisibility.get().getOrDefault("onDestroy", false));
        } else {
            result.put("onCreate", false);
            result.put("onStart", false);
            result.put("onResume", false);
            result.put("onPause", false);
            result.put("onStop", false);
            result.put("onRestart", false);
            result.put("onDestroy", false);
        }

        return result;
    }

    private HashMap<String, Optional<CallbackMethod>> getUnderlyingCallbackMethods(
            List<CallbackMethod> callbackMethods) {

        HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods =
                new HashMap<>();

        underlyingCallbackMethods.put(
                "onCreate",
                findByName(callbackMethods, "onCreate"));

        underlyingCallbackMethods.put(
                "onStart",
                findByName(callbackMethods, "onStart"));

        underlyingCallbackMethods.put(
                "onResume",
                findByName(callbackMethods, "onResume"));

        underlyingCallbackMethods.put(
                "onPause",
                findByName(callbackMethods, "onPause"));

        underlyingCallbackMethods.put(
                "onStop",
                findByName(callbackMethods, "onStop"));

        underlyingCallbackMethods.put(
                "onRestart",
                findByName(callbackMethods, "onRestart"));

        underlyingCallbackMethods.put(
                "onDestroy",
                findByName(callbackMethods, "onDestroy"));

        return underlyingCallbackMethods;
    }

    private Optional<CallbackMethod> findByName(
            List<CallbackMethod> handlers,
            String handlerName) {

        for (CallbackMethod handler : handlers) {
            if (handler.getPsiElement().getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }

        return Optional.empty();
    }

    private void toggleNodeVisibility(
            CallbackMethodNode node,
            HashMap<String, Boolean> subtreeVisibility) {

        if (!subtreeVisibility.get(node.getName())) {
            for (LifecycleNode nextNode : node.getNextNodes()) {
                nextNode.setVisible(true);
            }
            subtreeVisibility.replace(node.getName(), true);
        } else {
            for (LifecycleNode nextNode : node.getNextNodes()) {
                setNodeVisibility(nextNode, false);
            }
            subtreeVisibility.replace(node.getName(), false);
        }
    }

    private void setNodeVisibility(
            LifecycleNode node,
            Boolean visibility) {

        if (node instanceof CallbackMethodNode) {
            CallbackMethodNode callbackMethodNode = (CallbackMethodNode) node;

            for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                setNodeVisibility(nextNode, visibility);
            }
        }

        node.setVisible(visibility);
    }

    private void navigateTo(
            PsiElement element) {
        PsiElement navigationElement = element.getNavigationElement();

        if (navigationElement == null) {
            return;
        }

        if (navigationElement instanceof Navigatable) {
            Navigatable asNavigatable = (Navigatable) navigationElement;

            if (asNavigatable.canNavigate()) {
                asNavigatable.navigate(true);
            }
        }
    }

    class ActivityViewContents {
        ActivityViewContents(
                Activity activity,
                LifecyclePanel panel,
                Content content,
                String activityFileDigest,
                CallbackMethodNode graphRoot,
                HashMap<String, Boolean> subtreeVisibility,
                HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods) {

            this.activity = activity;
            this.panel = panel;
            this.content = content;
            this.activityFileDigest = activityFileDigest;
            this.graphRoot = graphRoot;
            this.subtreeVisibility = subtreeVisibility;
            this.underlyingCallbackMethods = underlyingCallbackMethods;
        }

        private final Activity activity;
        private final LifecyclePanel panel;
        private final Content content;
        private final String activityFileDigest;
        private final CallbackMethodNode graphRoot;
        private final HashMap<String, Boolean> subtreeVisibility;
        private final HashMap<String, Optional<CallbackMethod>> underlyingCallbackMethods;
    }
}
