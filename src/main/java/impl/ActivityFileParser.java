package impl;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.psi.*;
import impl.model.dstl.*;
import interfaces.IActivityFileParser;
import org.jetbrains.kotlin.psi.KtFile;

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

        Optional<PsiClass[]> classes =
                getClasses(file);

        if (!classes.isPresent() || classes.get().length == 0) {
            return Optional.empty();
        }

        List<LifecycleEventHandler> handlers =
                new ArrayList<>();

        PsiClass psiClass = classes.get()[0];

        try {
            for (PsiMethod method : psiClass.getAllMethods()) {
                if (callbacks.contains(method.getName())) {
                    handlers.add(
                            new LifecycleEventHandler(
                                    method,
                                    lookupResourceAcquisitions(method),
                                    lookupResourceReleases(method)));
                }
            }
        } catch (IndexNotReadyException ex) {
            return Optional.empty();
        }

        return Optional.of(new LifecycleAwareComponent(psiClass, handlers));
    }

    private List<ResourceAcquisition> lookupResourceAcquisitions(
            PsiMethod method) {

        List<ResourceAcquisition> result =
                new ArrayList<>();


        return result;
    }

    private List<ResourceRelease> lookupResourceReleases(
            PsiMethod method) {

        List<ResourceRelease> result =
                new ArrayList<>();


        return result;
    }

    private Optional<PsiClass[]> getClasses(PsiFile file) {
        if (file instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) file;
            return Optional.of(javaFile.getClasses());
        } else if (file instanceof KtFile) {
            KtFile kotlinFile = (KtFile) file;
            return Optional.of(kotlinFile.getClasses());
        } else {
            return Optional.empty();
        }
    }
}
