package interfaces.graphics.dsvl.model;

import impl.model.dstl.ResourceAcquisition;

import javax.swing.*;

public class ResourceAcquisitionLifecycleNode extends LifecycleNode {
    public ResourceAcquisitionLifecycleNode(ResourceAcquisition resourceAcquisition) {

        super(resourceAcquisition.getResourceName());

        this.resourceAcquisition = resourceAcquisition;
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("acquire.png")));
    }

    public ResourceAcquisition getResourceAcquisition() {
        return resourceAcquisition;
    }

    private final ResourceAcquisition resourceAcquisition;
}
