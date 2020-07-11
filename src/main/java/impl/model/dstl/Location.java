package impl.model.dstl;

import impl.Helper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class Location {
    public static final String XML_ELEMENT_NAME = "Location";

    public Location(String fileName, int lineNumber) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public static Optional<Location> valueOf(NodeList nodeList) {
        return
                Helper.findFirst(
                        nodeList,
                        XML_ELEMENT_NAME,
                        element -> valueOf(element));
    }

    public static Location valueOf(Element element) {
        return
                new Location(
                        element.getAttribute("FileName"),
                        Integer.valueOf(element.getAttribute("LineNumber")));
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    private final String fileName;
    private final int lineNumber;
}
