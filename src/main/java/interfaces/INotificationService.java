package interfaces;

import com.intellij.openapi.project.Project;

public interface INotificationService {
    void notify(Project project, Exception exception);
}
