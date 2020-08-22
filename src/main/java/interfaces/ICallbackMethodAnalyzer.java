package interfaces;

import com.intellij.psi.PsiMethod;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface ICallbackMethodAnalyzer<TPattern, TInstance> {
    void analyze(
            PsiMethod method,
            Predicate<TPattern> isPatternRecognized,
            BiConsumer<TPattern, TInstance> processPatternInstance);
}
