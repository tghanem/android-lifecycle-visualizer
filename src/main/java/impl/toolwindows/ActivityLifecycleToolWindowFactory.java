package impl.toolwindows;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import interfaces.graphics.dsvl.IActivityViewService;
import org.jetbrains.annotations.NotNull;

public class ActivityLifecycleToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ServiceManager
                .getService(IActivityViewService.class)
                .setActivityViewHolder(toolWindow);
    }
}
