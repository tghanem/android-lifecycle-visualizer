package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.VirtualFile;
import impl.ActivityFileParser;
import interfaces.IActivityFileProcessor;
import interfaces.IActivityFileParser;
import interfaces.IActivityViewProvider;
import interfaces.IActivityViewService;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Optional;

public class ActivityFileProcessorService implements IActivityFileProcessor {
    private final IActivityFileParser lifecycleParser;

    public ActivityFileProcessorService() {
        lifecycleParser = new ActivityFileParser();
    }

    @Override
    public void Process(VirtualFile file) throws Exception {
        Optional<Document> activityFileDocument =
                lifecycleParser.parse(file);

        if (!activityFileDocument.isPresent()) {
            return;
        }

        StreamSource transformSource =
                new StreamSource(
                        getClass()
                                .getClassLoader()
                                .getResourceAsStream("Transform.xsl"));

        Transformer transformer =
                TransformerFactory
                        .newInstance()
                        .newTransformer(transformSource);

        StringWriter transformerResult = new StringWriter();

        transformer.transform(new DOMSource(activityFileDocument.get()), new StreamResult(transformerResult));

        Document viewDocument =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(transformerResult.toString())));

        Collection<IActivityViewProvider> viewProviders =
                ServiceManager
                        .getService(IActivityViewService.class)
                        .getViewProviders();

        for (IActivityViewProvider provider : viewProviders) {
            provider.display(viewDocument);
        }
    }
}
