package interfaces;

import org.w3c.dom.Document;

import javax.xml.transform.TransformerConfigurationException;

public interface ILifecycleRepresentationConverter {
    Document Convert(Document source) throws Exception;
}
