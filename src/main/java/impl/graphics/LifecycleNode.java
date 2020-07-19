package impl.graphics;

import javax.swing.*;
import java.util.Objects;

public abstract class LifecycleNode extends JButton {
    public LifecycleNode(String name) {
        super(name);
        this.name = name;
        setVisible(false);
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
}
