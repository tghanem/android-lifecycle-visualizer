package impl.services;

import com.intellij.psi.PsiClass;
import com.intellij.util.Producer;
import interfaces.graphics.dsvl.model.*;
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
import java.util.function.Function;

public class LifecycleNodeFactory implements ILifecycleNodeFactory {
    @Override
    public CallbackMethodNode createCallbackMethodNode(
            PsiClass ownerActivityClass,
            String callbackMethodName,
            Optional<CallbackMethod> callbackMethod,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<CallbackMethodNode> onClick,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        CallbackMethodNode node = new CallbackMethodNode(paintNode, callbackMethodName);

        node.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onClick.accept(node);
                    }
                });

        attachToMouseRightClick(
                () ->
                        createCallbackMethodNodeMenu(
                                node,
                                nodeHasUnderlyingCallbackMethod,
                                goToNode,
                                onAddCallbackMethod),
                node);

        return node;
    }

    @Override
    public CircularLifecycleNode createCircularLifecycleNode(
            PsiClass ownerActivityClass,
            CallbackMethodNode targetCallbackMethodNode,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        CircularLifecycleNode node = new CircularLifecycleNode(paintNode, targetCallbackMethodNode);

        attachToMouseRightClick(
                () ->
                        createCallbackMethodNodeMenu(
                                targetCallbackMethodNode,
                                nodeHasUnderlyingCallbackMethod,
                                goToNode,
                                onAddCallbackMethod),
                node);

        return node;
    }

    @Override
    public ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition,
            Consumer<LifecycleNode> paintNode,
            Consumer<ResourceAcquisitionLifecycleNode> goToResource) {

        ResourceAcquisitionLifecycleNode resourceAcquisitionNode =
                new ResourceAcquisitionLifecycleNode(paintNode, resourceAcquisition);

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
            Consumer<LifecycleNode> paintNode,
            Consumer<ResourceReleaseLifecycleNode> goToResource) {

        ResourceReleaseLifecycleNode resourceReleaseNode =
                new ResourceReleaseLifecycleNode(paintNode, resourceRelease);

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
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod) {

        HashMap<String, Runnable> menuItems = new HashMap<>();

        if (nodeHasUnderlyingCallbackMethod.apply(node)) {
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
