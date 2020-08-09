package impl.model.dstl;

import com.intellij.psi.PsiClass;

import java.util.List;

public class Activity {
    public Activity(
            PsiClass element,
            List<CallbackMethod> callbackMethods) {

        this.element = element;
        this.callbackMethods = callbackMethods;
    }

    public PsiClass getPsiElement() {
        return element;
    }

    public List<CallbackMethod> getCallbackMethods() {
        return callbackMethods;
    }

    private final PsiClass element;
    private final List<CallbackMethod> callbackMethods;
}
