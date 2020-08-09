package interfaces;

import com.intellij.psi.PsiFile;
import impl.model.dstl.Activity;

import java.util.Optional;

public interface IActivityFileParser {
    Optional<Activity> parse(PsiFile file);
}
