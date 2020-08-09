package interfaces.consumers;

import interfaces.graphics.dsvl.model.LifecycleNode;

@FunctionalInterface
public interface LifecycleNodeConsumer {
    void accept(Integer level, LifecycleNode node);
}
