package interfaces.graphics.dsvl;

import com.intellij.psi.PsiClass;
import interfaces.graphics.dsvl.model.CircularLifecycleNode;
import interfaces.graphics.dsvl.model.CallbackMethodNode;
import interfaces.graphics.dsvl.model.ResourceAcquisitionLifecycleNode;
import interfaces.graphics.dsvl.model.ResourceReleaseLifecycleNode;
import impl.model.dstl.CallbackMethod;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

import java.util.Optional;
import java.util.function.Consumer;

public interface ILifecycleNodeFactory {
    CallbackMethodNode createCallbackMethodNode(
            PsiClass ownerActivityClass,
            String callbackMethodName,
            Optional<CallbackMethod> callbackMethod,
            Consumer<CallbackMethodNode> onClick);

    CircularLifecycleNode createCircularLifecycleNode(
            PsiClass ownerActivityClass,
            CallbackMethodNode targetCallbackMethodNode);

    ResourceAcquisitionLifecycleNode createResourceAcquisitionLifecycleNode(
            ResourceAcquisition resourceAcquisition);

    ResourceReleaseLifecycleNode createResourceReleaseLifecycleNode(
            ResourceRelease resourceRelease);
}
