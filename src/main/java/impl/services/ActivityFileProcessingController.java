package impl.services;

import com.intellij.psi.PsiFile;
import impl.model.dstl.Activity;
import interfaces.IActivityFileProcessingController;

import java.util.Optional;

public class ActivityFileProcessingController implements IActivityFileProcessingController {
    private Optional<PsiFile> lastProcessedActivityFile;
    private Optional<Activity> lastDisplayedActivity;

    public ActivityFileProcessingController() {
        lastProcessedActivityFile = Optional.empty();
        lastDisplayedActivity = Optional.empty();
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
    public void setProcessedActivityFile(
            PsiFile file,
            Activity activity) {

        lastProcessedActivityFile = Optional.of(file);
        lastDisplayedActivity = Optional.of(activity);
    }

    @Override
    public Optional<Activity> getLastDisplayedActivity() {
        return lastDisplayedActivity;
    }
}
