package impl.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class LifecyclePanel extends JPanel {
    public LifecyclePanel() {
        levelNodes = Optional.empty();
    }

    public void populate(
            LifecycleHandlerCollection handlers) {

        LifecycleNode root =
                buildLifecycleGraph(
                        handlers,
                        () -> {
                            revalidate();
                            repaint();
                        });

        levelNodes = Optional.of(getLevelNodes(root));
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
            Runnable repaint) {

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
}
