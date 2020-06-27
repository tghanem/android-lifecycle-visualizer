package windows;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class ActivitiesWindow {
    private JPanel contentPanel;
    private JTree components;

    public ActivitiesWindow() {
    }

    public JPanel getContent() {
        return contentPanel;
    }

    private void createUIComponents() {

        DefaultMutableTreeNode root =
                new DefaultMutableTreeNode("Main Activity");

        root.add(new DefaultMutableTreeNode("onCreate"));
        root.add(new DefaultMutableTreeNode("onResume"));
        root.add(new DefaultMutableTreeNode("onStop"));

        components = new JTree(root);
    }
}
