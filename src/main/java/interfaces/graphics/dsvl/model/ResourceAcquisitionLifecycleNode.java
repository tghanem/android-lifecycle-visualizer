package interfaces.graphics.dsvl.model;

import impl.model.dstl.ResourceAcquisition;

import javax.swing.*;

public class ResourceAcquisitionLifecycleNode extends LifecycleNode {
    public ResourceAcquisitionLifecycleNode(
            String name,
            ResourceAcquisition resourceAcquisition) {

        super(name);

        this.resourceAcquisition = resourceAcquisition;
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("acquire.png")));
    }

    public ResourceAcquisition getResourceAcquisition() {
        return resourceAcquisition;
    }

    private final ResourceAcquisition resourceAcquisition;
}
