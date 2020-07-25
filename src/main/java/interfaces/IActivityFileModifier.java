package interfaces;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

public interface IActivityFileModifier {
    PsiMethod createAndAddLifecycleHandlerMethod(PsiClass activityClass, String handlerName);
}
