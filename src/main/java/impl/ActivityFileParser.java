package impl;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import impl.model.dstl.LifecycleAwareComponent;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.Location;
import interfaces.IActivityFileParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ActivityFileParser implements IActivityFileParser {
    private static final List<String> callbacks =
            Arrays.asList(
                    "onCreate",
                    "onStart",
                    "onResume",
                    "onPause",
                    "onStop",
                    "onDestroy");

    @Override
    public Optional<LifecycleAwareComponent> parse(
            PsiFile file) throws Exception {

        PsiJavaFile asJava = (PsiJavaFile) file;
        PsiClass[] classes = asJava.getClasses();

        if (classes.length == 0) {
            return Optional.empty();
        }

        List<LifecycleEventHandler> handlers =
                new ArrayList<>();

        try {
            for (PsiMethod method : classes[0].getAllMethods()) {
                if (callbacks.contains(method.getName())) {
                    handlers.add(
                            new LifecycleEventHandler(
                                    method.getName(),
                                    new Location(file.getName(), method.getTextOffset()),
                                    new ArrayList<>(),
                                    new ArrayList<>()));
                }
            }
        } catch (IndexNotReadyException ex) {
            return Optional.empty();
        }

        return
                Optional.of(
                        new LifecycleAwareComponent(
                                new Location(file.getName(), classes[0].getTextOffset()),
                                handlers));
    }
}
