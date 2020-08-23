package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Producer;
import interfaces.graphics.dsvl.model.CircularLifecycleNode;
import interfaces.graphics.dsvl.model.CallbackMethodNode;
import interfaces.graphics.dsvl.model.ResourceAcquisitionLifecycleNode;
import interfaces.graphics.dsvl.model.ResourceReleaseLifecycleNode;
import impl.model.dstl.*;
import interfaces.IActivityFileModifier;
import interfaces.graphics.dsvl.ILifecycleNodeFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class LifecycleNodeFactory implements ILifecycleNodeFactory {
    @Override
    public CallbackMethodNode createCallbackMethodNode(
            PsiClass ownerActivityClass,
            String callbackMethodName,
            Optional<CallbackMethod> callbackMethod,
            Consumer<CallbackMethodNode> onClick) {

        CallbackMethodNode node =
                new CallbackMethodNode(callbackMethod, callbackMethodName);

        node.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onClick.accept(node);
                    }
                });

        attachToMouseRightClick(
                () -> createCallbackMethodNodeMenu(ownerActivityClass, node),
                node);

        return node;
    }

    @Override
    public CircularLifecycleNode createCircularLifecycleNode(
            PsiClass ownerActivityClass,
            CallbackMethodNode targetCallbackMethodNode) {

        CircularLifecycleNode node =
                new CircularLifecycleNode(targetCallbackMethodNode);

        attachToMouseRightClick(
                () -> createCallbackMethodNodeMenu(ownerActivityClass, targetCallbackMethodNode),
                node);

        return node;
    }

    @Override
    public ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition) {

        ResourceAcquisitionLifecycleNode resourceAcquisitionNode =
                new ResourceAcquisitionLifecycleNode(resourceAcquisition);

        attachToMouseRightClick(
                () -> {
                    HashMap<String, Runnable> menuItems = new HashMap<>();

                    menuItems.put(
                            "Go To Line",
                            () -> navigateTo(resourceAcquisition.getPsiElement()));

                    return createPopupMenu(menuItems);
                },
                resourceAcquisitionNode);

        return resourceAcquisitionNode;
    }

    @Override
    public ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease) {

        ResourceReleaseLifecycleNode resourceReleaseNode =
                new ResourceReleaseLifecycleNode(resourceRelease);

        attachToMouseRightClick(
                () -> {
                    HashMap<String, Runnable> menuItems = new HashMap<>();

                    menuItems.put(
                            "Go To Line",
                            () -> navigateTo(resourceRelease.getPsiElement()));

                    return createPopupMenu(menuItems);
                },
                resourceReleaseNode);

        return resourceReleaseNode;
    }

    private JPopupMenu createCallbackMethodNodeMenu(
            PsiClass activityClass,
            CallbackMethodNode node) {

        HashMap<String, Runnable> menuItems = new HashMap<>();

        if (node.getCallbackMethod().isPresent()) {
            menuItems.put(
                    "Go To Method",
                    () -> navigateTo(node.getCallbackMethod().get().getPsiElement()));
        } else {
            menuItems.put(
                    "Add Callback Method",
                    () -> {
                        PsiMethod callbackMethodPsiElement =
                                ServiceManager
                                        .getService(IActivityFileModifier.class)
                                        .createAndAddCallbackMethod(
                                                activityClass,
                                                node.getName());

                        node.setCallbackMethod(
                                new CallbackMethod(
                                        callbackMethodPsiElement,
                                        new ArrayList<>(),
                                        new ArrayList<>()));

                        navigateTo(callbackMethodPsiElement);
                    });
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

    private JPopupMenu createPopupMenu(HashMap<String, Runnable> menuItems) {
        JPopupMenu menu = new JPopupMenu();

        menuItems.forEach((itemName, onClickAction) -> {
            JMenuItem item = new JMenuItem(itemName);
            item.addActionListener(actionEvent -> onClickAction.run());
            menu.add(item);
        });

        return menu;
    }

    private void navigateTo(PsiElement element) {
        PsiElement navigationElement = element.getNavigationElement();

        if (navigationElement == null) {
            return;
        }

        if (navigationElement instanceof Navigatable) {
            Navigatable asNavigatable = (Navigatable) navigationElement;

            if (asNavigatable.canNavigate()) {
                asNavigatable.navigate(true);
            }
        }
    }
}
