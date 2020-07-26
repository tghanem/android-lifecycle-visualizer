package impl.analyzers;

import com.intellij.psi.PsiMethod;
import impl.Helper;
import impl.model.dstl.ResourceRelease;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class GnssDriverUnregistrationAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceRelease> {
    @Override
    public List<ResourceRelease> analyze(PsiMethod method) {
        List<ResourceRelease> result = new ArrayList<>();

        Helper.processMethodCallExpressions(
                method,
                (psiElement, qualifierFullyQualifiedName, methodName) -> {
                    if (qualifierFullyQualifiedName.equals("com.google.android.things.userdriver.UserDriverManager") &&
                            methodName.equals("unregisterGnssDriver")) {

                        result.add(new ResourceRelease(psiElement));
                    }
                });

        return result;
    }
}
