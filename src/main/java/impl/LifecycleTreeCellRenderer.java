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

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) o;
        Object userObject = treeNode.getUserObject();

        if (userObject instanceof Element) {
            Element userObjectElement = (Element) userObject;

            JLabel label;

            if (treeNode.getParent() == null) {
                label = new JLabel(userObjectElement.getAttribute("ApplicationName"));
            } else {
                label = new JLabel(userObjectElement.getAttribute("Name"));
            }

            label.setIcon(
                    new ImageIcon(
                            getClass()
                                    .getClassLoader()
                                    .getResource(userObjectElement.getAttribute("Icon"))));

            return label;
        } else {
            return new JLabel(userObject.toString());
        }
    }
}
