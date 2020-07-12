package impl.model.dstl;

import impl.Helper;
import impl.dsvl.LifecycleNode;
import impl.exceptions.MissingElementException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LifecycleAwareComponent {
    public LifecycleAwareComponent(
            Location location,
            List<LifecycleEventHandler> lifecycleEventHandlers) {

        this.location = location;
        this.lifecycleEventHandlers = lifecycleEventHandlers;
    }

    public static LifecycleAwareComponent valueOf(Element element) {
        List<LifecycleEventHandler> handlers = new ArrayList<>();

        Helper.processChildElements(
                element.getChildNodes(),
                child ->
                {
                    if (child.getTagName().equals(LifecycleEventHandler.XML_ELEMENT_NAME)) {
                        handlers.add(LifecycleEventHandler.valueOf(child));
                    }
                    return true;
                });

        return
                new LifecycleAwareComponent(
                        Location
                                .valueOf(element.getChildNodes())
                                .orElseThrow(() -> new MissingElementException("Location element was not found")),
                        handlers);
    }

    public Location getLocation() {
        return location;
    }

    public List<LifecycleEventHandler> getLifecycleEventHandlers() {
        return lifecycleEventHandlers;
    }

    public void addIfPresent(List<LifecycleNode> nodes, String handlerName) {
        findHandler(handlerName)
                .ifPresent(handler -> nodes.add(new LifecycleNode(handlerName, Optional.of(handler))));
    }

    public Optional<LifecycleEventHandler> findHandler(String handlerName) {
        for (LifecycleEventHandler handler : lifecycleEventHandlers) {
            if (handler.getName().equals(handlerName)) {
                return Optional.of(handler);
            }
        }
        return Optional.empty();
    }

    private final Location location;
    private final List<LifecycleEventHandler> lifecycleEventHandlers;
}
