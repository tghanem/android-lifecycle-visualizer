package impl.services;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import impl.model.dstl.Activity;
import interfaces.graphics.dsvl.IActivityViewService;
import interfaces.graphics.dsvl.model.ActivityMetadataToRender;
import interfaces.graphics.dsvl.model.LifecyclePanel;

import java.util.HashMap;
import java.util.Optional;

public class ActivityViewService implements IActivityViewService {
    private Optional<ToolWindow> activityViewHolder;
    private HashMap<String, DisplayedActivity> activities;

    public ActivityViewService() {
        activityViewHolder = Optional.empty();
        activities = new HashMap<>();
    }

    @Override
    public void setActivityViewHolder(ToolWindow toolWindow) {
        for (String activityQualifiedName : activities.keySet()) {
            DisplayedActivity displayedActivity =
                    activities.get(activityQualifiedName);

            toolWindow
                    .getContentManager()
                    .addContent(displayedActivity.content);
        }

        activityViewHolder = Optional.of(toolWindow);
    }

    @Override
    public void displayActivity(Activity activity) {
        String activityFullyQualifiedName =
                activity
                        .getPsiElement()
                        .getQualifiedName();

        if (activities.containsKey(activityFullyQualifiedName)) {
            if (activityViewHolder.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .setSelectedContent(activities.get(activityFullyQualifiedName).content);
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

            activities.put(
                    activityFullyQualifiedName,
                    new DisplayedActivity(activity, panel, activityContent));

            if (activityViewHolder.isPresent()) {
                activityViewHolder
                        .get()
                        .getContentManager()
                        .addContent(activityContent);
            }
        }
    }

    class DisplayedActivity {
        DisplayedActivity(
                Activity activity,
                LifecyclePanel panel,
                Content content) {

            this.activity = activity;
            this.panel = panel;
            this.content = content;
        }

        private final Activity activity;
        private final LifecyclePanel panel;
        private final Content content;
    }
}
