package impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import impl.model.dstl.Activity;
import impl.model.dstl.CallbackMethod;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.IActivityFileParser;
import interfaces.ICallbackMethodAnalyzer;
import interfaces.ICallbackMethodAnalyzerFactory;
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
                    "onRestart",
                    "onResume",
                    "onPause",
                    "onStop",
                    "onDestroy");

    @Override
    public Optional<Activity> parse(PsiFile file) {
        Optional<PsiClass[]> classes = getClasses(file);

        if (!classes.isPresent() || classes.get().length == 0) {
            return Optional.empty();
        }

        List<CallbackMethod> handlers = new ArrayList<>();

        PsiClass psiClass = classes.get()[0];

        ICallbackMethodAnalyzerFactory analyzerFactory =
                ServiceManager.getService(ICallbackMethodAnalyzerFactory.class);

        ICallbackMethodAnalyzer<ResourceAcquisition> resourceAcquisitionAnalyzer =
                analyzerFactory.createResourceAcquisitionAnalyzer();

        ICallbackMethodAnalyzer<ResourceRelease> resourceReleaseAnalyzer =
                analyzerFactory.createResourceReleaseAnalyzer();

        for (PsiMethod method : psiClass.getMethods()) {
            if (callbacks.contains(method.getName())) {
                handlers.add(
                        new CallbackMethod(
                                method,
                                resourceAcquisitionAnalyzer.analyze(method),
                                resourceReleaseAnalyzer.analyze(method)));
            }
        }

        return Optional.of(new Activity(psiClass, handlers));
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
