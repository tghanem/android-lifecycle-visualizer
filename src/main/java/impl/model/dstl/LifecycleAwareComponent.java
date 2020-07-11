package impl.model.dstl;

import impl.Helper;
import impl.exceptions.MissingElementException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

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

    private final Location location;
    private final List<LifecycleEventHandler> lifecycleEventHandlers;
}
