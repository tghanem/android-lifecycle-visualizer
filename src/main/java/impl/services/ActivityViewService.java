package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import impl.model.dstl.Activity;
import interfaces.IActivityFileProcessor;
import interfaces.graphics.dsvl.IActivityViewService;
import interfaces.graphics.dsvl.model.ActivityMetadataToRender;
import interfaces.graphics.dsvl.model.LifecyclePanel;
import org.apache.commons.codec.binary.Hex;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        toolWindow.addContentManagerListener(
                new ContentManagerListener() {
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

        if (contentsIndex.isEmpty()) {
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

        ActivityViewContents newContents =
                createActivityViewContents(activityFile);

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
            PsiFile activityFile) throws Exception {

        Optional<Activity> activity =
                ServiceManager
                        .getService(IActivityFileProcessor.class)
                        .process(activityFile);

        if (activity.isEmpty()) {
            throw new Exception("Expected to find an Activity file in " + activityFile.getName());
        }

        LifecyclePanel panel =
                new LifecyclePanel();

        panel.display(
                new ActivityMetadataToRender(
                        activity.get().getPsiElement(),
                        activity.get().getCallbackMethods()));

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

        return
                new ActivityViewContents(
                        activity.get(),
                        panel,
                        activityContent,
                        calculatePsiFileDigest(activityFile));
    }

    private String calculatePsiFileDigest(
            PsiFile file) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        digest.update(
                VfsUtil.loadBytes(file.getVirtualFile()));

        return Hex.encodeHexString(digest.digest());
    }

    class ActivityViewContents {
        ActivityViewContents(
                Activity activity,
                LifecyclePanel panel,
                Content content,
                String activityFileDigest) {

            this.activity = activity;
            this.panel = panel;
            this.content = content;
            this.activityFileDigest = activityFileDigest;
        }

        private final Activity activity;
        private final LifecyclePanel panel;
        private final Content content;
        private final String activityFileDigest;
    }
}
