package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import impl.Helper;
import interfaces.IFileEditorManagerEventHandler;
import interfaces.INotificationService;
import interfaces.graphics.dsvl.IActivityViewService;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class FileEditorManagerEventHandler implements IFileEditorManagerEventHandler {
    @Override
    public void processFileOpenedOrSelected(VirtualFile file, Project project) {
        DumbService
                .getInstance(project)
                .runWhenSmart(() -> {
                    try {
                        if (!file.isValid()) {
                            ServiceManager
                                    .getService(INotificationService.class)
                                    .notifyInfo(project, "File " + file.getName() + " is not valid");

                            return;
                        }

                        PsiFile psiFile =
                                PsiManager
                                        .getInstance(project)
                                        .findFile(file);

                        Optional<Boolean> isActivity =
                                isActivityFile(psiFile, project);

                        if (isActivity.orElse(false)) {
                            ServiceManager
                                    .getService(IActivityViewService.class)
                                    .openOrReloadActivity(psiFile);
                        } else {
                            ServiceManager
                                    .getService(INotificationService.class)
                                    .notifyInfo(project, "File " + file.getName() + " is not an Activity file");
                        }
                    } catch (Exception e) {
                        ServiceManager
                                .getService(INotificationService.class)
                                .notifyError(project, e);
                    }
                });
    }

    @Override
    public void processFileClosed(VirtualFile file, Project project) {
        DumbService
                .getInstance(project)
                .runWhenSmart(() -> {
                    try {
                        if (!file.isValid()) {
                            ServiceManager
                                    .getService(INotificationService.class)
                                    .notifyInfo(project, "File " + file.getName() + " is not valid");

                            return;
                        }

                        PsiFile psiFile =
                                PsiManager
                                        .getInstance(project)
                                        .findFile(file);

                        Optional<Boolean> isActivity =
                                isActivityFile(psiFile, project);

                        if (isActivity.orElse(false)) {
                            ServiceManager
                                    .getService(IActivityViewService.class)
                                    .closeActivity(psiFile);
                        } else {
                            ServiceManager
                                    .getService(INotificationService.class)
                                    .notifyInfo(project, "File " + file.getName() + " is not an Activity file");
                        }
                    } catch (Exception e) {
                        ServiceManager
                                .getService(INotificationService.class)
                                .notifyError(project, e);
                    }
                });
    }

    private Optional<Boolean> isActivityFile(PsiFile file, Project project) throws Exception {
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

    private Optional<PsiFile> findAndroidManifestFile(Project project) {
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

    private Boolean isActivityNameEqualTo(Element activityElement, String fileName) {
        String fullyQualifiedActivityName =
                activityElement.getAttribute("android:name");

        String[] activityNameTokens =
                fullyQualifiedActivityName.split("\\.");

        String activityName =
                activityNameTokens[activityNameTokens.length - 1];

        return (activityName + ".java").equals(fileName);
    }
}
