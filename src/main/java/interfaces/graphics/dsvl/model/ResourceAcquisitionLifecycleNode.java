package interfaces.graphics.dsvl.model;

import impl.model.dstl.ResourceAcquisition;

import java.util.function.Consumer;

public class ResourceAcquisitionLifecycleNode extends LifecycleNode {
    public ResourceAcquisitionLifecycleNode(Consumer<LifecycleNode> paintNode, ResourceAcquisition resourceAcquisition) {
        super(paintNode, resourceAcquisition.getResourceName());

        this.resourceAcquisition = resourceAcquisition;
    }

    public ResourceAcquisition getResourceAcquisition() {
        return resourceAcquisition;
    }

    private final ResourceAcquisition resourceAcquisition;
}
