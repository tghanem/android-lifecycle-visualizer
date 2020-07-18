package impl.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class LifecyclePanel extends JPanel {
    public LifecyclePanel() {
        levelNodes = Optional.empty();
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

        levelNodes = Optional.of(getLevelNodes(root));
    }

    private void processNodeClicked(
            LifecycleNode node) {

        if (node instanceof LifecycleHandlerNode) {
            LifecycleHandlerNode lifecycleHandlerNode =
                    (LifecycleHandlerNode)node;

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
            LifecycleHandlerNode lifecycleHandlerNode = (LifecycleHandlerNode)node;
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

        if (!levelNodes.isPresent()) {
            return;
        }

        removeAll();
        setLayout(null);
        paintComponent((Graphics2D) graphics);
    }

    private void paintComponent(
            Graphics2D graphics) {

        HashSet<LifecycleNode> drawnNodes =
                new HashSet<>();

        Insets insets = getInsets();

        List<List<LifecycleNode>> rows = levelNodes.get();

        int topMargin = insets.top + 50;

        for (int i = 0; i < rows.size(); i++) {
            int leftMargin = insets.left + 200;

            for (int j = 0; j < rows.get(i).size(); j++) {
                LifecycleNode node = rows.get(i).get(j);

                if (drawnNodes.contains(node)) {
                    continue;
                }

                Dimension nodeSize = node.getPreferredSize();

                add(node);

                node.setBounds(
                        leftMargin,
                        topMargin,
                        100,
                        30);

                drawnNodes.add(node);

                leftMargin += nodeSize.width + 50;
            }

            topMargin +=
                    rows
                            .get(i)
                            .get(0)
                            .getPreferredSize()
                            .height + 50;
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

    private List<List<LifecycleNode>> getLevelNodes(
            LifecycleNode node) {

        HashSet<LifecycleNode> countedNodes =
                new HashSet<>();

        List<List<LifecycleNode>> levelNodes =
                new ArrayList<>();

        levelNodes.add(Arrays.asList(node));

        traverseGetLevelNodes(
                Arrays.asList(node),
                countedNodes,
                levelNodes);

        return levelNodes;
    }

    private void traverseGetLevelNodes(
            List<LifecycleNode> nodes,
            HashSet<LifecycleNode> countedNodes,
            List<List<LifecycleNode>> levelNodes) {

        List<LifecycleNode> nextLevelNodes =
                new ArrayList<>();

        for (LifecycleNode node : nodes) {
            if (!countedNodes.contains(node)) {
                if (node instanceof LifecycleHandlerNode) {
                    for (LifecycleNode child : ((LifecycleHandlerNode)node).getChildren()) {
                        nextLevelNodes.add(child);
                    }
                }
                countedNodes.add(node);
            }
        }

        if (nextLevelNodes.size() > 0) {
            levelNodes.add(nextLevelNodes);

            traverseGetLevelNodes(
                    nextLevelNodes,
                    countedNodes,
                    levelNodes);
        }
    }

    private Optional<List<List<LifecycleNode>>> levelNodes;
    private HashMap<LifecycleNode, Boolean> subtreeVisibleHashMap;
}
