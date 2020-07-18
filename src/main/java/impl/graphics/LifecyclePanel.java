package impl.graphics;

import javax.swing.*;
import java.awt.*;
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

        LifecycleNode root =
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
                for (LifecycleNode child : lifecycleHandlerNode.getChildren()) {
                    child.setVisible(true);
                }
                subtreeVisibleHashMap.replace(node, true);
            } else {
                for (LifecycleNode child : lifecycleHandlerNode.getChildren()) {
                    setNodeVisibility(child, false);
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
            for (LifecycleNode child : lifecycleHandlerNode.getChildren()) {
                setNodeVisibility(child, visibility);
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

        paintComponent(
                graphRoot.get(),
                new HashSet<>(),
                0,
                50,
                200);
    }

    private void paintComponent(
            LifecycleNode node,
            HashSet<LifecycleNode> drawnNodes,
            int nodeIndex,
            int topMargin,
            int leftMargin) {

        add(node);

        node.setBounds(
                leftMargin + nodeIndex * (100 + 50),
                topMargin,
                100,
                30);

        drawnNodes.add(node);

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode lifecycleHandlerNode = (LifecycleHandlerNode) node;
            List<LifecycleNode> children = lifecycleHandlerNode.getChildren();
            int effectiveColumnIndex = 0;

            for (int i = 0; i < children.size(); i++) {
                if (!drawnNodes.contains(children.get(i))) {
                    paintComponent(
                            children.get(i),
                            drawnNodes,
                            effectiveColumnIndex,
                            topMargin + 30 + 50,
                            leftMargin);

                    effectiveColumnIndex++;
                }
            }
        }
    }

    private LifecycleNode buildLifecycleGraph(
            LifecycleHandlerCollection handlers,
            Consumer<LifecycleNode> repaint) {

        LifecycleHandlerNode onCreate =
                handlers.buildLifecycleHandlerNode("onCreate", repaint);

        onCreate.setVisible(true);

        LifecycleHandlerNode onStart =
                handlers.buildLifecycleHandlerNode("onStart", repaint);

        onCreate.addChild(onStart);

        LifecycleHandlerNode onResume =
                handlers.buildLifecycleHandlerNode("onResume", repaint);

        onStart.addChild(onResume);

        LifecycleHandlerNode onPause =
                handlers.buildLifecycleHandlerNode("onPause", repaint);

        onResume.addChild(onPause);

        LifecycleHandlerNode onStop =
                handlers.buildLifecycleHandlerNode("onStop", repaint);

        onPause.addChild(
                handlers.buildLifecycleHandlerNode("onResume", repaint));

        onPause.addChild(onStop);

        onPause.addChild(
                handlers.buildLifecycleHandlerNode("onCreate", repaint));

        LifecycleHandlerNode onRestart =
                handlers.buildLifecycleHandlerNode("onRestart", repaint);

        LifecycleHandlerNode onDestroy =
                handlers.buildLifecycleHandlerNode("onDestroy", repaint);

        onStop.addChild(onRestart);

        onStop.addChild(onDestroy);

        onStop.addChild(
                handlers.buildLifecycleHandlerNode("onCreate", repaint));

        subtreeVisibleHashMap.put(onCreate, false);
        subtreeVisibleHashMap.put(onStart, false);
        subtreeVisibleHashMap.put(onResume, false);
        subtreeVisibleHashMap.put(onPause, false);
        subtreeVisibleHashMap.put(onStop, false);
        subtreeVisibleHashMap.put(onRestart, false);
        subtreeVisibleHashMap.put(onDestroy, false);

        return onCreate;
    }

    private Optional<LifecycleNode> graphRoot;
    private HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
}
