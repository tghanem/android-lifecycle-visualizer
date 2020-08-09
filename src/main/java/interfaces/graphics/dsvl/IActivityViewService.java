package interfaces.graphics.dsvl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import impl.model.dstl.Activity;

public interface IActivityViewService {
    void setActivityViewHolder(Project project, ToolWindow toolWindow);
    void displayActivity(Activity activity);
}
