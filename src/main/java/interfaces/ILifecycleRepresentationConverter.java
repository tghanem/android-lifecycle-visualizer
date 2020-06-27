package interfaces;

import com.intellij.psi.xml.XmlDocument;

public interface ILifecycleRepresentationConverter {
    XmlDocument Convert(XmlDocument source);
}
