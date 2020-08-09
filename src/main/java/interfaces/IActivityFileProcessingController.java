package interfaces;

import com.intellij.psi.PsiFile;
import impl.model.dstl.Activity;

import java.util.Optional;

public interface IActivityFileProcessingController {
    Boolean shouldProcessActivityFile(PsiFile file);

    void setProcessedActivityFile(
            PsiFile file,
            Activity activity);

    Optional<Activity> getLastDisplayedActivity();
}
