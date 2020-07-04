package impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class Helper {
    public static String getExceptionInformation(Exception exception) {
        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().toString() + ": " + exception.getMessage());
        sb.append(System.lineSeparator());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw.toString());

        return sb.toString();
    }

    public static Optional<Element> findFirst(NodeList nodeList, Predicate<Element> predicate) {
        AtomicReference<Optional<Element>> result = new AtomicReference<>(Optional.empty());

        processChildElements(
                nodeList,
                element -> {
                    if (predicate.test(element)) {
                        result.set(Optional.of(element));
                        return false;
                    }
                    return true;
                });

        return result.get();
    }

    private static void processChildElements(NodeList nodeList, Function<Element, Boolean> processElement) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node instanceof Element) {
                if (!processElement.apply((Element)node)) {
                    break;
                }
            }
        }
    }

    public static Optional<PsiFile> findAndroidManifestFile(Project project) {
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
}
