package windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ActivitiesWindow window = new ActivitiesWindow();

        toolWindow
                .getContentManager()
                .addContent(
                        ContentFactory
                                .SERVICE
                                .getInstance()
                                .createContent(window.getContent(), "", false));
    }
}
