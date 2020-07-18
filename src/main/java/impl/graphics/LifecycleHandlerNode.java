package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LifecycleHandlerNode extends LifecycleNode {
    public LifecycleHandlerNode(Optional<LifecycleEventHandler> handler, String name) {
        super(name);

        this.handler = handler;
        this.links = new ArrayList<>();
        this.setVisible(false);
    }

    public List<LifecycleLink> getLinks() {
        return links;
    }

    public List<LifecycleLink> getNonCircularLinks() {
        List<LifecycleLink> result = new ArrayList<>();
        for (LifecycleLink link : links) {
            if (!link.isCircular()) {
                result.add(link);
            }
        }
        return result;
    }

    public void addLink(LifecycleNode node, boolean isCircular) {
        addLink(new LifecycleLink(node, isCircular));
    }

    public void addLink(LifecycleLink value) {
        links.add(value);
    }

    public void traverse(LifecycleNodeConsumer consumer) {
        traverseInternal(0, this, consumer);
    }

    private void traverseInternal(
            int depth,
            LifecycleNode node,
            LifecycleNodeConsumer processNode) {

        processNode.accept(depth, node);

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode handlerNode = (LifecycleHandlerNode) node;

            for (LifecycleLink link : handlerNode.getLinks()) {
                if (!link.isCircular()) {
                    traverseInternal(
                            depth + 1,
                            link.getTarget(),
                            processNode);
                }
            }
        }
    }

    private final List<LifecycleLink> links;
    private final Optional<LifecycleEventHandler> handler;
}
