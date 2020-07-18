package impl.graphics;

import impl.model.dstl.ResourceRelease;

public class ResourceReleaseLifecycleNode extends LifecycleNode {
    public ResourceReleaseLifecycleNode(
            String name,
            ResourceRelease resourceRelease) {

        super(name);
        this.resourceRelease = resourceRelease;
    }

    private final ResourceRelease resourceRelease;
}
