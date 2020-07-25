package windows;

import impl.graphics.ActivityMetadataToRender;
import impl.graphics.LifecyclePanel;
import interfaces.IActivityViewProvider;

import javax.swing.*;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;
    private LifecyclePanel panel;

    @Override
    public void display(ActivityMetadataToRender metadata) throws Exception {
        panel.populate(metadata);
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
