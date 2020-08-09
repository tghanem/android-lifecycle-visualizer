package interfaces.graphics.dsvl;

import com.intellij.openapi.wm.ToolWindow;
import impl.model.dstl.Activity;

public interface IActivityViewService {
    void setActivityViewHolder(ToolWindow toolWindow);
    void displayActivity(Activity activity);
}
