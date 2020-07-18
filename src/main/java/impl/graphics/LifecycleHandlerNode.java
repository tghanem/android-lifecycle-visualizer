package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LifecycleHandlerNode extends LifecycleNode {
    public LifecycleHandlerNode(Optional<LifecycleEventHandler> handler, String name) {
        super(name);

        this.handler = handler;
        this.children = new ArrayList<>();
        this.setVisible(false);
    }

    public List<LifecycleNode> getChildren() {
        return children;
    }

    public void addChild(LifecycleNode value) {
        children.add(value);
    }

    private final List<LifecycleNode> children;
    private final Optional<LifecycleEventHandler> handler;
}
