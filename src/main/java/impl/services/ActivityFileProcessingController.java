package impl.services;

import com.intellij.psi.PsiFile;
import impl.model.dstl.LifecycleAwareComponent;
import interfaces.IActivityFileProcessingController;

import java.util.Optional;

public class ActivityFileProcessingController implements IActivityFileProcessingController {
    private Optional<PsiFile> lastProcessedActivityFile;
    private Optional<LifecycleAwareComponent> lastDisplayedLifecycleComponent;

    public ActivityFileProcessingController() {
        lastProcessedActivityFile = Optional.empty();
        lastDisplayedLifecycleComponent = Optional.empty();
    }

    @Override
    public Boolean shouldProcessActivityFile(PsiFile psiFile) {
        if (lastProcessedActivityFile.isPresent()) {
            return
                    !psiFile
                            .getVirtualFile()
                            .getCanonicalPath()
                            .equalsIgnoreCase(
                                    lastProcessedActivityFile
                                            .get()
                                            .getVirtualFile()
                                            .getCanonicalPath());
        } else {
            return true;
        }
    }

    @Override
    public void setProcessedActivityFile(
            PsiFile file,
            LifecycleAwareComponent component) {

        lastProcessedActivityFile = Optional.of(file);
        lastDisplayedLifecycleComponent = Optional.of(component);
    }

    @Override
    public Optional<LifecycleAwareComponent> getLastDisplayedLifecycleComponent() {
        return lastDisplayedLifecycleComponent;
    }
}
