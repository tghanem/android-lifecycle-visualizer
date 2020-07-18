package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class LifecycleHandlerCollection extends ArrayList<LifecycleEventHandler> {
    public LifecycleHandlerCollection(Collection<LifecycleEventHandler> collection) {
        super(collection);
    }

    public Optional<LifecycleEventHandler> findByName(String handlerName) {
        for (LifecycleEventHandler handler : this) {
            if (handler.getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    public LifecycleHandlerNode buildLifecycleHandlerNode(String handlerName, Consumer<LifecycleNode> repaint) {
        LifecycleHandlerNode node =
                new LifecycleHandlerNode(
                        findByName(handlerName),
                        handlerName);

        node.addActionListener(n -> repaint.accept(node));

        return node;
    }
}
