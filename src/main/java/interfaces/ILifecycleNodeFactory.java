package interfaces;

import com.intellij.psi.PsiClass;
import impl.graphics.CircularLifecycleNode;
import impl.graphics.LifecycleHandlerNode;
import impl.graphics.ResourceAcquisitionLifecycleNode;
import impl.graphics.ResourceReleaseLifecycleNode;
import impl.model.dstl.LifecycleEventHandler;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

import java.util.Optional;
import java.util.function.Consumer;

public interface ILifecycleNodeFactory {
    LifecycleHandlerNode createLifecycleHandlerNode(
            PsiClass ownerActivityClass,
            String handlerName,
            Optional<LifecycleEventHandler> eventHandler,
            Consumer<LifecycleHandlerNode> onClick);

    CircularLifecycleNode createCircularLifecycleHandlerNode(
            PsiClass ownerActivityClass,
            LifecycleHandlerNode targetLifecycleHandlerNode);

    ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition);

    ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease);
}
