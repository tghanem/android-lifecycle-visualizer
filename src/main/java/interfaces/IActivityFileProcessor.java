package interfaces;

import com.intellij.openapi.vfs.VirtualFile;

public interface IActivityFileProcessor {
    void Process(VirtualFile file) throws Exception;
}
