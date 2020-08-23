package impl.services;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import interfaces.INotificationService;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NotificationService implements INotificationService {
    private final NotificationGroup notificationGroup;

    public NotificationService() {
        this.notificationGroup =
            new NotificationGroup(
                    "Activity Lifecycle Viewer",
                    NotificationDisplayType.BALLOON,
                    true);

    }

    @Override
    public void notifyInfo(Project project, String message) {
        notificationGroup
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    @Override
    public void notifyWarning(Project project, String message) {
        notificationGroup
                .createNotification(message, NotificationType.WARNING)
                .notify(project);
    }

    @Override
    public void notifyError(Project project, Exception exception) {
        notificationGroup
                .createNotification(getExceptionInformation(exception), NotificationType.ERROR)
                .notify(project);
    }

    private String getExceptionInformation(Exception exception) {
        StringBuilder sb = new StringBuilder();

        sb.append(exception.getClass().toString() + ": " + exception.getMessage());
        sb.append(System.lineSeparator());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        sb.append(sw.toString());

        return sb.toString();
    }
}
