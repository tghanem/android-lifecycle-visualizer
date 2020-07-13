package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModelPanel extends JPanel {
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

        GridLayout layout =
                new GridLayout(
                        Helper.getMaxDepth(graphRoot),
                        Helper.getMaxWidth(graphRoot));

        setLayout(layout);


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
