package interfaces.graphics.dsvl.model;

import impl.model.dstl.ResourceRelease;

import java.util.function.Consumer;

public class ResourceReleaseLifecycleNode extends LifecycleNode {
    public ResourceReleaseLifecycleNode(Consumer<LifecycleNode> paintNode, ResourceRelease resourceRelease) {
        super(paintNode, resourceRelease.getResourceName());

        this.resourceRelease = resourceRelease;
    }

    public ResourceRelease getResourceRelease() {
        return resourceRelease;
    }

    private final ResourceRelease resourceRelease;
}
