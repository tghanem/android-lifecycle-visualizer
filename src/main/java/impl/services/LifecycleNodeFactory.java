package impl.services;

import com.intellij.psi.PsiClass;
import com.intellij.util.Producer;
import interfaces.graphics.dsvl.model.CircularLifecycleNode;
import interfaces.graphics.dsvl.model.CallbackMethodNode;
import interfaces.graphics.dsvl.model.ResourceAcquisitionLifecycleNode;
import interfaces.graphics.dsvl.model.ResourceReleaseLifecycleNode;
import impl.model.dstl.*;
import interfaces.graphics.dsvl.ILifecycleNodeFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class LifecycleNodeFactory implements ILifecycleNodeFactory {
    @Override
    public CallbackMethodNode createCallbackMethodNode(
            PsiClass ownerActivityClass,
            String callbackMethodName,
            Optional<CallbackMethod> callbackMethod,
            Consumer<CallbackMethodNode> onClick,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        CallbackMethodNode node = new CallbackMethodNode(callbackMethod, callbackMethodName);

        node.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onClick.accept(node);
                    }
                });

        attachToMouseRightClick(
                () -> createCallbackMethodNodeMenu(node, goToNode, onAddCallbackMethod),
                node);

        return node;
    }

    @Override
    public CircularLifecycleNode createCircularLifecycleNode(
            PsiClass ownerActivityClass,
            CallbackMethodNode targetCallbackMethodNode,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        CircularLifecycleNode node = new CircularLifecycleNode(targetCallbackMethodNode);

        attachToMouseRightClick(
                () -> createCallbackMethodNodeMenu(targetCallbackMethodNode, goToNode, onAddCallbackMethod),
                node);

        return node;
    }

    @Override
    public ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition,
            Consumer<ResourceAcquisitionLifecycleNode> goToResource) {

        ResourceAcquisitionLifecycleNode resourceAcquisitionNode =
                new ResourceAcquisitionLifecycleNode(resourceAcquisition);

        attachToMouseRightClick(
                () -> {
                    HashMap<String, Runnable> menuItems = new HashMap<>();
                    menuItems.put("Go To Line", () -> goToResource.accept(resourceAcquisitionNode));
                    return createPopupMenu(menuItems);
                },
                resourceAcquisitionNode);

        return resourceAcquisitionNode;
    }

    @Override
    public ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease,
            Consumer<ResourceReleaseLifecycleNode> goToResource) {

        ResourceReleaseLifecycleNode resourceReleaseNode =
                new ResourceReleaseLifecycleNode(resourceRelease);

        attachToMouseRightClick(
                () -> {
                    HashMap<String, Runnable> menuItems = new HashMap<>();
                    menuItems.put("Go To Line", () -> goToResource.accept(resourceReleaseNode));
                    return createPopupMenu(menuItems);
                },
                resourceReleaseNode);

        return resourceReleaseNode;
    }

    private JPopupMenu createCallbackMethodNodeMenu(
            CallbackMethodNode node,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        HashMap<String, Runnable> menuItems = new HashMap<>();

        if (node.getCallbackMethod().isPresent()) {
            menuItems.put("Go To Method", () -> goToNode.accept(node));
        } else {
            menuItems.put("Add Callback Method", () -> onAddCallbackMethod.accept(node));
        }

        return createPopupMenu(menuItems);
    }

    private void attachToMouseRightClick(
            Producer<JPopupMenu> createMenu,
            JComponent component) {

        component.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                            createMenu.produce().show(
                                    mouseEvent.getComponent(),
                                    mouseEvent.getX(),
                                    mouseEvent.getY());
                        }
                    }
                });
    }

    private JPopupMenu createPopupMenu(
            HashMap<String, Runnable> menuItems) {

        JPopupMenu menu = new JPopupMenu();

        menuItems.forEach((itemName, onClickAction) -> {
            JMenuItem item = new JMenuItem(itemName);
            item.addActionListener(actionEvent -> onClickAction.run());
            menu.add(item);
        });

        return menu;
    }
}
