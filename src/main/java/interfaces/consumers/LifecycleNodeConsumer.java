package interfaces.consumers;

import impl.graphics.LifecycleNode;

@FunctionalInterface
public interface LifecycleNodeConsumer {
    void accept(Integer level, LifecycleNode node);
}
