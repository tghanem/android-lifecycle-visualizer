package impl.services;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import impl.model.dstl.LifecycleAwareComponent;
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
    public void displayActivityView(LifecycleAwareComponent asComponent) {
        String activityFullyQualifiedName =
                asComponent
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
                            asComponent.getPsiElement(),
                            asComponent.getLifecycleEventHandlers()));

            Content activityContent =
                    ContentFactory
                        .SERVICE
                        .getInstance()
                        .createContent(panel, asComponent.getPsiElement().getName(), false);

            activities.put(
                    activityFullyQualifiedName,
                    new DisplayedActivity(asComponent, panel, activityContent));

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
                LifecycleAwareComponent lifecycleAwareComponent,
                LifecyclePanel panel,
                Content content) {

            this.lifecycleAwareComponent = lifecycleAwareComponent;
            this.panel = panel;
            this.content = content;
        }

        private final LifecycleAwareComponent lifecycleAwareComponent;
        private final LifecyclePanel panel;
        private final Content content;
    }
}
