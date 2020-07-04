package impl.services;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import impl.Helper;
import interfaces.INotificationService;

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
    public void notify(Project project, Exception exception) {
        notificationGroup
                .createNotification(Helper.getExceptionInformation(exception), NotificationType.ERROR)
                .notify(project);
    }
}
