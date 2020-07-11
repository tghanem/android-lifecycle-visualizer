package impl.model.dstl;

import impl.exceptions.MissingElementException;
import org.w3c.dom.Element;

public class ResourceRelease {
    public static final String XML_ELEMENT_NAME = "ResourceRelease";

    public ResourceRelease(String resourceName, Location location) {
        this.resourceName = resourceName;
        this.location = location;
    }

    public static ResourceRelease valueOf(Element element) {
        return
                new ResourceRelease(
                        element.getAttribute("ResourceName"),
                        Location.valueOf(element.getChildNodes())
                                .orElseThrow(() -> new MissingElementException("Location element not found")));
    }

    public String getResourceName() {
        return resourceName;
    }

    public Location getLocation() {
        return location;
    }

    private final String resourceName;
    private final Location location;
}
