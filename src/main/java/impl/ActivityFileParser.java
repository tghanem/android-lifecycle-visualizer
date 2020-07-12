package impl;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import impl.model.dstl.LifecycleAwareComponent;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.Location;
import interfaces.IActivityFileParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.intellij.openapi.vfs.VfsUtilCore.loadText;

public class ActivityFileParser implements IActivityFileParser {
    private static final List<String> callbacks =
            Arrays.asList("onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy");

    @Override
    public Optional<LifecycleAwareComponent> parse(VirtualFile lifecycleImplementation) throws Exception {
        CompilationUnit parseResult =
                StaticJavaParser.parse(loadText(lifecycleImplementation));

        Optional<ClassOrInterfaceDeclaration> lifecycleComponentClassDeclaration =
                getActivityClass(parseResult);

        if (!lifecycleComponentClassDeclaration.isPresent()) {
            return Optional.empty();
        }

        List<LifecycleEventHandler> lifecycleEventHandlers =
                new ArrayList<>();

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(MethodDeclaration n, Object arg) {
                super.visit(n, arg);

                if (callbacks.contains(n.getName().asString())) {
                    lifecycleEventHandlers.add(
                            toLifecycleEventHandler(n, lifecycleImplementation.getName()));
                }
            }
        }.visit(parseResult, null);

        return
                Optional.of(
                        new LifecycleAwareComponent(
                                toLocation(
                                        lifecycleComponentClassDeclaration
                                                .get()
                                                .getRange()
                                                .get(),
                                        lifecycleImplementation.getName()),
                                lifecycleEventHandlers));
    }

    private Optional<ClassOrInterfaceDeclaration> getActivityClass(CompilationUnit parseResult) {
        List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();

        new VoidVisitorAdapter<Object>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Object arg) {
                super.visit(n, arg);
                classes.add(n);
            }
        }.visit(parseResult, null);

        if (classes.size() > 0) {
            return Optional.of(classes.get(0));
        }

        return Optional.empty();
    }

    private LifecycleEventHandler toLifecycleEventHandler(MethodDeclaration declaration, String fileName) {
        return
                new LifecycleEventHandler(
                        declaration.getNameAsString(),
                        toLocation(declaration.getRange().get(), fileName),
                        new ArrayList<>(),
                        new ArrayList<>());
    }

    private Location toLocation(Range range, String fileName) {
        return
                new Location(
                        fileName,
                        range.begin.line);
    }
}
