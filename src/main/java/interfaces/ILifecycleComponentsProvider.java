package interfaces;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.util.Pair;

import java.util.List;

public interface ILifecycleComponentsProvider {
    List<Pair<String, VirtualFile>> getLifecycleComponents(Project project) throws Exception;
}
