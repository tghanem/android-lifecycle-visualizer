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
        this.setVisible(false);
    }

    public List<LifecycleNode> getChildren() {
        return children;
    }

    public void addChild(LifecycleNode value) {
        children.add(value);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!subtreeVisible) {
            for (LifecycleNode child : children) {
                child.setVisible(true);
            }
            subtreeVisible = true;
        } else {
            for (LifecycleNode child : children) {
                toggleVisibility(child, false);
            }
            subtreeVisible = false;
        }
        super.actionPerformed(actionEvent);
    }

    private void toggleVisibility(LifecycleNode node, boolean visibility) {
        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode lifecycleHandlerNode = (LifecycleHandlerNode)node;
            for (LifecycleNode child : lifecycleHandlerNode.getChildren()) {
                toggleVisibility(child, visibility);
            }
            lifecycleHandlerNode.subtreeVisible = false;
        }
        node.setVisible(visibility);
    }

    private boolean subtreeVisible;

    private final List<LifecycleNode> children;
    private final Optional<LifecycleEventHandler> handler;
}
