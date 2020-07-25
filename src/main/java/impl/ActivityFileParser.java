package impl;

import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import impl.model.dstl.*;
import interfaces.IActivityFileParser;
import org.jetbrains.kotlin.psi.KtFile;

import java.util.*;

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

    private List<ResourceAcquisition> lookupResourceAcquisitions(PsiMethod method) {
        List<ResourceAcquisition> result = new ArrayList<>();

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiMethod resolvedMethod =
                    methodCallExpression.resolveMethod();

            if (resolvedMethod != null) {
                String returnTypeName =
                        resolvedMethod.getReturnType().getCanonicalText();

                if (returnTypeName.equals("android.hardware.Camera")) {
                    result.add(new ResourceAcquisition(element));
                }
            }
        }

        return result;
    }

    private List<ResourceRelease> lookupResourceReleases(PsiMethod method) {
        List<ResourceRelease> result = new ArrayList<>();

        Collection<PsiElement> methodCallExpressions =
                PsiTreeUtil.findChildrenOfAnyType(method, PsiMethodCallExpression.class);

        for (PsiElement element : methodCallExpressions) {
            PsiMethodCallExpression methodCallExpression =
                    (PsiMethodCallExpression) element;

            PsiElement methodReferenceExpression =
                    methodCallExpression.getFirstChild();

            if (methodReferenceExpression instanceof PsiReferenceExpression) {
                PsiReferenceExpression referenceExpression =
                        (PsiReferenceExpression) methodReferenceExpression;

                if (referenceExpression.getQualifierExpression() != null) {
                    if (referenceExpression.getQualifierExpression().getType() != null) {
                        String qualifierName =
                                referenceExpression
                                        .getQualifierExpression()
                                        .getType()
                                        .getCanonicalText();

                        String[] methodName =
                                referenceExpression
                                        .getQualifiedName()
                                        .split("\\.");

                        if (methodName.length > 1) {
                            if (qualifierName.equals("android.hardware.Camera") && methodName[1].equals("release")) {
                                result.add(new ResourceRelease(element));
                            }
                        }
                    }
                }
            }
        }

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
