package interfaces;

import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

public interface ILifecycleEventHandlerAnalyzerFactory {
    ILifecycleEventHandlerAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer();

    ILifecycleEventHandlerAnalyzer<ResourceRelease> createResourceReleaseAnalyzer();
}
