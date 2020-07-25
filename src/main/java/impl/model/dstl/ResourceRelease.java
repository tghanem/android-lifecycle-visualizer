package impl.model.dstl;

import com.intellij.psi.PsiElement;

public class ResourceRelease {
    public ResourceRelease(PsiElement element) {
        this.element = element;
    }

    public PsiElement getPsiElement() {
        return element;
    }

    private final PsiElement element;
}
