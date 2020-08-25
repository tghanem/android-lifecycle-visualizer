package interfaces.graphics.dsvl.model;

import interfaces.consumers.LifecycleNodeConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CallbackMethodNode extends LifecycleNode {
    public CallbackMethodNode(Consumer<LifecycleNode> paintNode, String name) {
        super(paintNode, name);

        this.nextNodes = new ArrayList<>();
    }

    public List<LifecycleNode> getNextNodes() {
        return nextNodes;
    }

    public void addNextNode(LifecycleNode value) {
        nextNodes.add(value);
    }

    public void addNextNode(int index, LifecycleNode value) {
        nextNodes.add(index, value);
    }

    public void traverse(LifecycleNodeConsumer consumer) {
        traverseInternal(0, this, consumer);
    }

    private void traverseInternal(
            int depth,
            LifecycleNode node,
            LifecycleNodeConsumer processNode) {

        processNode.accept(depth, node);

        if (node instanceof CallbackMethodNode) {
            CallbackMethodNode handlerNode = (CallbackMethodNode) node;

            for (LifecycleNode link : handlerNode.getNextNodes()) {
                traverseInternal(depth + 1, link, processNode);
            }
        }
    }

    private final List<LifecycleNode> nextNodes;
}
