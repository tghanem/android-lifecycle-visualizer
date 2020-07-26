package impl.analyzers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class FullQualifiedClassAndMethodNamesAnalyzerBase<T> implements ILifecycleEventHandlerAnalyzer<T> {
    protected FullQualifiedClassAndMethodNamesAnalyzerBase(HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch) {
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

            if (methodCallsToMatch.contains(classAndMethodName)) {
                result.add(createMethodCallExpressionHolder(element));
            }
        }

        return result;
    }

    protected abstract T createMethodCallExpressionHolder(PsiElement methodCallExpression);

    private final HashSet<FullyQualifiedClassAndMethodName> methodCallsToMatch;
}
