package impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import interfaces.ILifecycleComponentsProvider;
import javafx.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LifecycleComponentsProvider implements ILifecycleComponentsProvider {
    @Override
    public List<Pair<String, VirtualFile>> getLifecycleComponents(Project project) throws Exception {
        PsiFile[] files =
                FilenameIndex.getFilesByName(
                        project,
                        "AndroidManifest.xml",
                        GlobalSearchScope.projectScope(project));

        if (files.length == 0) {
            throw new Exception("AndroidManifest.xml is missing");
        }

        Document androidManifestDocument =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(new InputSource(new StringReader(VfsUtil.loadText(files[0].getVirtualFile()))));

        NodeList activityElements =
                androidManifestDocument.getElementsByTagName("activity");

        List<Pair<String, VirtualFile>> activityFilesList = new ArrayList<>();

        Helper.processChildElements(
                activityElements,
                activityElement -> {
                    String fullyQualifiedActivityName =
                            activityElement.getAttribute("android:name");

                    String[] activityNameTokens =
                            fullyQualifiedActivityName.split("\\.");

                    String activityName =
                            activityNameTokens[activityNameTokens.length - 1];

                    PsiFile[] javaFiles =
                            FilenameIndex.getFilesByName(
                                    project,
                                    activityName + ".java",
                                    GlobalSearchScope.projectScope(project));

                    if (javaFiles.length > 0) {
                        activityFilesList.add(
                                new Pair<>(activityName, javaFiles[0].getVirtualFile()));
                    }
                });

        return activityFilesList;
    }
}
