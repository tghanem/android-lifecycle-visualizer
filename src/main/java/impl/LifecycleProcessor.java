package impl;

import com.intellij.openapi.vfs.VirtualFile;
import interfaces.ILifecycleParser;
import interfaces.ILifecycleProcessor;
import interfaces.ILifecycleRepresentationConverter;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import java.util.Optional;

public class LifecycleProcessor implements ILifecycleProcessor {
    private final ILifecycleParser parser;
    private final ILifecycleRepresentationConverter converter;

    public LifecycleProcessor(
            ILifecycleParser parser,
            ILifecycleRepresentationConverter converter) {

        this.parser = parser;
        this.converter = converter;
    }

    @Override
    public Document Process(
            String applicationName,
            List<Pair<String, VirtualFile>> lifecycleComponentsFiles) throws Exception {

        Document lifecycleDocument =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        Element lifecycleRootElement =
                lifecycleDocument.createElement("ApplicationLifecycle");

        lifecycleRootElement.setAttribute("ApplicationName", applicationName);

        for (Pair<String, VirtualFile> activityFile : lifecycleComponentsFiles) {
            Optional<Document> document =
                    parser.Parse(activityFile.getValue(), activityFile.getKey());

            if (!document.isPresent()) {
                continue;
            }

            NodeList lifecycleAwareComponentElements =
                    document
                            .get()
                            .getElementsByTagName("LifecycleAwareComponent");

            Helper.processChildElements(
                    lifecycleAwareComponentElements,
                    element -> {
                        lifecycleRootElement.appendChild(
                                lifecycleDocument.importNode(element, true));
                    });
        }

        lifecycleDocument.appendChild(lifecycleRootElement);

        return converter.Convert(lifecycleDocument);
    }
}
