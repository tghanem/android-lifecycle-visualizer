package impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import interfaces.ILifecycleParser;
import interfaces.ILifecycleProcessor;
import interfaces.ILifecycleRepresentationConverter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LifecycleProcessor implements ILifecycleProcessor {
    private final ILifecycleParser parser;
    private final ILifecycleRepresentationConverter converter;

    public LifecycleProcessor(
            ILifecycleParser parser,
            ILifecycleRepresentationConverter converter) {

        this.parser = parser;
        this.converter = converter;
    }

    @Override
    public Document Process(Project project) throws Exception {
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

        Document lifecycleDocument =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        List<VirtualFile> activityFiles = new ArrayList<>();

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
                        activityFiles.add(javaFiles[0].getVirtualFile());
                    }
                });

        for (VirtualFile activityFile : activityFiles) {
            Document document = parser.Parse(activityFile);

            NodeList activityElementsFromParser =
                    document.getElementsByTagName("Activity");

            Helper.processChildElements(
                    activityElementsFromParser,
                    element -> lifecycleDocument.importNode(element, true));
        }

        return converter.Convert(lifecycleDocument);
    }
}
