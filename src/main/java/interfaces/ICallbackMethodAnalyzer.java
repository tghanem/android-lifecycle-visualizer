package interfaces;

import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ICallbackMethodAnalyzer<T> {
    List<T> analyze(PsiMethod method);
}
