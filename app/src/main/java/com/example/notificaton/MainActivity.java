package com.example.notificaton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {
    static final String TXT_REPLY = "text_reply";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;
        button = findViewById(R.id.btn1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                displayDownload();
                replyDirect();
                notification();
            }
        });



    }

//    inorder to see notification android version above 8, we need to define the channel
    private void createNotificationChannel(){
//        check for android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.directReplyTitle);
            String description = getString(R.string.directReplyText);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void replyDirect(){

        Intent directReply = new Intent(this, directReply.class);
        directReply.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent directReplyIntent = PendingIntent.getActivity(this, 0, directReply, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.channel_name))
                .setContentText(getString(R.string.channel_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            RemoteInput remoteInput = new RemoteInput.Builder(TXT_REPLY).setLabel("Reply").build();
            Intent replyIntent = new Intent(this, directReply.class);
            replyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_stat_name, "Reply",
                    replyPendingIntent).addRemoteInput(remoteInput).build();

            builder.addAction(action);
        }

        NotificationManagerCompat.from(this).notify(3, builder.build());
    }



    private void notification(){

        Intent intent = new Intent(this, Notification_content.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent ReplyIntent = new Intent(this, Reply.class);
        ReplyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 0, ReplyIntent, 0);

        Intent DismissIntent = new Intent(this, Dismiss.class);
        DismissIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent dismissPendingIntent = PendingIntent.getActivity(this, 0, DismissIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.channel_name))
                .setContentText(getString(R.string.channel_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                set ReplyIntent that will fire when user click on event
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.reply), replyPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.dismiss), dismissPendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat.from(this).notify(1, builder.build());

    }



    private void displayDownload(){

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setContentText(getString(R.string.downloadText))
            .setContentTitle(getString(R.string.downloadTitle))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        final int progress_max = 100;
        int progress_current = 0;
        builder.setProgress(progress_max, progress_current, false);
        notificationManager.notify(2, builder.build());
        Thread thread = new Thread(){
            public void run(){
                int count = 0;
                try {
                    while (count <= 100){
                        count += 10;
                        sleep(1000);
                        builder.setProgress(progress_max, count, false);
                        notificationManager.notify(2, builder.build());
                    }
                    builder.setContentText(getString(R.string.downloadComplete));
                    builder.setProgress(0,0, false);
                    notificationManager.notify(2, builder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }



}
