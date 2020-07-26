package impl.analyzers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import impl.model.dstl.ResourceAcquisition;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeprecatedCameraApiAcquisitionAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceAcquisition> {
    @Override
    public List<ResourceAcquisition> analyze(PsiMethod method) {
        List<ResourceAcquisition> result = new ArrayList<>();

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiMethod resolvedMethod =
                    methodCallExpression.resolveMethod();

            if (resolvedMethod != null) {
                String returnTypeName =
                        resolvedMethod.getReturnType().getCanonicalText();

                if (returnTypeName.equals("android.hardware.Camera")) {
                    result.add(new ResourceAcquisition(element));
                }
            }
        }

        return result;
    }
}
