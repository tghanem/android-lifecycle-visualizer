package impl.model.dstl;

import com.intellij.psi.PsiMethod;

import java.util.List;

public class CallbackMethod {
    public CallbackMethod(
            PsiMethod element,
            List<ResourceAcquisition> resourceAcquisitions,
            List<ResourceRelease> resourceReleases) {

        this.element = element;
        this.resourceAcquisitions = resourceAcquisitions;
        this.resourceReleases = resourceReleases;
    }

    public PsiMethod getPsiElement() {
        return element;
    }

    public List<ResourceAcquisition> getResourceAcquisitions() {
        return resourceAcquisitions;
    }

    public List<ResourceRelease> getResourceReleases() {
        return resourceReleases;
    }

    private final PsiMethod element;
    private final List<ResourceAcquisition> resourceAcquisitions;
    private final List<ResourceRelease> resourceReleases;
}
