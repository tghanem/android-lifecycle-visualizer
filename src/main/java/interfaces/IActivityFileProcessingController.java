package interfaces;

import com.intellij.psi.PsiFile;

public interface IActivityFileProcessingController {
    Boolean shouldProcessActivityFile(PsiFile file);

    void setProcessedActivityFile(PsiFile file);
}
