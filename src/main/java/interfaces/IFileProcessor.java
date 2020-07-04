package interfaces;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface IFileProcessor {
    void setCurrentlyOpenedFile(Project project, VirtualFile file) throws Exception;
}
