package impl.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class LifecyclePanel extends JPanel {
    public LifecyclePanel() {
        graphRoot = Optional.empty();
    }

    public void populate(LifecycleHandlerCollection handlers) {
        LifecycleNode root =
                buildLifecycleGraph(
                        handlers,
                        () -> {
                            shouldRedraw = true;
                            revalidate();
                            repaint();
                        });

        graphRoot = Optional.of(root);
        shouldRedraw = true;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (!graphRoot.isPresent() || !shouldRedraw) {
            return;
        }

        removeAll();

        List<List<LifecycleNode>> levelNodes =
                Helper.getLevelNodes(graphRoot.get());

        int maxLevelNodeCount =
                getMaxLevelNodeCount(levelNodes);

        GridLayout layout =
                new GridLayout(
                        levelNodes.size(),
                        maxLevelNodeCount);

        setLayout(layout);

        for (List<LifecycleNode> nodes : levelNodes) {
            for (int i = 0; i < maxLevelNodeCount; i++) {
                if (i < nodes.size()) {
                    add(nodes.get(i));
                } else {
                    add(new JLabel(" "));
                }
            }
        }

        shouldRedraw = false;
    }

    private int getMaxLevelNodeCount(List<List<LifecycleNode>> levelNodes) {
        int maxCount = 0;
        for (List<LifecycleNode> nodes : levelNodes) {
            if (nodes.size() > maxCount) {
                maxCount = nodes.size();
            }
        }
        return maxCount;
    }

    private LifecycleNode buildLifecycleGraph(
            LifecycleHandlerCollection handlers,
            Runnable repaint) {

        LifecycleHandlerNode onCreate =
                handlers.buildLifecycleHandlerNode("onCreate", repaint);

        LifecycleHandlerNode onStart =
                handlers.buildLifecycleHandlerNode("onStart", repaint);

        onCreate.addChild(onStart);

        LifecycleHandlerNode onResume =
                handlers.buildLifecycleHandlerNode("onResume", repaint);

        onStart.addChild(onResume);

        LifecycleHandlerNode onPause =
                handlers.buildLifecycleHandlerNode("onPause", repaint);

        onResume.addChild(onPause);

        LifecycleHandlerNode onRestart =
                handlers.buildLifecycleHandlerNode("onRestart", repaint);

        LifecycleHandlerNode onStop =
                handlers.buildLifecycleHandlerNode("onStop", repaint);

        onPause.addChild(onResume);
        onPause.addChild(onStop);
        onPause.addChild(onCreate);

        LifecycleHandlerNode onDestroy =
                handlers.buildLifecycleHandlerNode("onDestroy", repaint);

        onStop.addChild(onRestart);
        onStop.addChild(onDestroy);
        onStop.addChild(onCreate);

        return onCreate;
    }

    private Boolean shouldRedraw;
    private Optional<LifecycleNode> graphRoot;
}
