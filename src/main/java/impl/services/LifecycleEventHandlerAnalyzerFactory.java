package impl.services;

import impl.analyzers.DeprecatedCameraApiAcquisitionAnalyzer;
import impl.analyzers.DeprecatedCameraApiReleaseAnalyzer;
import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;
import interfaces.ILifecycleEventHandlerAnalyzer;
import interfaces.ILifecycleEventHandlerAnalyzerFactory;

public class LifecycleEventHandlerAnalyzerFactory implements ILifecycleEventHandlerAnalyzerFactory {
    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer() {
        return new DeprecatedCameraApiAcquisitionAnalyzer();
    }

    @Override
    public ILifecycleEventHandlerAnalyzer<ResourceRelease> createResourceReleaseAnalyzer() {
        return new DeprecatedCameraApiReleaseAnalyzer();
    }

}
