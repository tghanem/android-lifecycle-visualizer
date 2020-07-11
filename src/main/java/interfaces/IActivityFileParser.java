package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import impl.model.dstl.LifecycleAwareComponent;
import org.w3c.dom.Document;

import java.util.Optional;

public interface IActivityFileParser {
    Optional<LifecycleAwareComponent> parse(VirtualFile lifecycleImplementation) throws Exception;
}
