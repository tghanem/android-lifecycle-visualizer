package interfaces.graphics.dsvl;

import com.intellij.psi.PsiClass;
import interfaces.graphics.dsvl.model.CircularLifecycleNode;
import interfaces.graphics.dsvl.model.LifecycleHandlerNode;
import interfaces.graphics.dsvl.model.ResourceAcquisitionLifecycleNode;
import interfaces.graphics.dsvl.model.ResourceReleaseLifecycleNode;
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
