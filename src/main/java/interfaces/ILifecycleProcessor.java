package interfaces;

import com.intellij.openapi.vfs.VirtualFile;
import javafx.util.Pair;
import org.w3c.dom.Document;

import java.util.List;

public interface ILifecycleProcessor {
    Document Process(List<Pair<String, VirtualFile>> lifecycleComponentsFiles) throws Exception;
}
