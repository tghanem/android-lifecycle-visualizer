package interfaces.graphics.dsvl;

import com.intellij.openapi.wm.ToolWindow;
import impl.model.dstl.LifecycleAwareComponent;

public interface IActivityViewService {
    void setActivityViewHolder(ToolWindow toolWindow);
    void displayActivityView(LifecycleAwareComponent asComponent);
}
