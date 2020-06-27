package windows;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import impl.Helper;
import interfaces.ILifecycleProcessor;
import interfaces.INotificationController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ActivitiesWindow {
    private JPanel mainPanel;
    private JPanel controls;
    private JPanel content;
    private JButton refresh;

    private JTree lifecycleComponents;
    private JTextArea exceptionInformation;

    private final Project project;
    private final ILifecycleProcessor processor;
    private final INotificationController notificationController;

    public ActivitiesWindow(
            Project project,
            ILifecycleProcessor processor,
            INotificationController notificationController) {

        exceptionInformation = new JTextArea();

        refresh.addActionListener(
                actionEvent -> CompletableFuture.runAsync(
                        () -> {
                            try {
                                refresh.setEnabled(false);
                                Render(processor.Process(project));
                            } catch (Exception exception) {
                                Render(exception);
                            } finally {
                                refresh.setEnabled(true);
                            }
                        }));

        this.project = project;
        this.processor = processor;
        this.notificationController = notificationController;
    }

    public JPanel getContent() {
        return mainPanel;
    }

    private void Render(Document instance) {
        DefaultMutableTreeNode newRoot =
                new DefaultMutableTreeNode("Lifecycle Components");

        Helper.processChildElements(
                instance.getDocumentElement(),
                componentElement -> {
                    DefaultMutableTreeNode componentElementTreeNode =
                            new DefaultMutableTreeNode(componentElement.getAttribute("Name"));

                    Helper.processChildElements(
                            componentElement,
                            callbackElement -> {
                                DefaultMutableTreeNode callbackElementTreeNode =
                                        new DefaultMutableTreeNode(componentElement.getAttribute("Name"));

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
        ApplicationManager.getApplication().invokeLater(
                () -> {
                    try {
                        exceptionInformation.setText(Helper.getExceptionInformation(exception));

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
