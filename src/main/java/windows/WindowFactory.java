package windows;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import impl.*;
import org.jetbrains.annotations.NotNull;

public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ActivitiesWindow window =
                new ActivitiesWindow(
                        project,
                        new LifecycleProcessor(
                                new LifecycleParser(),
                                new LifecycleRepresentationConverter()),
                        new LifecycleComponentsProvider(),
                        new NotificationController(
                                new NotificationGroup(
                                        "Activities Visualizer Errors",
                                        NotificationDisplayType.TOOL_WINDOW,
                                        true),
                                project));

        toolWindow
                .getContentManager()
                .addContent(
                        ContentFactory
                                .SERVICE
                                .getInstance()
                                .createContent(window.getContent(), "", false));
    }
}
