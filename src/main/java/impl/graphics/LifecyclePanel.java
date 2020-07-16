package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LifecyclePanel extends JPanel {
    private static final int LIFECYCLE_TREE_DEPTH = 6;

    public void populate(List<LifecycleEventHandler> handlers) {
        graphRoot =
                buildLifecycleGraph(
                        () -> {
                            revalidate();
                            repaint();
                        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        removeAll();

        List<List<LifecycleNode>> levelNodes =
                Helper.getLevelNodes(graphRoot);

        int maxLevelNodeCount =
                getMaxLevelNodeCount(levelNodes);

        GridLayout layout =
                new GridLayout(
                        LIFECYCLE_TREE_DEPTH,
                        maxLevelNodeCount);

        setLayout(layout);

        for (List<LifecycleNode> nodes : levelNodes) {
            for (int i = 0; i < nodes.size(); i++) {
                if (i < nodes.size()) {
                    add(nodes.get(i));
                } else {
                    add(new JPanel());
                }
            }
        }
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

    private LifecycleNode buildLifecycleGraph(Runnable repaint) {
        LifecycleNode onCreate =
                new LifecycleNode("onCreate", repaint);

        LifecycleNode onStart =
                new LifecycleNode("onStart", repaint);

        onCreate.add(onStart);

        LifecycleNode onResume =
                new LifecycleNode("onResume", repaint);

        onStart.addChild(onResume);

        LifecycleNode onPause =
                new LifecycleNode("onPause", repaint);

        onResume.addChild(onPause);

        LifecycleNode onRestart =
                new LifecycleNode("onRestart", repaint);

        LifecycleNode onStop =
                new LifecycleNode("onStop", repaint);

        onPause.addChild(onResume);
        onPause.addChild(onStop);
        onPause.addChild(onCreate);

        LifecycleNode onDestroy =
                new LifecycleNode("onDestroy", repaint);

        onStop.addChild(onRestart);
        onStop.addChild(onDestroy);
        onStop.addChild(onCreate);

        return onCreate;
    }

    private LifecycleNode graphRoot;
}
