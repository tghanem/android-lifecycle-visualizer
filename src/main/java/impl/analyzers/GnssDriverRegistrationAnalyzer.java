package impl.analyzers;

import com.intellij.psi.PsiMethod;
import impl.Helper;
import impl.model.dstl.ResourceAcquisition;
import interfaces.ILifecycleEventHandlerAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class GnssDriverRegistrationAnalyzer implements ILifecycleEventHandlerAnalyzer<ResourceAcquisition> {
    @Override
    public List<ResourceAcquisition> analyze(PsiMethod method) {
        List<ResourceAcquisition> result = new ArrayList<>();

        Helper.processMethodCallExpressions(
                method,
                (psiElement, qualifierFullyQualifiedName, methodName) -> {
                    if (qualifierFullyQualifiedName.equals("com.google.android.things.userdriver.UserDriverManager") &&
                            methodName.equals("registerGnssDriver")) {

                        result.add(new ResourceAcquisition(psiElement));
                    }
                });

        return result;
    }
}
