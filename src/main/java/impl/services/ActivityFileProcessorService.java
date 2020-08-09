package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiFile;
import impl.ActivityFileParser;
import impl.model.dstl.LifecycleAwareComponent;
import interfaces.IActivityFileParser;
import interfaces.IActivityFileProcessingController;
import interfaces.IActivityFileProcessor;
import interfaces.graphics.dsvl.IActivityViewService;

import java.util.Optional;

public class ActivityFileProcessorService implements IActivityFileProcessor {
    private final IActivityFileParser lifecycleParser;

    public ActivityFileProcessorService() {
        lifecycleParser = new ActivityFileParser();
    }

    @Override
    public void process(PsiFile file) {
        Boolean shouldProcessActivityFile =
                ServiceManager
                    .getService(IActivityFileProcessingController.class)
                    .shouldProcessActivityFile(file);

        if (!shouldProcessActivityFile) {
            return;
        }

        Optional<LifecycleAwareComponent> activityFileDocument =
                lifecycleParser.parse(file);

        if (!activityFileDocument.isPresent()) {
            return;
        }

        ServiceManager
                .getService(IActivityViewService.class)
                .displayActivityView(activityFileDocument.get());

        ServiceManager
                .getService(IActivityFileProcessingController.class)
                .setProcessedActivityFile(file, activityFileDocument.get());
    }
}
