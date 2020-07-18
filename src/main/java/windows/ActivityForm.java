package windows;

import impl.graphics.LifecycleHandlerCollection;
import impl.graphics.LifecyclePanel;
import interfaces.IActivityViewProvider;

import javax.swing.*;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;
    private LifecyclePanel panel;

    @Override
    public void display(LifecycleHandlerCollection handlers) {
        panel.populate(handlers);
        panel.revalidate();
        panel.repaint();
    }

    public JPanel getContent() {
        return panel;
    }

    private void createUIComponents() {
        panel = new LifecyclePanel();
        mainPanel = new JPanel();
        mainPanel.add(panel);
    }
}
