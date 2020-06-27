package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.Document;

public interface ILifecycleParser {
    Document Parse(VirtualFile lifecycleImplementation) throws Exception;
}
