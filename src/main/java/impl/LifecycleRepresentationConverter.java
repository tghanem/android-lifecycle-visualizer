package impl;

import interfaces.ILifecycleRepresentationConverter;
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

public class LifecycleRepresentationConverter implements ILifecycleRepresentationConverter {
    @Override
    public Document Convert(Document source) throws Exception {
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

        transformer.transform(new DOMSource(source), new StreamResult(transformerResult));

        return
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(transformerResult.toString())));
    }
}
