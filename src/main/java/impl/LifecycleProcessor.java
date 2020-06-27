package impl;

import com.intellij.openapi.project.Project;
import interfaces.ILifecycleParser;
import interfaces.ILifecycleProcessor;
import interfaces.ILifecycleRepresentationConverter;
import org.w3c.dom.Document;

public class LifecycleProcessor implements ILifecycleProcessor {
    private final ILifecycleParser parser;
    private final ILifecycleRepresentationConverter converter;

    public LifecycleProcessor(
        ILifecycleParser parser,
        ILifecycleRepresentationConverter converter) {

        this.parser = parser;
        this.converter = converter;
    }

    @Override
    public Document Process(Project project) {
        return null;
    }
}
