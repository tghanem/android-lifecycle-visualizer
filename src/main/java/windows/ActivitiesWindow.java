package windows;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import impl.Helper;
import impl.LifecycleTreeCellRenderer;
import impl.exceptions.InformationalException;
import interfaces.ILifecycleComponentsProvider;
import interfaces.ILifecycleProcessor;
import interfaces.INotificationController;
import javafx.util.Pair;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ActivitiesWindow {
    private JPanel mainPanel;
    private JPanel content;
    private JPanel controls;
    private JButton refresh;

    private JTree lifecycleComponents;
    private JTextArea exceptionInformation;

    private final Project project;
    private final ILifecycleProcessor processor;
    private final ILifecycleComponentsProvider lifecycleComponentsProvider;
    private final INotificationController notificationController;

    public ActivitiesWindow(
            Project project,
            ILifecycleProcessor processor,
            ILifecycleComponentsProvider lifecycleComponentsProvider,
            INotificationController notificationController) {

        exceptionInformation = new JTextArea();

        refresh.addActionListener(
                actionEvent -> {
                    try {
                        List<Pair<String, VirtualFile>> lifecycleComponentsFiles =
                                lifecycleComponentsProvider.getLifecycleComponents(project);

                        CompletableFuture.runAsync(
                                () -> {
                                    try {
                                        refresh.setEnabled(false);
                                        Render(processor.Process(project.getName(), lifecycleComponentsFiles));
                                    } catch (Exception exception) {
                                        Render(exception);
                                    } finally {
                                        refresh.setEnabled(true);
                                    }
                                });
                    } catch (InformationalException e) {
                        Render(e.getMessage());
                    } catch (Exception e) {
                        Render(e);
                    }
                });

        lifecycleComponents.setCellRenderer(
                new LifecycleTreeCellRenderer());

        this.project = project;
        this.processor = processor;
        this.lifecycleComponentsProvider = lifecycleComponentsProvider;
        this.notificationController = notificationController;
    }

    public JPanel getContent() {
        return mainPanel;
    }

    private void Render(Document instance) {
        DefaultMutableTreeNode newRoot =
                new DefaultMutableTreeNode(instance.getDocumentElement());

        Helper.processChildElements(
                instance.getDocumentElement(),
                componentElement -> {
                    DefaultMutableTreeNode componentElementTreeNode =
                            new DefaultMutableTreeNode(componentElement);

                    Helper.processChildElements(
                            componentElement,
                            callbackElement -> {
                                DefaultMutableTreeNode callbackElementTreeNode =
                                        new DefaultMutableTreeNode(callbackElement);

                                componentElementTreeNode.add(callbackElementTreeNode);
                            });

                    newRoot.add(componentElementTreeNode);
                });

        ApplicationManager.getApplication().invokeLater(
                () -> {
                    DefaultTreeModel model = (DefaultTreeModel) lifecycleComponents.getModel();
                    model.setRoot(newRoot);
                    model.reload();

                    content.removeAll();
                    content.add(lifecycleComponents);
                    content.revalidate();
                    content.repaint();
                });
    }

    private void Render(Exception exception) {
        Render(Helper.getExceptionInformation(exception));
    }

    private void Render(String information) {
        ApplicationManager.getApplication().invokeLater(
                () -> {
                    try {
                        exceptionInformation.setText(information);

                        content.removeAll();
                        content.add(exceptionInformation);
                        content.revalidate();
                        content.repaint();
                    } catch (Exception fatalException) {
                        notificationController.Notify(fatalException);
                    }
                });
    }
}
