package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LifecycleHandlerNode extends LifecycleNode {
    public LifecycleHandlerNode(
            Optional<LifecycleEventHandler> handler,
            String name,
            Runnable repaint) {

        super(name, repaint);

        this.handler = handler;
        this.children = new ArrayList<>();
    }

    public List<LifecycleNode> getChildren() {
        return children;
    }

    public void addChild(LifecycleNode value) {
        children.add(value);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        for (LifecycleNode child : children) {
            child.setShouldShow(!child.getShouldShow());
        }
        super.actionPerformed(actionEvent);
    }

    private final List<LifecycleNode> children;
    private final Optional<LifecycleEventHandler> handler;
}
