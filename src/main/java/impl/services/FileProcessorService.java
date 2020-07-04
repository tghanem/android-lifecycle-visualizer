package impl.services;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import impl.Helper;
import interfaces.IActivityFileProcessor;
import interfaces.IFileProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Optional;

public class FileProcessorService implements IFileProcessor {
    @Override
    public void setCurrentlyOpenedFile(Project project, VirtualFile file) throws Exception {
        Optional<PsiFile> androidManifestFile =
                Helper.findAndroidManifestFile(project);

        if (!androidManifestFile.isPresent()) {
            return;
        }

        Document androidManifestDocument =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(
                                new InputSource(
                                        new StringReader(VfsUtil.loadText(androidManifestFile.get().getVirtualFile()))));

        NodeList activityElements =
                androidManifestDocument.getElementsByTagName("activity");

        Boolean isActivityFile =
                Helper
                        .findFirst(activityElements, element -> isActivityNameEqualTo(element, file.getName()))
                        .isPresent();

        if (isActivityFile) {
            ServiceManager
                    .getService(IActivityFileProcessor.class)
                    .Process(file);
        }
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
