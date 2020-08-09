package interfaces;

import com.intellij.psi.PsiFile;
import impl.model.dstl.LifecycleAwareComponent;

import java.util.Optional;

public interface IActivityFileProcessingController {
    Boolean shouldProcessActivityFile(PsiFile file);

    void setProcessedActivityFile(
            PsiFile file,
            LifecycleAwareComponent component);

    Optional<LifecycleAwareComponent> getLastDisplayedLifecycleComponent();
}
