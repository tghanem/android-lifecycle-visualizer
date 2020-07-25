package impl.graphics;

import com.intellij.psi.PsiClass;
import impl.model.dstl.LifecycleEventHandler;

import java.util.List;

public class ActivityMetadataToRender {
    public ActivityMetadataToRender(
            PsiClass activityClass,
            List<LifecycleEventHandler> handlers) {

        this.activityClass = activityClass;
        this.handlers = handlers;
    }

    public PsiClass getActivityClass() {
        return activityClass;
    }

    public List<LifecycleEventHandler> getHandlers() {
        return handlers;
    }

    private final PsiClass activityClass;
    private final List<LifecycleEventHandler> handlers;
}
