package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiFile;
import impl.ActivityFileParser;
import interfaces.graphics.dsvl.IActivityViewService;
import interfaces.graphics.dsvl.model.ActivityMetadataToRender;
import impl.model.dstl.LifecycleAwareComponent;
import interfaces.*;
import interfaces.graphics.dsvl.IActivityViewProvider;

import java.util.Collection;
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

        Collection<IActivityViewProvider> viewProviders =
                ServiceManager
                        .getService(IActivityViewService.class)
                        .getViewProviders();

        for (IActivityViewProvider provider : viewProviders) {
            LifecycleAwareComponent component =
                    activityFileDocument.get();

            provider.display(
                    new ActivityMetadataToRender(
                            component.getPsiElement(),
                            component.getLifecycleEventHandlers()));
        }

        ServiceManager
                .getService(IActivityFileProcessingController.class)
                .setProcessedActivityFile(file, activityFileDocument.get());
    }
}
