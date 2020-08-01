package impl.services;

import com.intellij.psi.PsiFile;
import interfaces.IActivityFileProcessingController;

import java.util.Optional;

public class ActivityFileProcessingController implements IActivityFileProcessingController {
    private Optional<PsiFile> lastProcessedActivityFile;

    public ActivityFileProcessingController() {
        lastProcessedActivityFile = Optional.empty();
    }

    @Override
    public Boolean shouldProcessActivityFile(PsiFile psiFile) {
        if (lastProcessedActivityFile.isPresent()) {
            return
                    !psiFile
                            .getVirtualFile()
                            .getCanonicalPath()
                            .equalsIgnoreCase(
                                    lastProcessedActivityFile
                                            .get()
                                            .getVirtualFile()
                                            .getCanonicalPath());
        } else {
            return true;
        }
    }

    @Override
    public void setProcessedActivityFile(PsiFile file) {
        lastProcessedActivityFile = Optional.of(file);
    }
}
