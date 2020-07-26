package impl.graphics;

import impl.model.dstl.ResourceRelease;

public class ResourceReleaseLifecycleNode extends LifecycleNode {
    public ResourceReleaseLifecycleNode(
            String name,
            ResourceRelease resourceRelease) {

        super(name);
        this.resourceRelease = resourceRelease;
    }

    public ResourceRelease getResourceRelease() {
        return resourceRelease;
    }

    private final ResourceRelease resourceRelease;
}
