package interfaces;

import com.intellij.openapi.project.Project;
import org.w3c.dom.Document;

public interface ILifecycleProcessor {
    Document Process(Project project) throws Exception;
}
