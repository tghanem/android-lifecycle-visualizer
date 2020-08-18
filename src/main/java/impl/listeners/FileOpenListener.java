package impl.listeners;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import impl.Helper;
import interfaces.INotificationService;
import interfaces.graphics.dsvl.IActivityViewService;
import org.apache.xerces.parsers.DOMParser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class FileOpenListener implements FileEditorManagerListener {
    @Override
    public void fileOpened(
            @NotNull FileEditorManager source,
            @NotNull VirtualFile file) {

        fileOpenedOrSelected(
                file,
                source.getProject(),
                ServiceManager.getService(IActivityViewService.class),
                ServiceManager.getService(INotificationService.class));
    }

    @Override
    public void selectionChanged(
            @NotNull FileEditorManagerEvent event) {

        if (event.getNewFile() == null) {
            return;
        }

        fileOpenedOrSelected(
                event.getNewFile(),
                event.getManager().getProject(),
                ServiceManager.getService(IActivityViewService.class),
                ServiceManager.getService(INotificationService.class));
    }

    @Override
    public void fileClosed(
            @NotNull FileEditorManager source,
            @NotNull VirtualFile file) {

        fileClosed(
                file,
                source.getProject(),
                ServiceManager.getService(IActivityViewService.class),
                ServiceManager.getService(INotificationService.class));
    }

    private void fileOpenedOrSelected(
            VirtualFile file,
            Project project,
            IActivityViewService viewService,
            INotificationService notificationService) {

        DumbService
                .getInstance(project)
                .runWhenSmart(() -> {
                    try {
                        if (!file.isValid()) {
                            return;
                        }

                        PsiFile psiFile =
                                PsiManager
                                        .getInstance(project)
                                        .findFile(file);

                        Optional<Boolean> isActivity =
                                isActivityFile(psiFile, project);

                        if (isActivity.orElse(false)) {
                            viewService.openOrReloadActivity(psiFile);
                        }
                    } catch (Exception e) {
                        notificationService.notify(project, e);
                    }
                });
    }

    private void fileClosed(
            VirtualFile file,
            Project project,
            IActivityViewService viewService,
            INotificationService notificationService) {

        DumbService
                .getInstance(project)
                .runWhenSmart(() -> {
                    try {
                        if (!file.isValid()) {
                            return;
                        }

                        PsiFile psiFile =
                                PsiManager
                                        .getInstance(project)
                                        .findFile(file);

                        Optional<Boolean> isActivity =
                                isActivityFile(psiFile, project);

                        if (isActivity.orElse(false)) {
                            viewService.closeActivity(psiFile);
                        }
                    } catch (Exception e) {
                        notificationService.notify(project, e);
                    }
                });
    }

    private Optional<Boolean> isActivityFile(
            PsiFile file,
            Project project) throws Exception {

        Optional<PsiFile> androidManifestFile =
                findAndroidManifestFile(project);

        if (!androidManifestFile.isPresent()) {
            return Optional.empty();
        }

        DOMParser parser = new DOMParser();

        parser.parse(
                androidManifestFile
                        .get()
                        .getVirtualFile()
                        .getPath());

        Document androidManifestDocument = parser.getDocument();

        NodeList activityElements =
                androidManifestDocument.getElementsByTagName("activity");

        return
                Optional.of(
                        Helper
                                .findFirst(activityElements, element -> isActivityNameEqualTo(element, file.getName()))
                                .isPresent());
    }

    private Optional<PsiFile> findAndroidManifestFile(
            Project project) {

        PsiFile[] files =
                FilenameIndex.getFilesByName(
                        project,
                        "AndroidManifest.xml",
                        GlobalSearchScope.projectScope(project));

        if (files.length > 0) {
            return Optional.of(files[0]);
        }

        return Optional.empty();
    }

    private Boolean isActivityNameEqualTo(
            Element activityElement,
            String fileName) {

        String fullyQualifiedActivityName =
                activityElement.getAttribute("android:name");

        String[] activityNameTokens =
                fullyQualifiedActivityName.split("\\.");

        String activityName =
                activityNameTokens[activityNameTokens.length - 1];

        return (activityName + ".java").equals(fileName);
    }
}
