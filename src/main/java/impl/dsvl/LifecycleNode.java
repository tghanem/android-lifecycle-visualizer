package impl.dsvl;

import impl.model.dstl.LifecycleEventHandler;

import java.util.Optional;

public class LifecycleNode {
    public LifecycleNode(
            String name,
            Optional<LifecycleEventHandler> lifecycleEventHandler) {

        this.name = name;
        this.lifecycleEventHandler = lifecycleEventHandler;
    }

    public String getName() {
        return name;
    }

    public Optional<LifecycleEventHandler> getLifecycleEventHandler() {
        return lifecycleEventHandler;
    }

    private final String name;
    private final Optional<LifecycleEventHandler> lifecycleEventHandler;
}
