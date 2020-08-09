package interfaces;

import impl.model.dstl.ResourceAcquisition;
import impl.model.dstl.ResourceRelease;

public interface ICallbackMethodAnalyzerFactory {
    ICallbackMethodAnalyzer<ResourceAcquisition> createResourceAcquisitionAnalyzer();

    ICallbackMethodAnalyzer<ResourceRelease> createResourceReleaseAnalyzer();
}
