package interfaces;

import com.intellij.openapi.project.Project;

public interface INotificationService {
    void notifyInfo(Project project, String message);
    void notifyWarning(Project project, String message);
    void notifyError(Project project, Exception exception);
}
