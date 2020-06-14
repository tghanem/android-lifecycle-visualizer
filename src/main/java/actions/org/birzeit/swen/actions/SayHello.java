package actions.org.birzeit.swen.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.popup.JBPopupFactory;

public class SayHello extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        NotificationGroup group = new NotificationGroup("Errors", NotificationDisplayType.BALLOON, true);
        Notification notification = group.createNotification("Hello World!", NotificationType.INFORMATION);
        notification.notify(null);
    }
}
