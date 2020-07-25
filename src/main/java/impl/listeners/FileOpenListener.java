package impl.listeners;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import interfaces.IFileProcessor;
import interfaces.INotificationService;
import org.jetbrains.annotations.NotNull;

public class FileOpenListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        try {
            PsiFile psiFile =
                PsiManager
                        .getInstance(source.getProject())
                        .findFile(file);

            ServiceManager
                    .getService(IFileProcessor.class)
                    .setCurrentlyOpenedFile(psiFile);
        } catch (Exception e) {
            ServiceManager
                    .getService(INotificationService.class)
                    .notify(source.getProject(), e);
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        try {
            if (event.getNewFile() == null) {
                return;
            }

            PsiFile psiFile =
                    PsiManager
                            .getInstance(event.getManager().getProject())
                            .findFile(event.getNewFile());

            ServiceManager
                    .getService(IFileProcessor.class)
                    .setCurrentlyOpenedFile(psiFile);
        } catch (Exception e) {
            ServiceManager
                    .getService(INotificationService.class)
                    .notify(event.getManager().getProject(), e);
        }
    }
}
