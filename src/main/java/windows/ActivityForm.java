package windows;

import impl.graphics.ModelPanel;
import impl.model.dstl.LifecycleEventHandler;
import interfaces.IActivityViewProvider;

import javax.swing.*;
import java.util.List;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;
    private ModelPanel panel;

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
        panel = new ModelPanel();
        mainPanel = new JPanel();
        mainPanel.add(panel);
    }
}
