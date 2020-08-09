package interfaces;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

public interface IActivityFileModifier {
    PsiMethod createAndAddCallbackMethod(
            PsiClass activityClass,
            String callbackMethodName);
}
