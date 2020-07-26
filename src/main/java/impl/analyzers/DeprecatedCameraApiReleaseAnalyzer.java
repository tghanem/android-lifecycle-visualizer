package impl.analyzers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import impl.model.dstl.ResourceRelease;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeprecatedCameraApiReleaseAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceRelease> {
    @Override
    public List<ResourceRelease> analyze(PsiMethod method) {
        List<ResourceRelease> result = new ArrayList<>();

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiElement methodReferenceExpression =
                    methodCallExpression.getFirstChild();

            if (methodReferenceExpression instanceof PsiReferenceExpression) {
                PsiReferenceExpression referenceExpression =
                        (PsiReferenceExpression) methodReferenceExpression;

                if (referenceExpression.getQualifierExpression() != null) {
                    if (referenceExpression.getQualifierExpression().getType() != null) {
                        String qualifierName =
                                referenceExpression
                                        .getQualifierExpression()
                                        .getType()
                                        .getCanonicalText();

                        String[] methodName =
                                referenceExpression
                                        .getQualifiedName()
                                        .split("\\.");

                        if (methodName.length > 1) {
                            if (qualifierName.equals("android.hardware.Camera") && methodName[1].equals("release")) {
                                result.add(new ResourceRelease(element));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
