
package com.smartline.smartline;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.provider.CalendarContract.EXTRA_EVENT_ID;

public class MainActivityWear extends WearableActivity {

    private TextView mTextView;
    private Button button;
    public NotificationManagerCompat notificationManager;
    public int notificationId;
    public NotificationCompat.Builder notificationBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);

        button = findViewById(R.id.button);
        mTextView = findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        notificationId = 001;
        // The channel ID of the notification.
        String id = "my_channel_01";
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivityWear.class);
        viewIntent.putExtra(EXTRA_EVENT_ID, 0);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create Notification Action
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,"Smartline", viewPendingIntent).build();

        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.
        notificationBuilder =
                new NotificationCompat.Builder(this, id)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Smartline")
                        .setContentText("Description")
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action));


        // Get an instance of the NotificationManager service
        notificationManager =
                NotificationManagerCompat.from(this);


        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // Issue the notification with notification manager.
                notificationManager.notify(001, notificationBuilder.build());
            }
        });

    }
}
