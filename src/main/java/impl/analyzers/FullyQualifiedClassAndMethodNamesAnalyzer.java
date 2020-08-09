package impl.analyzers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import interfaces.ICallbackMethodAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class FullyQualifiedClassAndMethodNamesAnalyzer<T> implements ICallbackMethodAnalyzer<T> {
    public FullyQualifiedClassAndMethodNamesAnalyzer(
            HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, T>> methodCallsToMatch) {

        this.methodCallsToMatch = methodCallsToMatch;
    }

    @Override
    public List<T> analyze(PsiMethod method) {
        List<T> result = new ArrayList<>();

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiMethod resolvedMethod =
                    methodCallExpression.resolveMethod();

            if (resolvedMethod == null) {
                continue;
            }

            FullyQualifiedClassAndMethodName classAndMethodName =
                    new FullyQualifiedClassAndMethodName(
                            resolvedMethod.getContainingClass().getQualifiedName(),
                            resolvedMethod.getName());

            if (methodCallsToMatch.containsKey(classAndMethodName)) {
                result.add(methodCallsToMatch.get(classAndMethodName).apply(methodCallExpression));
            }
        }

        return result;
    }

    private final HashMap<FullyQualifiedClassAndMethodName, Function<PsiMethodCallExpression, T>> methodCallsToMatch;
}
