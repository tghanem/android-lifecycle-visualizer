package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
            List<LifecycleEventHandler> handlers) {

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
            List<LifecycleEventHandler> handlers,
            Consumer<LifecycleNode> repaint) {

        LifecycleHandlerNode onCreate =
                buildLifecycleHandlerNode(handlers, "onCreate", repaint);

        onCreate.setVisible(true);

        LifecycleHandlerNode onStart =
                buildLifecycleHandlerNode(handlers, "onStart", repaint);

        onCreate.addLink(0, onStart, false);

        LifecycleHandlerNode onResume =
                buildLifecycleHandlerNode(handlers, "onResume", repaint);

        onStart.addLink(0, onResume, false);

        LifecycleHandlerNode onPause =
                buildLifecycleHandlerNode(handlers, "onPause", repaint);

        onResume.addLink(0, onPause, false);

        LifecycleHandlerNode onStop =
                buildLifecycleHandlerNode(handlers, "onStop", repaint);

        onPause.addLink(0, onResume, true);
        onPause.addLink(0, onStop, false);
        onPause.addLink(0, onCreate, true);

        LifecycleHandlerNode onRestart =
                buildLifecycleHandlerNode(handlers, "onRestart", repaint);

        LifecycleHandlerNode onDestroy =
                buildLifecycleHandlerNode(handlers, "onDestroy", repaint);

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

    private Optional<LifecycleEventHandler> findByName(
            List<LifecycleEventHandler> handlers,
            String handlerName) {

        for (LifecycleEventHandler handler : handlers) {
            if (handler.getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    private LifecycleHandlerNode buildLifecycleHandlerNode(
            List<LifecycleEventHandler> handlers,
            String handlerName,
            Consumer<LifecycleNode> repaint) {

        Optional<LifecycleEventHandler> handler =
                findByName(handlers, handlerName);

        LifecycleHandlerNode node =
                new LifecycleHandlerNode(handler, handlerName);

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addLink(
                        new ResourceAcquisitionLifecycleNode(
                                resourceAcquisition.getResourceName(),
                                resourceAcquisition),
                        false);
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.addLink(
                        new ResourceReleaseLifecycleNode(
                                resourceRelease.getResourceName(),
                                resourceRelease),
                        false);
            }
        }

        node.addActionListener(n -> repaint.accept(node));

        node.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {
                        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                            JPopupMenu menu = new JPopupMenu();

                            if (!handler.isPresent()) {
                                menu.add(
                                        new JMenuItem(
                                                new AbstractAction("Add Handler") {
                                                    @Override
                                                    public void actionPerformed(ActionEvent actionEvent) {
                                                    }
                                                }));
                            } else {
                                menu.add(
                                        new JMenuItem(
                                                new AbstractAction("Go To Handler") {
                                                    @Override
                                                    public void actionPerformed(ActionEvent actionEvent) {
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