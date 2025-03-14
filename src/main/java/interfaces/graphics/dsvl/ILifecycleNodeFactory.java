package interfaces.graphics.dsvl;

import com.intellij.psi.PsiClass;
import interfaces.graphics.dsvl.model.*;
import impl.model.dstl.CallbackMethod;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ILifecycleNodeFactory {
    CallbackMethodNode createCallbackMethodNode(
            PsiClass ownerActivityClass,
            String callbackMethodName,
            Optional<CallbackMethod> callbackMethod,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<CallbackMethodNode> onClick,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod);

    CircularLifecycleNode createCircularLifecycleNode(
            PsiClass ownerActivityClass,
            CallbackMethodNode targetCallbackMethodNode,
            Function<CallbackMethodNode, Boolean> nodeHasUnderlyingCallbackMethod,
            Consumer<LifecycleNode> paintNode,
            Consumer<CallbackMethodNode> goToNode,
            Consumer<CallbackMethodNode> onAddCallbackMethod);

    ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition,
            Consumer<LifecycleNode> paintNode,
            Consumer<ResourceAcquisitionLifecycleNode> goToResource);

    ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease,
            Consumer<LifecycleNode> paintNode,
            Consumer<ResourceReleaseLifecycleNode> goToResource);
}
