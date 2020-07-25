package impl.graphics;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiMethod;
import impl.Helper;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.IActivityFileModifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class LifecyclePanel extends JPanel {
    public LifecyclePanel() {
        graphRoot = Optional.empty();
        subtreeVisibleHashMap = new HashMap<>();
    }

    public void populate(
            ActivityMetadataToRender metadata) {

        subtreeVisibleHashMap.clear();

        LifecycleHandlerNode root =
                buildLifecycleGraph(
                        metadata,
                        node -> {
                            processNodeClicked(node);
                            revalidate();
                            repaint();
                        });

        graphRoot = Optional.of(root);
    }

    private void processNodeClicked(
            LifecycleNode node) {

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode lifecycleHandlerNode =
                    (LifecycleHandlerNode) node;

            Boolean subtreeVisible =
                    subtreeVisibleHashMap.get(node);

            if (!subtreeVisible) {
                for (LifecycleNode nextNode : lifecycleHandlerNode.getNextNodes()) {
                    nextNode.setVisible(true);
                }
                subtreeVisibleHashMap.replace(node, true);
            } else {
                for (LifecycleNode nextNode : lifecycleHandlerNode.getNextNodes()) {
                    setNodeVisibility(nextNode, false);
                }
                subtreeVisibleHashMap.replace(node, false);
            }
        }
    }

    private void setNodeVisibility(
            LifecycleNode node,
            boolean visibility) {

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode lifecycleHandlerNode = (LifecycleHandlerNode) node;
            for (LifecycleNode nextNode : lifecycleHandlerNode.getNextNodes()) {
                setNodeVisibility(nextNode, visibility);
            }
            subtreeVisibleHashMap.replace(node, false);
        }

        node.setVisible(visibility);
    }

    @Override
    protected void paintComponent(
            Graphics graphics) {

        super.paintComponent(graphics);

        if (!graphRoot.isPresent()) {
            return;
        }

        removeAll();
        setLayout(null);

        List<List<LifecycleNode>> rows =
                new ArrayList<>();

        graphRoot.get().traverse(
                (level, n) -> {
                    if (level < rows.size()) {
                        rows.get(level).add(n);
                    } else {
                        rows.add(new ArrayList<>(Arrays.asList(n)));
                    }
                });

        int maxCount = 0;

        for (List<LifecycleNode> row : rows) {
            if (row.size() > maxCount) {
                maxCount = row.size();
            }
        }

        double canvasHeight = getHeight();
        double canvasWidth = getWidth();

        double nodeAreaHeight = canvasHeight / rows.size();
        double nodeAreaWidth = canvasWidth / maxCount;

        double topMargin = 0;

        for (int i = 0; i < rows.size(); i++) {
            double rowInternalPadding =
                    (canvasWidth - (nodeAreaWidth * rows.get(i).size())) / (rows.get(i).size() + 1);

            double leftMargin = rowInternalPadding;

            for (int j = 0; j < rows.get(i).size(); j++) {
                LifecycleNode lifecycleNode = rows.get(i).get(j);

                add(lifecycleNode);

                double nodeWidth = nodeAreaWidth * 0.5;
                double nodeHeight = nodeAreaHeight * 0.5;
                double nodeLeftMargin = leftMargin + nodeWidth * 0.5;
                double nodeTopMargin = topMargin + nodeHeight * 0.5;

                lifecycleNode
                        .setBounds(
                                (int) nodeLeftMargin,
                                (int) nodeTopMargin,
                                (int) nodeWidth,
                                (int) nodeHeight);

                leftMargin += nodeAreaWidth + rowInternalPadding;
            }

            topMargin += nodeAreaHeight;
        }

        Graphics2D g2 = (Graphics2D) graphics;

        for (List<LifecycleNode> row : rows) {
            for (LifecycleNode node : row) {
                if (node instanceof LifecycleHandlerNode) {
                    LifecycleHandlerNode handlerNode = (LifecycleHandlerNode) node;

                    for (LifecycleNode nextNode : handlerNode.getNextNodes()) {
                        if (nextNode.isVisible()) {
                            drawNonCircularLine(
                                    g2,
                                    node.getBounds(),
                                    nextNode.getBounds());
                        }
                    }
                }
            }
        }
    }

    private void drawNonCircularLine(
            Graphics2D graphics,
            Rectangle source,
            Rectangle target) {

        Path2D path = new Path2D.Double();
        path.moveTo(source.x + source.width / 2.0, source.y);
        path.lineTo(target.x + target.width / 2.0, target.y);
        path.closePath();

        graphics.draw(path);
    }

    private LifecycleHandlerNode buildLifecycleGraph(
            ActivityMetadataToRender metadata,
            Consumer<LifecycleNode> repaint) {

        LifecycleHandlerNode onCreate =
                buildLifecycleHandlerNode(metadata, "onCreate", repaint);

        onCreate.setVisible(true);

        LifecycleHandlerNode onStart =
                buildLifecycleHandlerNode(metadata, "onStart", repaint);

        onCreate.addNextNode(0, onStart);

        LifecycleHandlerNode onResume =
                buildLifecycleHandlerNode(metadata, "onResume", repaint);

        onStart.addNextNode(0, onResume);

        LifecycleHandlerNode onPause =
                buildLifecycleHandlerNode(metadata, "onPause", repaint);

        onResume.addNextNode(0, onPause);

        LifecycleHandlerNode onStop =
                buildLifecycleHandlerNode(metadata, "onStop", repaint);

        onPause.addNextNode(0, new CircularLifecycleNode(onResume));
        onPause.addNextNode(0, new CircularLifecycleNode(onCreate));
        onPause.addNextNode(0, onStop);

        LifecycleHandlerNode onRestart =
                buildLifecycleHandlerNode(metadata, "onRestart", repaint);

        LifecycleHandlerNode onDestroy =
                buildLifecycleHandlerNode(metadata, "onDestroy", repaint);

        onStop.addNextNode(0, onRestart);
        onStop.addNextNode(0, onDestroy);
        onStop.addNextNode(0, new CircularLifecycleNode(onCreate));

        subtreeVisibleHashMap.put(onCreate, false);
        subtreeVisibleHashMap.put(onStart, false);
        subtreeVisibleHashMap.put(onResume, false);
        subtreeVisibleHashMap.put(onPause, false);
        subtreeVisibleHashMap.put(onStop, false);
        subtreeVisibleHashMap.put(onRestart, false);
        subtreeVisibleHashMap.put(onDestroy, false);

        return onCreate;
    }

    private Optional<LifecycleEventHandler> findByName(
            List<LifecycleEventHandler> handlers,
            String handlerName) {

        for (LifecycleEventHandler handler : handlers) {
            if (handler.getPsiElement().getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    private LifecycleHandlerNode buildLifecycleHandlerNode(
            ActivityMetadataToRender metadata,
            String handlerName,
            Consumer<LifecycleNode> repaint) {

        Optional<LifecycleEventHandler> handler =
                findByName(metadata.getHandlers(), handlerName);

        LifecycleHandlerNode node =
                new LifecycleHandlerNode(handler, handlerName);

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addNextNode(
                        new ResourceAcquisitionLifecycleNode(
                                resourceAcquisition.getPsiElement().getText(),
                                resourceAcquisition));
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.addNextNode(
                        new ResourceReleaseLifecycleNode(
                                resourceRelease.getPsiElement().getText(),
                                resourceRelease));
            }
        }

        node.addActionListener(n -> repaint.accept(node));

        node.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                            JPopupMenu menu = new JPopupMenu();

                            if (!node.getHandler().isPresent()) {
                                menu.add(
                                        new JMenuItem(
                                                new AbstractAction("Add Handler") {
                                                    @Override
                                                    public void actionPerformed(ActionEvent actionEvent) {
                                                        PsiMethod handlerElement =
                                                                ServiceManager
                                                                        .getService(IActivityFileModifier.class)
                                                                        .createAndAddLifecycleHandlerMethod(
                                                                                metadata.getActivityClass(),
                                                                                handlerName);

                                                        node.setHandler(
                                                                new LifecycleEventHandler(
                                                                        handlerElement,
                                                                        new ArrayList<>(),
                                                                        new ArrayList<>()));

                                                        Helper.navigateTo(handlerElement);
                                                    }
                                                }));
                            } else {
                                menu.add(
                                        new JMenuItem(
                                                new AbstractAction("Go To Handler") {
                                                    @Override
                                                    public void actionPerformed(ActionEvent actionEvent) {
                                                        Helper.navigateTo(handler.get().getPsiElement());
                                                    }
                                                }));
                            }

                            menu.show(
                                    mouseEvent.getComponent(),
                                    mouseEvent.getX(),
                                    mouseEvent.getY());
                        }
                    }
                });

        return node;
    }

    private Optional<LifecycleHandlerNode> graphRoot;
    private HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
}