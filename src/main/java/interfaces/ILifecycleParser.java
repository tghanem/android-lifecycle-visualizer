package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.Document;

import java.util.Optional;

public interface ILifecycleParser {
    Optional<Document> Parse(VirtualFile lifecycleImplementation, String lifecycleComponentName) throws Exception;
}
