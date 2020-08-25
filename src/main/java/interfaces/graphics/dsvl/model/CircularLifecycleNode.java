package interfaces.graphics.dsvl.model;

import java.util.function.Consumer;

public class CircularLifecycleNode extends LifecycleNode {
    public CircularLifecycleNode(Consumer<LifecycleNode> paintNode, LifecycleNode target) {
        super(paintNode, target.getName());

        this.target = target;
    }

    public LifecycleNode getTarget() {
        return target;
    }

    private final LifecycleNode target;

}
