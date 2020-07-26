package interfaces.consumers;

import com.intellij.psi.PsiMethodCallExpression;

@FunctionalInterface
public interface PsiMethodCallExpressionConsumer {
    void process(
            PsiMethodCallExpression psiElement,
            String qualifierFullyQualifiedName,
            String methodName);
}
