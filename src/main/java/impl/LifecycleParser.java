package impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import interfaces.ILifecycleParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Optional;

import static com.intellij.openapi.vfs.VfsUtilCore.loadText;

public class LifecycleParser implements ILifecycleParser {
    @Override
    public Document Parse(VirtualFile lifecycleImplementation) throws Exception {
        CompilationUnit parseResult =
                StaticJavaParser.parse(loadText(lifecycleImplementation));

        Document document =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        Element activityElement =
                document.createElement("Activity");

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);

                Optional<Element> callbackElement =
                        toCallbackElement(n, document, lifecycleImplementation.getName());

                if (callbackElement.isPresent()) {
                    activityElement.appendChild(callbackElement.get());
                }
            }
        }.visit(parseResult, null);

        document.appendChild(activityElement);

        return document;
    }

    private Optional<Element> toCallbackElement(MethodDeclaration declaration, Document document, String fileName) {
        String methodName = declaration.getName().asString();

        if (methodName.equals("onCreate")) {
            Element callbackElement =
                    document.createElement("onCreate");

            callbackElement.setAttribute("Name", "onCreate");
            callbackElement.appendChild(toLocationElement(declaration.getRange().get(), document, fileName));

            return Optional.of(callbackElement);
        }

        return Optional.empty();
    }

    private Element toLocationElement(Range range, Document document, String fileName) {
        Element locationElement =
                document.createElement("Location");

        locationElement.setAttribute("FileName", fileName);
        locationElement.setAttribute("LineNumber", String.valueOf(range.begin.line));

        return locationElement;
    }
}
