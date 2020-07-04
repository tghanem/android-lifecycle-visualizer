package impl.listeners;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import interfaces.IFileProcessor;
import interfaces.INotificationService;
import org.jetbrains.annotations.NotNull;

public class FileOpenListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        try {
            ServiceManager
                    .getService(IFileProcessor.class)
                    .setCurrentlyOpenedFile(source.getProject(), file);
        } catch (Exception e) {
            ServiceManager
                    .getService(INotificationService.class)
                    .notify(source.getProject(), e);
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        try {
            ServiceManager
                    .getService(IFileProcessor.class)
                    .setCurrentlyOpenedFile(event.getManager().getProject(), event.getNewFile());
        } catch (Exception e) {
            ServiceManager
                    .getService(INotificationService.class)
                    .notify(event.getManager().getProject(), e);
        }
    }
}
