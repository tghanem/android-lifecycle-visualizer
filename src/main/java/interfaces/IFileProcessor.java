package interfaces;

import com.intellij.psi.PsiFile;

public interface IFileProcessor {
    void setCurrentlyOpenedFile(PsiFile file) throws Exception;
}
