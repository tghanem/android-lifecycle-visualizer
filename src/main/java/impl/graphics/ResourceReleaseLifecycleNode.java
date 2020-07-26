package impl.graphics;

import impl.model.dstl.ResourceRelease;

import javax.swing.*;

public class ResourceReleaseLifecycleNode extends LifecycleNode {
    public ResourceReleaseLifecycleNode(
            String name,
            ResourceRelease resourceRelease) {

        super(name);

        this.resourceRelease = resourceRelease;
        this.setIcon(new ImageIcon(getClass().getClassLoader().getResource("socket.png")));
    }

    public ResourceRelease getResourceRelease() {
        return resourceRelease;
    }

    private final ResourceRelease resourceRelease;
}
