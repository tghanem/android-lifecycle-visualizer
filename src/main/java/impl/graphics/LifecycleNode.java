package impl.graphics;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class LifecycleNode extends JButton implements ActionListener {
    public LifecycleNode(String name, Runnable repaint) {
        super(name);

        this.name = name;
        this.repaint = repaint;
        this.addActionListener(this);
    }

    public boolean getShouldShow() {
        return shouldShow;
    }

    public void setShouldShow(boolean value) {
        shouldShow = value;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        shouldShow = !shouldShow;
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

    private boolean shouldShow;

    private final String name;
    private final Runnable repaint;
}
