package impl;

import com.intellij.notification.NotificationGroup;
import interfaces.INotificationController;

public class NotificationController implements INotificationController {
    private final NotificationGroup notificationGroup;

    public NotificationController(NotificationGroup notificationGroup) {
        this.notificationGroup = notificationGroup;
    }

    @Override
    public void Notify(Exception exception) {

    }
}
