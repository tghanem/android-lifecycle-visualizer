package impl.model.dstl;

import com.intellij.psi.PsiClass;

import java.util.List;

public class LifecycleAwareComponent {
    public LifecycleAwareComponent(
            PsiClass element,
            List<LifecycleEventHandler> lifecycleEventHandlers) {

        this.element = element;
        this.lifecycleEventHandlers = lifecycleEventHandlers;
    }

    public PsiClass getPsiElement() {
        return element;
    }

    public List<LifecycleEventHandler> getLifecycleEventHandlers() {
        return lifecycleEventHandlers;
    }

    private final PsiClass element;
    private final List<LifecycleEventHandler> lifecycleEventHandlers;
}
