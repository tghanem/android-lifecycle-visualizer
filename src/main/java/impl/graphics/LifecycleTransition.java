package impl.graphics;

public class LifecycleTransition {
    public LifecycleTransition(LifecycleNode start, LifecycleNode end) {
        this.start = start;
        this.end = end;
    }

    public LifecycleNode getStart() {
        return start;
    }

    public LifecycleNode getEnd() {
        return end;
    }

    private final LifecycleNode start;
    private final LifecycleNode end;
}
