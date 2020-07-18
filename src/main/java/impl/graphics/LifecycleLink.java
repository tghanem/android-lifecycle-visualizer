package impl.graphics;

public class LifecycleLink {
    public LifecycleLink(LifecycleNode target, boolean isCircular) {
        this.target = target;
        this.isCircular = isCircular;
    }

    public LifecycleNode getTarget() {
        return target;
    }

    public boolean isCircular() {
        return isCircular;
    }

    private final LifecycleNode target;
    private final boolean isCircular;
}
