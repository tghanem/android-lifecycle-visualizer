package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import impl.Helper;
import interfaces.IActivityFileProcessor;
import interfaces.IFileProcessor;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class FileProcessorService implements IFileProcessor {
    @Override
    public void setCurrentlyOpenedFile(PsiFile file) throws Exception {
        Optional<PsiFile> androidManifestFile =
                findAndroidManifestFile(file.getProject());

        if (!androidManifestFile.isPresent()) {
            return;
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

        Boolean isActivityFile =
                Helper
                        .findFirst(activityElements, element -> isActivityNameEqualTo(element, file.getName()))
                        .isPresent();

        if (isActivityFile) {
            ServiceManager
                    .getService(IActivityFileProcessor.class)
                    .process(file);
        }
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
