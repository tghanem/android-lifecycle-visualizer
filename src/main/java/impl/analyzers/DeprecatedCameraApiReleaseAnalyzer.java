package impl.analyzers;

import com.intellij.psi.PsiMethod;
import impl.Helper;
import impl.model.dstl.ResourceRelease;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedCameraApiReleaseAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceRelease> {
    @Override
    public List<ResourceRelease> analyze(PsiMethod method) {
        List<ResourceRelease> result = new ArrayList<>();

        Helper.processMethodCallExpressions(
                method,
                (psiElement, qualifierFullName, methodName) -> {
                    if (qualifierFullName.equals("android.hardware.Camera") && methodName.equals("release")) {
                        result.add(new ResourceRelease(psiElement));
                    }
                });

        return result;
    }
}
