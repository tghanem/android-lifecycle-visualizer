package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlDocument;

public interface ILifecycleParser {
    XmlDocument Parse(VirtualFile lifecycleImplementation);
}
