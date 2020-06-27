package impl;

import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class LifecycleTreeCellRenderer implements TreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree,
            Object o,
            boolean b,
            boolean b1,
            boolean b2,
            int i,
            boolean b3) {

        Object userObject = ((DefaultMutableTreeNode) o).getUserObject();

        if (userObject instanceof Element) {
            Element userObjectElement = (Element) userObject;

            JLabel label = new JLabel(userObjectElement.getAttribute("Name"));

            label.setIcon(
                    new ImageIcon(
                            getClass()
                                    .getClassLoader()
                                    .getResource(userObjectElement.getAttribute("Icon"))));

            return label;
        }

        return new JLabel(userObject.toString());
    }
}
