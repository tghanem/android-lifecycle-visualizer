package windows;

import impl.graphics.LifecyclePanel;
import impl.model.dstl.LifecycleEventHandler;
import interfaces.IActivityViewProvider;

import javax.swing.*;
import java.util.List;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;
    private LifecyclePanel panel;

    @Override
    public void display(List<LifecycleEventHandler> handlers) {
        panel.populate(handlers);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {
        panel = new LifecyclePanel();
        mainPanel = new JPanel();
        mainPanel.add(panel);
    }
}
