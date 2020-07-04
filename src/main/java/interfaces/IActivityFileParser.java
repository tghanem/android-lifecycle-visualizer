package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import org.w3c.dom.Document;

import java.util.Optional;

public interface IActivityFileParser {
    Optional<Document> parse(VirtualFile lifecycleImplementation) throws Exception;
}
