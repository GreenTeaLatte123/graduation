package com.example.test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID_CLASS1 = "class1_channel";
    private static final String CHANNEL_ID_CLASS2 = "class2_channel";
    private static final String CHANNEL_ID_LIGHT  = "light_status";
    private static final String DEFAULT_CHANNEL_ID = "default_channel_id";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("tag", "Refreshed tokens: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 메시지의 알림 페이로드가 있는지 확인
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            // 알림을 생성
            sendNotification(title, message);
        }

        // 메시지의 데이터 페이로드가 있는지 확인
        if (remoteMessage.getData().size() > 0) {
            Log.d("tag", "Message data payload: " + remoteMessage.getData());
        }
    }

    private void sendNotification(String title, String messageBody) {
        // 각 채널에 따라 다른 채널 ID 사용
        String channelId;
        if (title.equals("Class 1 Detected")) {
            channelId = CHANNEL_ID_CLASS1;
        } else if (title.equals("Class 2 Detected")) {
            channelId = CHANNEL_ID_CLASS2;
        } else if (title.equals("light_status")) {
            channelId = CHANNEL_ID_LIGHT;
        }
        else {
            channelId = DEFAULT_CHANNEL_ID; // 기본적으로 사용할 채널 ID
        }

        // 알림을 클릭했을 때 MainActivity로 이동
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // 알림 빌더 생성
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        // 알림 매니저 가져오기
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Oreo 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName;
            int importance;
            if (channelId.equals(CHANNEL_ID_CLASS1)) {
                channelName = "Class 1 Detection Channel";
                importance = NotificationManager.IMPORTANCE_HIGH;
            } else if (channelId.equals(CHANNEL_ID_CLASS2)) {
                channelName = "Class 2 Detection Channel";
                importance = NotificationManager.IMPORTANCE_HIGH;
            } else if (channelId.equals(CHANNEL_ID_LIGHT)) {
                channelName = "Light Status Update";
                importance = NotificationManager.IMPORTANCE_HIGH;
            } else {
                channelName = "Default Channel";
                importance = NotificationManager.IMPORTANCE_DEFAULT;
            }
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // 고유한 ID를 부여하여 알림을 전송
        int notificationId = (int) System.currentTimeMillis(); // 시간을 기반으로 고유 ID 생성
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
