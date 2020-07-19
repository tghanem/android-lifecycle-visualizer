package impl.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class LifecyclePanel extends JPanel {
    public LifecyclePanel() {
        graphRoot = Optional.empty();
        subtreeVisibleHashMap = new HashMap<>();
    }

    public void populate(
            LifecycleHandlerCollection handlers) {

        subtreeVisibleHashMap.clear();

        LifecycleHandlerNode root =
                buildLifecycleGraph(
                        handlers,
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
                for (LifecycleLink link : lifecycleHandlerNode.getNonCircularLinks()) {
                    link.getTarget().setVisible(true);
                }
                subtreeVisibleHashMap.replace(node, true);
            } else {
                for (LifecycleLink link : lifecycleHandlerNode.getNonCircularLinks()) {
                    setNodeVisibility(link.getTarget(), false);
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
            for (LifecycleLink link : lifecycleHandlerNode.getNonCircularLinks()) {
                setNodeVisibility(link.getTarget(), visibility);
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

                    for (LifecycleLink link : handlerNode.getLinks()) {
                        if (link.getTarget().isVisible()) {
                            if (!link.isCircular()) {
                                drawNonCircularLine(
                                        g2,
                                        node.getBounds(),
                                        link.getTarget().getBounds());
                            }
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
            LifecycleHandlerCollection handlers,
            Consumer<LifecycleNode> repaint) {

        LifecycleHandlerNode onCreate =
                handlers.buildLifecycleHandlerNode("onCreate", repaint);

        onCreate.setVisible(true);

        LifecycleHandlerNode onStart =
                handlers.buildLifecycleHandlerNode("onStart", repaint);

        onCreate.addLink(0, onStart, false);

        LifecycleHandlerNode onResume =
                handlers.buildLifecycleHandlerNode("onResume", repaint);

        onStart.addLink(0, onResume, false);

        LifecycleHandlerNode onPause =
                handlers.buildLifecycleHandlerNode("onPause", repaint);

        onResume.addLink(0, onPause, false);

        LifecycleHandlerNode onStop =
                handlers.buildLifecycleHandlerNode("onStop", repaint);

        onPause.addLink(0, onResume, true);
        onPause.addLink(0, onStop, false);
        onPause.addLink(0, onCreate, true);

        LifecycleHandlerNode onRestart =
                handlers.buildLifecycleHandlerNode("onRestart", repaint);

        LifecycleHandlerNode onDestroy =
                handlers.buildLifecycleHandlerNode("onDestroy", repaint);

        onStop.addLink(0, onRestart, false);
        onStop.addLink(0, onDestroy, false);
        onStop.addLink(0, onCreate, true);

        subtreeVisibleHashMap.put(onCreate, false);
        subtreeVisibleHashMap.put(onStart, false);
        subtreeVisibleHashMap.put(onResume, false);
        subtreeVisibleHashMap.put(onPause, false);
        subtreeVisibleHashMap.put(onStop, false);
        subtreeVisibleHashMap.put(onRestart, false);
        subtreeVisibleHashMap.put(onDestroy, false);

        return onCreate;
    }

    private Optional<LifecycleHandlerNode> graphRoot;
    private HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
}