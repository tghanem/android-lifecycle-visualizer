package impl.services;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import impl.model.dstl.Activity;
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
    public void setActivityViewHolder(Project project, ToolWindow toolWindow) {
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
                            FileEditorManager
                                    .getInstance(project)
                                    .setSelectedEditor(
                                            activities
                                                    .get(event.getIndex())
                                                    .activity
                                                    .getPsiElement()
                                                    .getContainingFile()
                                                    .getVirtualFile(),
                                            FileEditorProvider.EP_FILE_EDITOR_PROVIDER.getName());
                        }
                    }
                });

        activityViewHolder = Optional.of(toolWindow);
    }

    @Override
    public void displayActivity(Activity activity) {
        String activityFullyQualifiedName =
                activity
                        .getPsiElement()
                        .getQualifiedName();

        Optional<ActivityViewContents> viewContents =
                getActivityViewContents(activityFullyQualifiedName);

        if (viewContents.isPresent()) {
            if (activityViewHolder.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .setSelectedContent(viewContents.get().content);
            }
        } else {
            LifecyclePanel panel = new LifecyclePanel();

            panel.display(
                    new ActivityMetadataToRender(
                            activity.getPsiElement(),
                            activity.getCallbackMethods()));

            Content activityContent =
                    ContentFactory
                            .SERVICE
                            .getInstance()
                            .createContent(panel, activity.getPsiElement().getName(), false);

            activities.add(new ActivityViewContents(activity, panel, activityContent));

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

    private Optional<ActivityViewContents> getActivityViewContents(String activityFullyQualifiedName) {
        for (ActivityViewContents contents : activities) {
            String thatActivityFullyQualifiedName =
                    contents
                            .activity
                            .getPsiElement()
                            .getQualifiedName();

            if (thatActivityFullyQualifiedName.equals(activityFullyQualifiedName)) {
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
