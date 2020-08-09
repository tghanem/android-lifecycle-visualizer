package interfaces.graphics.dsvl.model;

import impl.model.dstl.CallbackMethod;
import interfaces.consumers.LifecycleNodeConsumer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CallbackMethodNode extends LifecycleNode {
    public CallbackMethodNode(Optional<CallbackMethod> handler, String name) {
        super(name);

        this.handler = handler;
        this.nextNodes = new ArrayList<>();

        if (!handler.isPresent()) {
            this.setForeground(Color.GRAY);
        }

        this.setVisible(false);
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("handler.png")));
    }

    public void setHandler(CallbackMethod value) {
        handler = Optional.of(value);

        this.setForeground(Color.BLACK);
    }

    public Optional<CallbackMethod> getHandler() {
        return handler;
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
    private Optional<CallbackMethod> handler;
}
