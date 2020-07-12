package interfaces;

import impl.model.dstl.LifecycleEventHandler;

import java.util.List;

public interface IActivityViewProvider {
    void display(List<LifecycleEventHandler> handlers);
}
