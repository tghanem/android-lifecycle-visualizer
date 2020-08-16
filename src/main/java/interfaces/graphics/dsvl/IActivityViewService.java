package interfaces.graphics.dsvl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiFile;

public interface IActivityViewService {
    void setActivityViewHolder(
            Project project,
            ToolWindow toolWindow);

    void openActivity(PsiFile activityFile);

    void closeActivity(PsiFile activityFile);
}
