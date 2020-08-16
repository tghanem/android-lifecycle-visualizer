package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
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
import org.jetbrains.annotations.NotNull;

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
    public void openActivity(
            PsiFile activityFile) {

        Optional<ActivityViewContents> contents =
                getActivityViewContents(activityFile);

        if (contents.isPresent()) {
            if (activityViewHolder.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .setSelectedContent(contents.get().content);
            }
        } else {
            Optional<Activity> activity =
                    ServiceManager
                        .getService(IActivityFileProcessor.class)
                        .process(activityFile);

            if (activity.isEmpty()) {
                return;
            }

            LifecyclePanel panel = new LifecyclePanel();

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

            activities.add(
                    new ActivityViewContents(
                            activity.get(),
                            panel,
                            activityContent));

            if (activityViewHolder.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(activityContent);

                activityViewHolder
                        .get()
                        .getContentManager()
                        .setSelectedContent(activityContent, true);
            }
        }
    }

    @Override
    public void closeActivity(
            PsiFile activityFile) {

        Optional<ActivityViewContents> contents =
                getActivityViewContents(activityFile);

        if (contents.isEmpty() || activityViewHolder.isEmpty()) {
            return;
        }

        activityViewHolder
                .get()
                .getContentManager()
                .removeContent(contents.get().content, true);
    }

    private Optional<ActivityViewContents> getActivityViewContents(
            PsiFile activityFile) {

        String thisActivityFilePath =
                activityFile
                        .getVirtualFile()
                        .getCanonicalPath();

        for (ActivityViewContents contents : activities) {
            String thatActivityFilePath =
                    contents
                        .activity
                        .getPsiElement()
                        .getContainingFile()
                        .getVirtualFile()
                        .getCanonicalPath();

            if (thatActivityFilePath.equals(thisActivityFilePath)) {
                return Optional.of(contents);
            }
        }
        return Optional.empty();
    }

    class ActivityViewContents {
        ActivityViewContents(Activity activity, LifecyclePanel panel, Content content) {
            this.activity = activity;
            this.panel = panel;
            this.content = content;
        }

        private final Activity activity;
        private final LifecyclePanel panel;
        private final Content content;
    }
}
