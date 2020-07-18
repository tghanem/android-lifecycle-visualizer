package interfaces;

import com.intellij.psi.PsiFile;

public interface IActivityFileProcessor {
    void Process(PsiFile file) throws Exception;
}
