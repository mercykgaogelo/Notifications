package com.projectpractice.notifyme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // CREATING A MEMBER VARIABLE FOR THE NOTIFY ME BUTTON
    private Button button_notify;
    private Button button_cancel;
    private Button button_update;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    // creating a constant notification (channel ID channel is used to post notification)
    //every notification channel must be associsted with an ID that is unique within your package
    private static final String PRIMARY_CHANNEL_ID ="primary_notification_channel";
    // create a unique constant member variable to represent the update notification action for your broadcast.
    // Make sure to prefix the variable value with your app's package name to ensure its uniqueness:
    private static final String ACTION_UPDATE_NOTIFICATION ="com.projectpractice.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION = "com.projectpractice.notifyme.ACTION_CANCEL_NOTIFICATION";

    //NotificationManager class are use to deliver notification to users.
    //member variable to store NotificationManager object
    private NotificationManager mNotifyManager;
    //constant notification id to update or cancel
    private static final int NOTIFICATION_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //INITIALIZING THE button_notify
        button_notify = findViewById(R.id.notify);
        //CREATING onClickListener for button_notify
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CALLING THE sendNotification() method
                sendNotification();
            }
        });
        //CALLING METHOD createNotificationChannel
        createNotificationChannel();
        button_update = findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();

            }
        });
        button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               cancelNotification();
            }
        });
         setNotificationButtonState(true,false,false);
         //To receive the ACTION_UPDATE_NOTIFICATION intent, register your broadcast receiver in the onCreate() method:
         registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
         registerReceiver(mReceiver,new IntentFilter(ACTION_CANCEL_NOTIFICATION));
    }
    //To unregister your receiver, override the onDestroy() method of your Activity:

                @Override
                 protected void onDestroy() {
                 unregisterReceiver(mReceiver);
                 super.onDestroy();
    }

    //CREATING A METHOD STUB FOR THE SendNotification()METHOD
    //getting the builder object using getNotificationBuilder() method
    //Notifications are created using the NotificationCompat.Builder
    //Icon (required), which you set in your code using the setSmallIcon() method.
    //Title (optional), which you set using setContentTitle().
    //Detail text (optional), which you set using setContentText()
    public void sendNotification(){
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        //Use getBroadcast() to get a PendingIntent. To make sure that this
        // pending intent is sent and used only once, set FLAG_ONE_SHOT.
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(NOTIFICATION_ID,notifyBuilder.build());
        setNotificationButtonState(false,true,true);
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification",updatePendingIntent);
    }

    //CREATING createNotificationChannel() method
    public void createNotificationChannel(){
        // instantiate the NotificationManager member variable
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //condition to check for the device API version(channels available in API 26 and higher)
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    //create notification channel
                         //construct notification channel object and use PRIMARY_CHANNEL_ID AS channel id
                    //set the channel name and the importance to high
                    //mascot notification
                    NotificationChannel notificationChannel = new
                            NotificationChannel(PRIMARY_CHANNEL_ID,
                            "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
                    //configure notifications
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setDescription("Notification from Mascot");
                    mNotifyManager.createNotificationChannel(notificationChannel);
                }
    }
    //Content intents can be explicit intents to launch an activity
    //implicit intent perform an action, or broadcast intents to notify the system of a system event or custom event
    //create method called getNotificationBuilder()
    private NotificationCompat.Builder getNotificationBuilder(){
        //creating explicit intent method to launch mainActivity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        //getting pendingIntent using getActivity(using getActivity() method to get pendingIntent and passing the
        //notification Id for requestCode and use FLAG_UPDATE_CURRENT FLAG
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //instantiate the notification builder for notification channel id
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this,PRIMARY_CHANNEL_ID)
                          .setContentTitle("You've been notified!")
                          .setContentText("This is your notification text.")
                          .setSmallIcon(R.drawable.ic_android).setContentIntent(notificationPendingIntent)
                                      //  .setAutoCancel(true)
                           .setContentIntent(notificationPendingIntent)
                           .setPriority(NotificationCompat.PRIORITY_HIGH) //Priority is an integer value from PRIORITY_MIN (-2) to
                                                               // PRIORITY_MAX (2). Notifications with a higher priority are sorted
                           .setDefaults(NotificationCompat.DEFAULT_ALL)
                          .setAutoCancel(true);
        return notifyBuilder;
    }
    //BigPictureStyle allow you to include an image in the notification
    //BigPictureStyle is a subclass of NotificationCompat.Style which provide alternative layouts
    public void updateNotification(){
        Bitmap androidImage = BitmapFactory
        .decodeResource(getResources(),R.drawable.mascot_1);
        //using getNotificationBuilder to get NotificationCompat.Builder
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setStyle(new NotificationCompat.InboxStyle()
                               .addLine("Notifiation1")
                               .addLine("notification11").setSummaryText("my notification"));
       // notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    //      .bigPicture(androidImage)
                    //      .setBigContentTitle("Notification"));
        //building the notification and call notify() on the notificationManager pass in the same notification ID
        //Create another pending intent to let the app know that
        // the user has dismissed the notification, and toggle the button states accordingly


       // Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
       // PendingIntent cancelPendingIntent = PendingIntent.getBroadcast
       //         (this, NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);
     //   notifyBuilder.setDeleteIntent(cancelPendingIntent);

        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false,false,true);
    }
    public void cancelNotification(){
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true,false,false);
    }
     void setNotificationButtonState(Boolean isNotifyEnabled,
                                     Boolean isUpdateEnabled,
                                     Boolean isCancelEnabled){
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);

    }
    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}