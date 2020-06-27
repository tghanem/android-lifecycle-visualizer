package impl;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import interfaces.ILifecycleParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.intellij.openapi.vfs.VfsUtilCore.loadText;

public class LifecycleParser implements ILifecycleParser {
    private static final List<String> callbacks =
            Arrays.asList("onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy");

    @Override
    public Optional<Document> Parse(VirtualFile lifecycleImplementation, String lifecycleComponentName) throws Exception {
        CompilationUnit parseResult =
                StaticJavaParser.parse(loadText(lifecycleImplementation));

        Document document =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        Element lifecycleAwareComponentElement =
                document.createElement("LifecycleAwareComponent");

        Optional<ClassOrInterfaceDeclaration> lifecycleComponentClassDeclaration =
                parseResult.getClassByName(lifecycleComponentName);

        if (!lifecycleComponentClassDeclaration.isPresent()) {
            return Optional.empty();
        }

        lifecycleAwareComponentElement.setAttribute(
                "Name",
                lifecycleComponentClassDeclaration.get().getNameAsString());

        lifecycleAwareComponentElement.appendChild(
                toLocationElement(
                        lifecycleComponentClassDeclaration
                                .get()
                                .getRange()
                                .get(),
                        document,
                        lifecycleImplementation.getName()));

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);

                if (callbacks.contains(n.getName().asString())) {
                    lifecycleAwareComponentElement.appendChild(
                            toLifecycleEventHandlerElement(n, document, lifecycleImplementation.getName()));
                }
            }
        }.visit(parseResult, null);

        document.appendChild(lifecycleAwareComponentElement);

        return Optional.of(document);
    }

    private Element toLifecycleEventHandlerElement(MethodDeclaration declaration, Document document, String fileName) {
        Element lifecycleEventHandlerElement =
                document.createElement("LifecycleEventHandler");

        lifecycleEventHandlerElement.setAttribute(
                "Name",
                declaration.getNameAsString());

        lifecycleEventHandlerElement.appendChild(
                toLocationElement(
                        declaration
                                .getRange()
                                .get(),
                        document,
                        fileName));

        return lifecycleEventHandlerElement;
    }

    private Element toLocationElement(Range range, Document document, String fileName) {
        Element locationElement =
                document.createElement("Location");

        locationElement.setAttribute(
                "FileName",
                fileName);

        locationElement.setAttribute(
                "LineNumber",
                String.valueOf(range.begin.line));

        return locationElement;
    }
}
