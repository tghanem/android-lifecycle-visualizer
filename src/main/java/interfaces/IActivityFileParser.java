package interfaces;

import com.intellij.psi.PsiFile;
import impl.model.dstl.LifecycleAwareComponent;

import java.util.Optional;

public interface IActivityFileParser {
    Optional<LifecycleAwareComponent> parse(PsiFile file) throws Exception;
}
