package com.example.loginregister.push_alram;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.loginregister.MainActivity;
import com.example.loginregister.Notice_B.Post_Comment;
import com.example.loginregister.R;
import com.example.loginregister.login.PreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationManager pushManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private Alarms data;
    private ArrayList<Alarm> alarms;

    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

    }
    // [END on_new_token]

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
         Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                Log.e(TAG,"###############");
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if(PreferencesManager.getAccount(getApplicationContext()).equals(mAuth.getUid())) {
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notification", true)) {
                Log.e("notification", "received");
                pushManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                sendNotification(remoteMessage.getData());
                addToAlarm(remoteMessage.getData());

            }
        }

    }
    // [END receive_message]

    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }


    private void sendNotification(Map message) {

        NotificationChannel pushChannel = null;
        NotificationChannel nonChannel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            pushChannel = new NotificationChannel("push_channel", "푸쉬 알림", NotificationManager.IMPORTANCE_HIGH);
            pushChannel.enableLights(true);
            pushChannel.setLightColor((getColor(R.color.colorAccent)));
            pushChannel.setDescription("푸쉬 알림");
            pushManager.createNotificationChannel(pushChannel);
            Log.e("notification","channel");

            nonChannel = new NotificationChannel("non_channel","상단바 알림",NotificationManager.IMPORTANCE_DEFAULT);
            nonChannel.enableLights(true);
            nonChannel.setLightColor((getColor(R.color.colorAccent)));
            nonChannel.setDescription("상단바 알림");
            pushManager.createNotificationChannel(nonChannel);
        }


        Intent intent = new Intent(this, Post_Comment.class);

        intent.putExtra("forum_sort",message.get("forum_sort").toString());
        intent.putExtra("post_id",message.get("post_id").toString());
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId;
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("push",true)) {
            channelId = "push_channel";
        }
        else {
            channelId = "non_channel";
        }

        @SuppressLint("ResourceAsColor") NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.dkdlzhs   ) // 아이콘
                .setContentTitle(message.get("title").toString()) // 제목
                .setContentText(message.get("body").toString()) // 내용
                .setAutoCancel(true)//누르면사라짐
                .setColor((ContextCompat.getColor(this, R.color.btn_theme))) //상단바 아이콘이랑 이름 색
                .setColorized(true)
                .setContentIntent(pendingIntent)//클릭하면 이동할 곳
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
            Log.e("alarm",PreferencesManager.getAccount(getApplicationContext()));
            Log.e("alarm",mAuth.getUid());
            if(PreferencesManager.getAccount(getApplicationContext()).equals(mAuth.getUid())) {
                Log.e("alarm","일치");
                pushManager.notify(createID(), builder.build());

            }

    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.KOREA).format(now));
        return id;
    }

    public void addToAlarm(Map message){
        long datetime = System.currentTimeMillis();
        Date date = new Date(datetime);
        Timestamp timestamp = new Timestamp(date);
        mStore.collection("Alarm").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data = documentSnapshot.toObject(Alarms.class);
                alarms = data.getAlarms();
                Alarm alarm = new Alarm(message.get("title").toString(),timestamp,message.get("forum_sort").toString(),message.get("post_id").toString(),false);
                alarms.add(alarm);
                data.setAlarms(alarms);
                mStore.collection("Alarm").document(mAuth.getUid()).set(data);

            }
        });

    }


}