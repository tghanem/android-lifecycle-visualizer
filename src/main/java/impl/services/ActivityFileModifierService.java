package impl.services;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import interfaces.IActivityFileModifier;

public class ActivityFileModifierService implements IActivityFileModifier {
    @Override
    public void addLifecycleEventHandler(PsiClass activityClass, String handlerName) {
        PsiFile activityFile =
                activityClass.getContainingFile();

        PsiMethod handlerMethod =
                PsiElementFactory
                        .getInstance(activityFile.getProject())
                        .createMethod(handlerName, PsiType.VOID);

        handlerMethod
                .getModifierList()
                .addAnnotation(Override.class.getName());

        handlerMethod
                .getModifierList()
                .setModifierProperty(PsiModifier.PROTECTED, true);

        WriteCommandAction.runWriteCommandAction(
                activityFile.getProject(),
                () -> {
                    activityClass.add(handlerMethod);
                });
    }
}
