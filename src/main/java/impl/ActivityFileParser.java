package impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import impl.model.dstl.LifecycleAwareComponent;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.IActivityFileParser;
import interfaces.ILifecycleEventHandlerAnalyzer;
import interfaces.ILifecycleEventHandlerAnalyzerFactory;
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
    public Optional<LifecycleAwareComponent> parse(PsiFile file) {
        Optional<PsiClass[]> classes = getClasses(file);

        if (!classes.isPresent() || classes.get().length == 0) {
            return Optional.empty();
        }

        List<LifecycleEventHandler> handlers = new ArrayList<>();

        PsiClass psiClass = classes.get()[0];

        ILifecycleEventHandlerAnalyzerFactory analyzerFactory =
                ServiceManager.getService(ILifecycleEventHandlerAnalyzerFactory.class);

        ILifecycleEventHandlerAnalyzer<ResourceAcquisition> resourceAcquisitionAnalyzer =
                analyzerFactory.createResourceAcquisitionAnalyzer();

        ILifecycleEventHandlerAnalyzer<ResourceRelease> resourceReleaseAnalyzer =
                analyzerFactory.createResourceReleaseAnalyzer();

        try {
            for (PsiMethod method : psiClass.getAllMethods()) {
                if (callbacks.contains(method.getName())) {
                    handlers.add(
                            new LifecycleEventHandler(
                                    method,
                                    resourceAcquisitionAnalyzer.analyze(method),
                                    resourceReleaseAnalyzer.analyze(method)));
                }
            }
        } catch (IndexNotReadyException ex) {
            return Optional.empty();
        }

        return Optional.of(new LifecycleAwareComponent(psiClass, handlers));
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
