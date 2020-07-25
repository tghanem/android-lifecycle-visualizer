package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiFile;
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
                Helper.findAndroidManifestFile(file.getProject());

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

    private Boolean isActivityNameEqualTo(Element activityElement, String fileName) {
        String fullyQualifiedActivityName =
                activityElement.getAttribute("android:name");

        String[] activityNameTokens =
                fullyQualifiedActivityName.split("\\.");

        String activityName =
                activityNameTokens[activityNameTokens.length - 1];

        return (activityName + ".java").equals(fileName) ||
                (activityName + ".kt").equals(fileName);
    }
}
