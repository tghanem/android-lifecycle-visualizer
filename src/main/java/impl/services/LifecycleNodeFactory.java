package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import impl.graphics.CircularLifecycleNode;
import impl.graphics.LifecycleHandlerNode;
import impl.graphics.ResourceAcquisitionLifecycleNode;
import impl.graphics.ResourceReleaseLifecycleNode;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.IActivityFileModifier;
import interfaces.ILifecycleNodeFactory;

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
    public LifecycleHandlerNode createLifecycleHandlerNode(
            PsiClass ownerActivityClass,
            String handlerName,
            Optional<LifecycleEventHandler> eventHandler,
            Consumer<LifecycleHandlerNode> onClick) {

        LifecycleHandlerNode node =
                new LifecycleHandlerNode(eventHandler, handlerName);

        node.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        onClick.accept(node);
                    }
                });

        attachToMouseRightClick(
                createLifecycleHandlerNodeMenu(
                        ownerActivityClass,
                        node),
                node);

        return node;
    }

    @Override
    public CircularLifecycleNode createCircularLifecycleHandlerNode(
            PsiClass ownerActivityClass,
            LifecycleHandlerNode targetLifecycleHandlerNode) {

        CircularLifecycleNode node =
                new CircularLifecycleNode(targetLifecycleHandlerNode);

        attachToMouseRightClick(
                createLifecycleHandlerNodeMenu(ownerActivityClass, targetLifecycleHandlerNode),
                node);

        return node;
    }

    @Override
    public ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition) {

        ResourceAcquisitionLifecycleNode resourceAcquisitionNode =
                new ResourceAcquisitionLifecycleNode(
                        resourceAcquisition.getPsiElement().getText(),
                        resourceAcquisition);

        HashMap<String, Runnable> menuItems = new HashMap<>();

        menuItems.put(
                "Go To Line",
                () -> navigateTo(resourceAcquisition.getPsiElement()));

        attachToMouseRightClick(
                createPopupMenu(menuItems),
                resourceAcquisitionNode);

        return resourceAcquisitionNode;
    }

    @Override
    public ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease) {

        ResourceReleaseLifecycleNode resourceReleaseNode =
                new ResourceReleaseLifecycleNode(
                        resourceRelease.getPsiElement().getText(),
                        resourceRelease);

        HashMap<String, Runnable> menuItems = new HashMap<>();

        menuItems.put(
                "Go To Line",
                () -> navigateTo(resourceRelease.getPsiElement()));

        attachToMouseRightClick(
                createPopupMenu(menuItems),
                resourceReleaseNode);

        return resourceReleaseNode;
    }

    private JPopupMenu createLifecycleHandlerNodeMenu(
            PsiClass activityClass,
            LifecycleHandlerNode node) {

        HashMap<String, Runnable> menuItems =
                new HashMap<>();

        if (node.getHandler().isPresent()) {
            menuItems.put(
                    "Go To Handler",
                    () -> {
                        navigateTo(node.getHandler().get().getPsiElement());
                    });
        } else {
            menuItems.put(
                    "Add Handler",
                    () -> {
                        PsiMethod handlerElement =
                                ServiceManager
                                        .getService(IActivityFileModifier.class)
                                        .createAndAddLifecycleHandlerMethod(
                                                activityClass,
                                                node.getName());

                        node.setHandler(
                                new LifecycleEventHandler(
                                        handlerElement,
                                        new ArrayList<>(),
                                        new ArrayList<>()));

                        navigateTo(handlerElement);
                    });
        }

        return createPopupMenu(menuItems);
    }

    private void attachToMouseRightClick(JPopupMenu menu, JComponent component) {
        component.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                            menu.show(
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
