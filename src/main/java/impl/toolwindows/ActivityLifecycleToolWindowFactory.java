package impl.toolwindows;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import interfaces.IFileEditorManagerEventHandler;
import interfaces.graphics.dsvl.IActivityViewService;
import org.jetbrains.annotations.NotNull;

public class ActivityLifecycleToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ServiceManager
                .getService(IActivityViewService.class)
                .setActivityViewHolder(project, toolWindow);

        for (FileEditor editor : FileEditorManager.getInstance(project).getAllEditors()) {
            ServiceManager
                    .getService(IFileEditorManagerEventHandler.class)
                    .processFileOpenedOrSelected(editor.getFile(), project);
        }

        project
                .getMessageBus()
                .connect()
                .subscribe(
                        FileEditorManagerListener.FILE_EDITOR_MANAGER,
                        new FileEditorManagerListener() {
                            @Override
                            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                                ServiceManager
                                        .getService(IFileEditorManagerEventHandler.class)
                                        .processFileOpenedOrSelected(file, source.getProject());
                            }

                            @Override
                            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                                ServiceManager
                                        .getService(IFileEditorManagerEventHandler.class)
                                        .processFileClosed(file, source.getProject());
                            }

                            @Override
                            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                                if (event.getNewFile() == null) {
                                    return;
                                }

                                ServiceManager
                                        .getService(IFileEditorManagerEventHandler.class)
                                        .processFileOpenedOrSelected(event.getNewFile(), event.getManager().getProject());
                            }
                        });
    }
}
