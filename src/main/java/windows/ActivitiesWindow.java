package windows;

import com.intellij.openapi.project.Project;
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
        DefaultTreeModel model = (DefaultTreeModel) lifecycleComponents.getModel();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();

        processChildElements(
                instance.getDocumentElement(),
                componentElement -> {
                    DefaultMutableTreeNode componentElementTreeNode =
                            new DefaultMutableTreeNode(componentElement.getAttribute("Name"));

                    processChildElements(
                            componentElement,
                            callbackElement -> {
                                DefaultMutableTreeNode callbackElementTreeNode =
                                        new DefaultMutableTreeNode(componentElement.getAttribute("Name"));

                                componentElementTreeNode.add(callbackElementTreeNode);
                            });

                    root.add(componentElementTreeNode);
                });

        model.reload();

        content.removeAll();
        content.add(lifecycleComponents);
        content.revalidate();
        content.repaint();
    }

    private void processChildElements(Element element, Consumer<Element> processElement) {
        NodeList childNodeList = element.getChildNodes();

        for (int i = 0; i < childNodeList.getLength(); i++) {
            Node node = childNodeList.item(i);

            if (node instanceof Element) {
                processElement.accept((Element) node);
            }
        }
    }

    private void Render(Exception exception) {
        try {
            exceptionInformation.setText(getExceptionInformation(exception));

            content.removeAll();
            content.add(exceptionInformation);
            content.revalidate();
            content.repaint();
        } catch (Exception fatalException) {
            notificationController.Notify(fatalException);
        }
    }

    private String getExceptionInformation(Exception exception) {
        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().toString() + ": " + exception.getMessage());
        sb.append(System.lineSeparator());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw.toString());

        return sb.toString();
    }
}
