package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.VirtualFile;
import impl.ActivityFileParser;
import impl.dsvl.LifecycleNode;
import impl.model.dstl.LifecycleAwareComponent;
import interfaces.IActivityFileParser;
import interfaces.IActivityFileProcessor;
import interfaces.IActivityViewProvider;
import interfaces.IActivityViewService;

import java.util.Collection;
import java.util.Optional;

public class ActivityFileProcessorService implements IActivityFileProcessor {
    private final IActivityFileParser lifecycleParser;

    public ActivityFileProcessorService() {
        lifecycleParser = new ActivityFileParser();
    }

    @Override
    public void Process(VirtualFile file) throws Exception {
        Optional<LifecycleAwareComponent> activityFileDocument =
                lifecycleParser.parse(file);

        if (!activityFileDocument.isPresent()) {
            return;
        }

        LifecycleNode viewDocument =
                LifecycleNode.valueOf(activityFileDocument.get());

        Collection<IActivityViewProvider> viewProviders =
                ServiceManager
                        .getService(IActivityViewService.class)
                        .getViewProviders();

        for (IActivityViewProvider provider : viewProviders) {
            provider.display(viewDocument);
        }
    }
}
