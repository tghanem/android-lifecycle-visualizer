package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ModelPanel extends JPanel {
    public ModelPanel() {
        transitions = new ArrayList<>();
    }

    public void setHandlers(Optional<List<LifecycleEventHandler>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        if (handlers == null || !handlers.isPresent()) {
            return;
        }

        draw((Graphics2D) graphics, handlers.get());
    }

    private void draw(Graphics2D graphics, List<LifecycleEventHandler> handlers) {
        removeAll();
        transitions.clear();

        buildLifecycleTransitions(
                buildLifecycleNodes(handlers));

        add(transitions.get(0).getStart());
    }

    private void buildLifecycleTransitions(
            HashMap<String, LifecycleNode> nodeHashMap) {

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onCreate"),
                        nodeHashMap.get("onStart")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onStart"),
                        nodeHashMap.get("onResume")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onResume"),
                        nodeHashMap.get("onPause")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onPause"),
                        nodeHashMap.get("onResume")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onPause"),
                        nodeHashMap.get("onCreate")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onPause"),
                        nodeHashMap.get("onStop")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onStop"),
                        nodeHashMap.get("onCreate")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onStop"),
                        nodeHashMap.get("onRestart")));

        transitions.add(
                new LifecycleTransition(
                        nodeHashMap.get("onStop"),
                        nodeHashMap.get("onDestroy")));
    }

    private HashMap<String, LifecycleNode> buildLifecycleNodes(
            List<LifecycleEventHandler> handlers) {

        HashMap<String, LifecycleNode> nodeHashMap =
                new HashMap<>();

        nodeHashMap.put("onCreate", buildLifecycleNode(handlers, "onCreate"));
        nodeHashMap.put("onStart", buildLifecycleNode(handlers, "onStart"));
        nodeHashMap.put("onResume", buildLifecycleNode(handlers, "onResume"));
        nodeHashMap.put("onPause", buildLifecycleNode(handlers, "onPause"));
        nodeHashMap.put("onRestart", buildLifecycleNode(handlers, "onRestart"));
        nodeHashMap.put("onStop", buildLifecycleNode(handlers, "onStop"));
        nodeHashMap.put("onDestroy", buildLifecycleNode(handlers, "onDestroy"));

        return nodeHashMap;
    }

    private LifecycleNode buildLifecycleNode(
            List<LifecycleEventHandler> handlers,
            String nodeName) {

        for (LifecycleEventHandler handler : handlers) {
            if (handler.getName().equals(nodeName)) {
                return
                        new LifecycleNode(
                                nodeName,
                                this::drawTransitions,
                                Optional.of(handler));
            }
        }

        return
                new LifecycleNode(
                        nodeName,
                        this::drawTransitions,
                        Optional.empty());
    }

    private void drawTransitions(LifecycleNode node) {
        for (LifecycleTransition transition : transitions) {

        }
    }

    private Optional<List<LifecycleEventHandler>> handlers;
    private List<LifecycleTransition> transitions;
}
