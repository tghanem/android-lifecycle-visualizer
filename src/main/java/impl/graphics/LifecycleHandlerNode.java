package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LifecycleHandlerNode extends LifecycleNode {
    public LifecycleHandlerNode(Optional<LifecycleEventHandler> handler, String name) throws IOException {
        super(name);

        this.handler = handler;
        this.nextNodes = new ArrayList<>();

        if (!handler.isPresent()) {
            this.setForeground(Color.GRAY);
            this.setBackground(Color.LIGHT_GRAY);
        }

        this.setVisible(false);
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("handler.png")));
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

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode handlerNode = (LifecycleHandlerNode) node;

            for (LifecycleNode link : handlerNode.getNextNodes()) {
                traverseInternal(depth + 1, link, processNode);
            }
        }
    }

    private final List<LifecycleNode> nextNodes;
    private final Optional<LifecycleEventHandler> handler;
}
