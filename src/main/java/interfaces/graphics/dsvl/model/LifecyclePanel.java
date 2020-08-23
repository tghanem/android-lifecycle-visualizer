package interfaces.graphics.dsvl.model;

import interfaces.graphics.dsvl.IActivityViewProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.*;

public class LifecyclePanel extends JPanel implements IActivityViewProvider {
    public LifecyclePanel() {
        graphRoot = Optional.empty();
    }

    @Override
    public void display(CallbackMethodNode graphRoot) {
        this.graphRoot = Optional.of(graphRoot);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
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
                if (node instanceof CallbackMethodNode) {
                    CallbackMethodNode handlerNode = (CallbackMethodNode) node;

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

    private void drawNonCircularLine(Graphics2D graphics, Rectangle source, Rectangle target) {
        Path2D path = new Path2D.Double();
        path.moveTo(source.x + source.width / 2.0, source.y);
        path.lineTo(target.x + target.width / 2.0, target.y);
        path.closePath();

        graphics.draw(path);
    }

    private Optional<CallbackMethodNode> graphRoot;
}