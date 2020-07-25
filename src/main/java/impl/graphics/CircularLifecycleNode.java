package impl.graphics;

public class CircularLifecycleNode extends LifecycleNode {
    public CircularLifecycleNode(LifecycleNode target) {
        super(target.getName());
        this.target = target;
    }

    public LifecycleNode getTarget() {
        return target;
    }

    private final LifecycleNode target;
}
