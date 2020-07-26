package impl.analyzers;

import com.intellij.psi.PsiMethod;
import impl.Helper;
import impl.model.dstl.ResourceAcquisition;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedCameraApiAcquisitionAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceAcquisition> {
    @Override
    public List<ResourceAcquisition> analyze(PsiMethod method) {
        List<ResourceAcquisition> result = new ArrayList<>();

        Helper.processMethodCallExpressions(
                method,
                (psiElement, qualifierFullyQualifiedName, methodName) -> {
                    if (qualifierFullyQualifiedName.equals("android.hardware.Camera") && methodName.equals("open")) {
                        result.add(new ResourceAcquisition(psiElement));
                    }
                });

        return result;
    }
}
