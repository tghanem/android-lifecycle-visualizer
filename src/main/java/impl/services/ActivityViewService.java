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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
                            activityFile,
                            contentsIndex);
                }
            }
        } else {
            createAndAddNewActivityViewContents(
                    activityFile,
                    Optional.empty());
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
            PsiFile activityFile,
            Optional<Integer> index) throws Exception {

        Optional<ActivityViewContents> newContents =
                createActivityViewContents(activityFile);

        if (!newContents.isPresent()) {
            return;
        }

        if (index.isPresent()) {
            activities.add(index.get(), newContents.get());
        } else {
            activities.add(newContents.get());
        }

        if (activityViewHolder.isPresent()) {
            if (index.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(newContents.get().content, index.get());
            } else {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(newContents.get().content);
            }

            activityViewHolder
                    .get()
                    .getContentManager()
                    .setSelectedContent(newContents.get().content, true);
        }
    }

    private Optional<ActivityViewContents> createActivityViewContents(
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

            return Optional.empty();
        }

        LifecyclePanel panel = new LifecyclePanel();

        HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap = new HashMap<>();

        CallbackMethodNode graphRoot =
                buildLifecycleGraph(
                        activity.get().getPsiElement(),
                        activity.get().getCallbackMethods(),
                        subtreeVisibleHashMap,
                        node -> {
                            if (node instanceof CallbackMethodNode) {
                                CallbackMethodNode callbackMethodNode =
                                        (CallbackMethodNode) node;

                                Boolean subtreeVisible =
                                        subtreeVisibleHashMap.get(node);

                                if (!subtreeVisible) {
                                    for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                                        nextNode.setVisible(true);
                                    }
                                    subtreeVisibleHashMap.replace(node, true);
                                } else {
                                    for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                                        setNodeVisibility(nextNode, subtreeVisibleHashMap, false);
                                    }
                                    subtreeVisibleHashMap.replace(node, false);
                                }
                            }
                            panel.revalidate();
                            panel.repaint();
                        },
                        node -> navigateTo(node.getCallbackMethod().get().getPsiElement()),
                        node -> navigateTo(node.getResourceAcquisition().getPsiElement()),
                        node -> navigateTo(node.getResourceRelease().getPsiElement()),
                        node -> {
                            PsiMethod callbackMethodPsiElement =
                                    ServiceManager
                                            .getService(IActivityFileModifier.class)
                                            .createAndAddCallbackMethod(activity.get().getPsiElement(), node.getName());

                            node.setCallbackMethod(
                                    new CallbackMethod(
                                            callbackMethodPsiElement,
                                            new ArrayList<>(),
                                            new ArrayList<>()));

                            navigateTo(callbackMethodPsiElement);
                        });

        panel.display(graphRoot);

        Content activityContent =
                ContentFactory
                        .SERVICE
                        .getInstance()
                        .createContent(
                                panel,
                                activity
                                        .get()
                                        .getPsiElement()
                                        .getName(),
                                false);

        ActivityViewContents contents =
                new ActivityViewContents(
                        activity.get(),
                        panel,
                        activityContent,
                        calculatePsiFileDigest(activityFile),
                        graphRoot,
                        subtreeVisibleHashMap);

        return Optional.of(contents);
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
            HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap,
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
                        goToNode,
                        onAddCallbackMethod));

        onPause.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleNode(
                        activityClass,
                        onCreate,
                        goToNode,
                        onAddCallbackMethod));

        onPause.addNextNode(0, onStop);

        CallbackMethodNode onRestart =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onRestart",
                        callbackMethods,
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
                        goToNode,
                        onAddCallbackMethod));

        CallbackMethodNode onDestroy =
                buildLifecycleHandlerNode(
                        activityClass,
                        "onDestroy",
                        callbackMethods,
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
                        goToNode,
                        onAddCallbackMethod));

        subtreeVisibleHashMap.put(onCreate, false);
        subtreeVisibleHashMap.put(onStart, false);
        subtreeVisibleHashMap.put(onResume, false);
        subtreeVisibleHashMap.put(onPause, false);
        subtreeVisibleHashMap.put(onStop, false);
        subtreeVisibleHashMap.put(onRestart, false);
        subtreeVisibleHashMap.put(onDestroy, false);

        return onCreate;
    }

    private CallbackMethodNode buildLifecycleHandlerNode(
            PsiClass activityClass,
            String callbackMethodName,
            List<CallbackMethod> callbackMethods,
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
                        callbackMethodNode -> onNodeClicked.accept(callbackMethodNode),
                        goToNode,
                        onAddCallbackMethod);

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addNextNode(
                        lifecycleNodeFactory
                                .createResourceAcquisitionLifecycleNode(resourceAcquisition, goToResourceAcquisition));
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.addNextNode(
                        lifecycleNodeFactory
                                .createResourceReleaseLifecycleNode(resourceRelease, goToResourceRelease));
            }
        }

        return node;
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

    private void setNodeVisibility(
            LifecycleNode node,
            HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap,
            boolean visibility) {

        if (node instanceof CallbackMethodNode) {
            CallbackMethodNode callbackMethodNode = (CallbackMethodNode) node;
            for (LifecycleNode nextNode : callbackMethodNode.getNextNodes()) {
                setNodeVisibility(nextNode, subtreeVisibleHashMap, visibility);
            }
            subtreeVisibleHashMap.replace(node, false);
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
                HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap) {

            this.activity = activity;
            this.panel = panel;
            this.content = content;
            this.activityFileDigest = activityFileDigest;
            this.graphRoot = graphRoot;
            this.subtreeVisibleHashMap = subtreeVisibleHashMap;
        }

        private final Activity activity;
        private final LifecyclePanel panel;
        private final Content content;
        private final String activityFileDigest;
        private final CallbackMethodNode graphRoot;
        private final HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
    }
}
