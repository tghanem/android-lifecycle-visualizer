package impl.graphics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public abstract class LifecycleNode extends JButton implements ActionListener {
    public LifecycleNode(String name, Runnable repaint) {
        super(name);

        this.name = name;
        this.repaint = repaint;
        this.addActionListener(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        repaint.run();
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
    protected final Runnable repaint;
}
