package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.function.Consumer;

public class LifecycleNode extends JButton {
    public LifecycleNode(
            String name,
            Consumer<LifecycleNode> drawTransitions,
            Optional<LifecycleEventHandler> handler) {

        this.name = name;
        this.drawTransitions = drawTransitions;
        this.handler = handler;

        this.addActionListener(
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        buttonClicked();
                    }
                });
    }

    public Optional<LifecycleEventHandler> getHandler() {
        return handler;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        setText(name);
        setEnabled(handler.isPresent());
    }

    private void buttonClicked() {
        drawTransitions.accept(this);
    }

    private String name;
    private Consumer<LifecycleNode> drawTransitions;
    private final Optional<LifecycleEventHandler> handler;
}
