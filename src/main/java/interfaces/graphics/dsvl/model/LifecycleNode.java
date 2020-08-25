package interfaces.graphics.dsvl.model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class LifecycleNode extends JButton {
    public LifecycleNode(Consumer<LifecycleNode> paintNode, String name) {
        super(name);

        this.paintNode = paintNode;
        this.name = name;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        paintNode.accept(this);
        super.paintComponent(graphics);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        LifecycleNode that = (LifecycleNode) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    protected final String name;
    private final Consumer<LifecycleNode> paintNode;
}
