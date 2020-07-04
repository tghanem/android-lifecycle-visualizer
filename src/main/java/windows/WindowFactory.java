package windows;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import interfaces.IActivityViewService;
import org.jetbrains.annotations.NotNull;

public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ActivityForm window = new ActivityForm();

        ServiceManager
                .getService(IActivityViewService.class)
                .registerViewProvider(window);

        toolWindow
                .getContentManager()
                .addContent(
                        ContentFactory
                                .SERVICE
                                .getInstance()
                                .createContent(window.getContent(), "", false));
    }
}
