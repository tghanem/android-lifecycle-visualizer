package impl.graphics;

import javax.swing.*;
import java.awt.*;

public class CircularLifecycleNode extends LifecycleNode {
    public CircularLifecycleNode(LifecycleNode target) {
        super(target.getName());

        this.target = target;
        this.setForeground(Color.GRAY);
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("circular_node.png")));
    }

    public LifecycleNode getTarget() {
        return target;
    }

    private final LifecycleNode target;
}
