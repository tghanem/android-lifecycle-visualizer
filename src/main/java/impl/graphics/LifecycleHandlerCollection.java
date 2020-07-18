package impl.graphics;

import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class LifecycleHandlerCollection extends ArrayList<LifecycleEventHandler> {
    public LifecycleHandlerCollection(Collection<LifecycleEventHandler> collection) {
        super(collection);
    }

    public Optional<LifecycleEventHandler> findByName(String handlerName) {
        for (LifecycleEventHandler handler : this) {
            if (handler.getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    public LifecycleHandlerNode buildLifecycleHandlerNode(String handlerName, Consumer<LifecycleNode> repaint) {
        Optional<LifecycleEventHandler> handler =
                findByName(handlerName);

        LifecycleHandlerNode node =
                new LifecycleHandlerNode(handler, handlerName);

        if (handler.isPresent()) {
            for (ResourceAcquisition resourceAcquisition : handler.get().getResourceAcquisitions()) {
                node.addLink(
                        new ResourceAcquisitionLifecycleNode(
                                resourceAcquisition.getResourceName(),
                                resourceAcquisition),
                        false);
            }

            for (ResourceRelease resourceRelease : handler.get().getResourceReleases()) {
                node.add(
                        new ResourceReleaseLifecycleNode(
                                resourceRelease.getResourceName(),
                                resourceRelease),
                        false);
            }
        }

        node.addActionListener(n -> repaint.accept(node));

        return node;
    }
}
