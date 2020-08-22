package impl;

import com.intellij.psi.*;
import impl.analyzers.FullyQualifiedClassAndMethodName;
import impl.analyzers.FullyQualifiedClassAndMethodNamesAnalyzer;
import impl.model.dstl.Activity;
import impl.model.dstl.CallbackMethod;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import impl.settings.AppSettingsState;
import interfaces.IActivityFileParser;
import interfaces.ICallbackMethodAnalyzer;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.*;

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

        ICallbackMethodAnalyzer<FullyQualifiedClassAndMethodName, PsiMethodCallExpression> resourceAnalyzer =
                new FullyQualifiedClassAndMethodNamesAnalyzer();

        HashMap<FullyQualifiedClassAndMethodName, String> resourceAcquisitions =
                AppSettingsState.getInstance().parseResourceAcquisitions();

        HashMap<FullyQualifiedClassAndMethodName, String> resourceReleases =
                AppSettingsState.getInstance().parseResourceReleases();

        for (PsiMethod method : psiClass.getMethods()) {
            List<ResourceAcquisition> acquisitions = new ArrayList<>();
            List<ResourceRelease> releases = new ArrayList<>();

            if (callbacks.contains(method.getName())) {
                resourceAnalyzer.analyze(
                        method,
                        fullyQualifiedClassAndMethodName -> {
                            return
                                    resourceAcquisitions.containsKey(fullyQualifiedClassAndMethodName) ||
                                            resourceReleases.containsKey(fullyQualifiedClassAndMethodName);
                        },
                        (fullyQualifiedClassAndMethodName, psiMethodCallExpression) -> {
                            if (resourceAcquisitions.containsKey(fullyQualifiedClassAndMethodName)) {
                                acquisitions.add(
                                        new ResourceAcquisition(
                                                psiMethodCallExpression,
                                                resourceAcquisitions.get(fullyQualifiedClassAndMethodName)));
                            } else if (resourceReleases.containsKey(fullyQualifiedClassAndMethodName)) {
                                releases.add(
                                        new ResourceRelease(
                                                psiMethodCallExpression,
                                                resourceReleases.get(fullyQualifiedClassAndMethodName)));
                            }
                        });
            }

            handlers.add(new CallbackMethod(method, acquisitions, releases));
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
