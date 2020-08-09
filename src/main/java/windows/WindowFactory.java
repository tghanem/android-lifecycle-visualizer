package windows;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import interfaces.graphics.dsvl.model.ActivityMetadataToRender;
import impl.model.dstl.LifecycleAwareComponent;
import interfaces.IActivityFileProcessingController;
import interfaces.graphics.dsvl.IActivityViewService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ActivityForm window = new ActivityForm();

        Optional<LifecycleAwareComponent> component =
                ServiceManager
                        .getService(IActivityFileProcessingController.class)
                        .getLastDisplayedLifecycleComponent();

        if (component.isPresent()) {
            window.display(
                    new ActivityMetadataToRender(
                            component
                                    .get()
                                    .getPsiElement(),
                            component
                                    .get()
                                    .getLifecycleEventHandlers()));
        }

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
