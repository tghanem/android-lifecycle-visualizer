package impl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class Helper {
    public static Optional<Element> findFirst(
            NodeList nodeList,
            Predicate<Element> predicate) {

        AtomicReference<Optional<Element>> result =
                new AtomicReference<>(Optional.empty());

        processChildElements(
                nodeList,
                element -> {
                    if (predicate.test(element)) {
                        result.set(Optional.of(element));
                        return false;
                    }
                    return true;
                });

        return result.get();
    }

    public static void processChildElements(
            NodeList nodeList,
            Function<Element, Boolean> processElement) {

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node instanceof Element) {
                if (!processElement.apply((Element) node)) {
                    break;
                }
            }
        }
    }

}
