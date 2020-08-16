package impl.services;

import com.intellij.psi.PsiFile;
import impl.ActivityFileParser;
import impl.model.dstl.Activity;
import interfaces.IActivityFileParser;
import interfaces.IActivityFileProcessor;

import java.util.Optional;

public class ActivityFileProcessorService implements IActivityFileProcessor {
    private final IActivityFileParser lifecycleParser;

    public ActivityFileProcessorService() {
        lifecycleParser = new ActivityFileParser();
    }

    @Override
    public Optional<Activity> process(PsiFile file) {
        return lifecycleParser.parse(file);
    }
}
