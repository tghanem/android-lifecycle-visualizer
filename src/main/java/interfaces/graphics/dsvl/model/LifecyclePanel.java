package interfaces.graphics.dsvl.model;

import com.intellij.openapi.components.ServiceManager;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.graphics.dsvl.ILifecycleNodeFactory;

import javax.swing.*;
import java.awt.*;
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

        ILifecycleNodeFactory lifecycleNodeFactory =
                ServiceManager.getService(ILifecycleNodeFactory.class);

        LifecycleHandlerNode onCreate =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onCreate",
                        repaint);

        onCreate.setVisible(true);

        LifecycleHandlerNode onStart =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onStart",
                        repaint);

        onCreate.addNextNode(0, onStart);

        LifecycleHandlerNode onResume =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onResume",
                        repaint);

        onStart.addNextNode(0, onResume);

        LifecycleHandlerNode onPause =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onPause",
                        repaint);

        onResume.addNextNode(0, onPause);

        LifecycleHandlerNode onStop =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onStop",
                        repaint);

        onPause.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleHandlerNode(
                        metadata.getActivityClass(),
                        onResume));

        onPause.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleHandlerNode(
                        metadata.getActivityClass(),
                        onCreate));

        onPause.addNextNode(0, onStop);

        LifecycleHandlerNode onRestart =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onRestart",
                        repaint);

        onRestart.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleHandlerNode(
                        metadata.getActivityClass(),
                        onStart));

        LifecycleHandlerNode onDestroy =
                buildLifecycleHandlerNode(
                        lifecycleNodeFactory,
                        metadata,
                        "onDestroy",
                        repaint);

        onStop.addNextNode(0, onRestart);
        onStop.addNextNode(0, onDestroy);

        onStop.addNextNode(
                0,
                lifecycleNodeFactory.createCircularLifecycleHandlerNode(
                        metadata.getActivityClass(),
                        onCreate));

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
            ILifecycleNodeFactory lifecycleNodeFactory,
            ActivityMetadataToRender metadata,
            String handlerName,
            Consumer<LifecycleNode> repaint) {

        Optional<LifecycleEventHandler> handler =
                findByName(metadata.getHandlers(), handlerName);

        LifecycleHandlerNode node =
                lifecycleNodeFactory.createLifecycleHandlerNode(
                        metadata.getActivityClass(),
                        handlerName,
                        handler,
                        lifecycleHandlerNode -> repaint.accept(lifecycleHandlerNode));

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addNextNode(
                        lifecycleNodeFactory.createResourceAcquisitionLifecycleNode(resourceAcquisition));
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.addNextNode(
                        lifecycleNodeFactory.createResourceReleaseLifecycleNode(resourceRelease));
            }
        }

        return node;
    }

    private Optional<LifecycleHandlerNode> graphRoot;
    private HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
}