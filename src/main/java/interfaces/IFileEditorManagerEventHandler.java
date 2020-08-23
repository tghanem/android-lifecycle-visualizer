package interfaces;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface IFileEditorManagerEventHandler {
    void processFileOpenedOrSelected(
            VirtualFile file,
            Project project);

    void processFileClosed(
            VirtualFile file,
            Project project);
}
