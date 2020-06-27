package impl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

public class Helper {
    public static String getExceptionInformation(Exception exception) {
        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().toString() + ": " + exception.getMessage());
        sb.append(System.lineSeparator());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw.toString());

        return sb.toString();
    }

    public static void processChildElements(Element element, Consumer<Element> processElement) {
        NodeList childNodeList = element.getChildNodes();
        processChildElements(childNodeList, processElement);
    }

    public static void processChildElements(NodeList nodeList, Consumer<Element> processElement) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node instanceof Element) {
                processElement.accept((Element) node);
            }
        }
    }
}
