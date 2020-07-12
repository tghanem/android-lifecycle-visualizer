package impl.model.dstl;

import impl.Helper;
import impl.exceptions.MissingElementException;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class LifecycleEventHandler {
    public static final String XML_ELEMENT_NAME = "LifecycleEventHandler";

    public LifecycleEventHandler(
            String name,
            Location location,
            List<ResourceAcquisition> resourceAcquisitions,
            List<ResourceRelease> resourceReleases) {

        this.name = name;
        this.location = location;
        this.resourceAcquisitions = resourceAcquisitions;
        this.resourceReleases = resourceReleases;
    }

    public static LifecycleEventHandler valueOf(Element element) {
        List<ResourceAcquisition> resourceAcquisitions = new ArrayList<>();

        List<ResourceRelease> resourceReleases = new ArrayList<>();

        Helper.processChildElements(
                element.getChildNodes(),
                child ->
                {
                    switch (child.getTagName())
                    {
                        case ResourceAcquisition.XML_ELEMENT_NAME:
                            resourceAcquisitions.add(ResourceAcquisition.valueOf(child));
                            break;
                        case ResourceRelease.XML_ELEMENT_NAME:
                            resourceReleases.add(ResourceRelease.valueOf(child));
                            break;
                    }

                    return true;
                });

        return
                new LifecycleEventHandler(
                        element.getAttribute("Name"),
                        Location
                                .valueOf(element.getChildNodes())
                                .orElseThrow(() -> new MissingElementException("Location element not found")),
                        resourceAcquisitions,
                        resourceReleases);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public List<ResourceAcquisition> getResourceAcquisitions() {
        return resourceAcquisitions;
    }

    public List<ResourceRelease> getResourceReleases() {
        return resourceReleases;
    }

    private final String name;
    private final Location location;
    private final List<ResourceAcquisition> resourceAcquisitions;
    private final List<ResourceRelease> resourceReleases;
}
