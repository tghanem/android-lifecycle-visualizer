package impl;

import com.intellij.ide.plugins.PluginManager;
import interfaces.ILifecycleRepresentationConverter;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

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

        transformer.setErrorListener(
                new ErrorListener() {
                    @Override
                    public void warning(TransformerException e) throws TransformerException {
                        PluginManager.getLogger().warn(e);
                    }

                    @Override
                    public void error(TransformerException e) throws TransformerException {
                        PluginManager.getLogger().error(e);
                    }

                    @Override
                    public void fatalError(TransformerException e) throws TransformerException {
                        PluginManager.getLogger().error(e);
                    }
                });

        Document result =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        transformer.transform(new DOMSource(source), new DOMResult(result));

        return result;
    }
}
