package interfaces;

import com.intellij.psi.PsiMethod;

import java.util.List;

public interface ILifecycleEventHandlerAnalyzer<T> {
    List<T> analyze(PsiMethod method);
}
