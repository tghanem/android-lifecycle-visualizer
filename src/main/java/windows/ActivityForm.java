package windows;

import interfaces.graphics.dsvl.model.ActivityMetadataToRender;
import interfaces.graphics.dsvl.model.LifecyclePanel;
import interfaces.graphics.dsvl.IActivityViewProvider;

import javax.swing.*;

public class ActivityForm implements IActivityViewProvider {
    private JPanel mainPanel;

    private JPanel navigatorPanel;
    private LifecyclePanel lifecyclePanel;

    @Override
    public void display(ActivityMetadataToRender metadata) {
        lifecyclePanel.populate(metadata);
        lifecyclePanel.revalidate();
        lifecyclePanel.repaint();
    }

    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {
        lifecyclePanel = new LifecyclePanel();
        mainPanel = new JPanel();
        mainPanel.add(lifecyclePanel);
    }
}
