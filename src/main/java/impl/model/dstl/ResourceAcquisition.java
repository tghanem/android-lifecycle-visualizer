package impl.model.dstl;

import com.intellij.psi.PsiElement;

public class ResourceAcquisition {
    public ResourceAcquisition(PsiElement element, String resourceName) {
        this.element = element;
        this.resourceName = resourceName;
    }

    public PsiElement getPsiElement() {
        return element;
    }

    public String getResourceName() {
        return resourceName;
    }

    private final PsiElement element;
    private final String resourceName;
}
