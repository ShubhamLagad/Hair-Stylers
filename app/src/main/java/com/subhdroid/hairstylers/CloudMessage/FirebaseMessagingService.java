package com.subhdroid.hairstylers.CloudMessage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.subhdroid.hairstylers.Customer.CustomerDashboard;
import com.subhdroid.hairstylers.Parlour.ParlourLogin;
import com.subhdroid.hairstylers.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

//    NotificationManager mNotificationManager;
    private static final String channel_id = "Service remainder channel";
    private static final int req_code = 100;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(false);
        }

        // vibration
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);




//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder.setSmallIcon(R.drawable.icontrans);
//            builder.setSmallIcon(R.drawable.ic_baseline_email_24);
//        } else {
////            builder.setSmallIcon(R.drawable.icon_kritikar);
//            builder.setSmallIcon(R.drawable.ic_baseline_email_24);
//        }
//
//
//        Intent resultIntent = new Intent(this, ParlourLogin.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        builder.setContentTitle(remoteMessage.getNotification().getTitle());
//        builder.setContentText(remoteMessage.getNotification().getBody());
//        builder.setContentIntent(pendingIntent);
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
//        builder.setAutoCancel(true);
//        builder.setPriority(Notification.PRIORITY_MAX);
//
//        mNotificationManager =
//                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "Your_channel_id";
//            NotificationChannel channel = new NotificationChannel(
//                    channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channelId);
//        }
//
//
//// notificationId is a unique int for each notification that you must define
//        mNotificationManager.notify(100, builder.build());



//        ========================================
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                channel_id);


        Intent intent = new Intent(this, CustomerDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,req_code,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationBuilder.setSmallIcon(R.mipmap.hair_stylers_icon)
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)  //for dismiss notification
                    .setChannelId(channel_id)
                    .build();

            nm.createNotificationChannel(new NotificationChannel(channel_id,"Service remainder",
                    NotificationManager.IMPORTANCE_HIGH));
        }else {
            notificationBuilder.setSmallIcon(R.mipmap.hair_stylers_icon)
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        nm.notify(1,notificationBuilder.build());

    }
}