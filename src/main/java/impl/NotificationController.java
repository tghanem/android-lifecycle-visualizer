package impl;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import interfaces.INotificationController;

public class NotificationController implements INotificationController {
    private final NotificationGroup notificationGroup;
    private final Project project;

    public NotificationController(NotificationGroup notificationGroup, Project project) {
        this.notificationGroup = notificationGroup;
        this.project = project;
    }

    @Override
    public void Notify(Exception exception) {
        notificationGroup
                .createNotification(Helper.getExceptionInformation(exception), NotificationType.ERROR)
                .notify(project);
    }
}
