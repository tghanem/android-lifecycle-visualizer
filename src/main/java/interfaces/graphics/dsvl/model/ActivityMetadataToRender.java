package interfaces.graphics.dsvl.model;

import com.intellij.psi.PsiClass;
import impl.model.dstl.CallbackMethod;

import java.util.List;

public class ActivityMetadataToRender {
    public ActivityMetadataToRender(
            PsiClass activityClass,
            List<CallbackMethod> callbackMethods) {

        this.activityClass = activityClass;
        this.callbackMethods = callbackMethods;
    }

    public PsiClass getActivityClass() {
        return activityClass;
    }

    public List<CallbackMethod> getCallbackMethods() {
        return callbackMethods;
    }

    private final PsiClass activityClass;
    private final List<CallbackMethod> callbackMethods;
}
