package impl.analyzers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import interfaces.ICallbackMethodAnalyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class FullyQualifiedClassAndMethodNamesAnalyzer
        implements ICallbackMethodAnalyzer<FullyQualifiedClassAndMethodName, PsiMethodCallExpression> {

    @Override
    public void analyze(
            PsiMethod method,
            Predicate<FullyQualifiedClassAndMethodName> isPatternRecognized,
            BiConsumer<FullyQualifiedClassAndMethodName, PsiMethodCallExpression> processPatternInstance) {

        HashSet<FullyQualifiedClassAndMethodName> visitingMethods =
                new HashSet<>();

        visitingMethods.add(
                new FullyQualifiedClassAndMethodName(
                        method.getContainingClass().getQualifiedName(),
                        method.getName()));

        analyze(method, isPatternRecognized, processPatternInstance, visitingMethods);
    }

    private void analyze(
            PsiMethod method,
            Predicate<FullyQualifiedClassAndMethodName> isPatternRecognized,
            BiConsumer<FullyQualifiedClassAndMethodName, PsiMethodCallExpression> processPatternInstance,
            HashSet<FullyQualifiedClassAndMethodName> visitingMethods) {

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(
                        method,
                        PsiMethodCallExpression.class);

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

            if (isPatternRecognized.test(classAndMethodName)) {
                processPatternInstance.accept(classAndMethodName, methodCallExpression);
            } else {
                Boolean isInSameProject =
                        resolvedMethod
                                .getContainingFile()
                                .getVirtualFile()
                                .getCanonicalPath()
                                .startsWith(method.getProject().getBasePath());

                if (isInSameProject && !visitingMethods.contains(classAndMethodName)) {
                    visitingMethods.add(classAndMethodName);
                    analyze(resolvedMethod, isPatternRecognized, processPatternInstance, visitingMethods);
                    visitingMethods.remove(classAndMethodName);
                }
            }
        }

    }
}
