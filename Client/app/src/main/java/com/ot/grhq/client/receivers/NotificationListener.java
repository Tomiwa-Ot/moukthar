package com.ot.grhq.client.receivers;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        super.onNotificationPosted(sbn);

        String packageName = sbn.getPackageName();
        String content = extractNotificationContent(sbn);

        // Send notification data to WebSocketService using a broadcast intent
        Intent intent = new Intent();
        intent.setAction("notification_data");
        intent.putExtra("package_name", packageName);
        intent.putExtra("notification_content", content);
        sendBroadcast(intent);
    }

    /**
     * Get notification content
     * @param sbn
     * @return notification content
     */
    private String extractNotificationContent(StatusBarNotification sbn) {
        // Access the Notification object from StatusBarNotification
        android.app.Notification notification = sbn.getNotification();

        // Extract content from the Notification
        // Note: The way to extract content may vary depending on the notification's content structure
        if (notification != null && notification.extras != null) {
            // Example: Extracting text content
            CharSequence notificationText = notification.extras.getCharSequence(android.app.Notification.EXTRA_TEXT);
            if (notificationText != null) {
                return notificationText.toString();
            }
        }

        return null;
    }
}
