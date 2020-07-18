package impl.graphics;

@FunctionalInterface
public interface LifecycleNodeConsumer {
    void accept(Integer level, LifecycleNode node);
}
