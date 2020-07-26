package impl.graphics;

import impl.model.dstl.ResourceAcquisition;

public class ResourceAcquisitionLifecycleNode extends LifecycleNode {
    public ResourceAcquisitionLifecycleNode(
            String name,
            ResourceAcquisition resourceAcquisition) {

        super(name);
        this.resourceAcquisition = resourceAcquisition;
    }

    public ResourceAcquisition getResourceAcquisition() {
        return resourceAcquisition;
    }

    private final ResourceAcquisition resourceAcquisition;
}
