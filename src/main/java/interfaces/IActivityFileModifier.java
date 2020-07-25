package interfaces;

import com.intellij.psi.PsiClass;

public interface IActivityFileModifier {
    void addLifecycleEventHandler(PsiClass activityClass, String handlerName);
}
