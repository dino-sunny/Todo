package com.dino.todo.home

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dino.todo.R
import com.dino.todo.utility.Constants.ConstantVariables.NOTIFICATION_CHANNEL
import com.dino.todo.utility.Constants.ConstantVariables.TODO_DESCRIPTION
import com.dino.todo.utility.Constants.ConstantVariables.TODO_TITLE


//Broadcast receiver for the alarm, which delivers the notification.
class AlarmReceiver : BroadcastReceiver() {
    private var mNotificationManager: NotificationManager? = null

    override fun onReceive(context: Context, intent: Intent) {
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Deliver the notification.
        deliverNotification(context, intent)
    }

    private fun deliverNotification(context: Context, intent: Intent) {
        // Create the content intent for the notification, which launches
        // home activity
        val contentIntent = Intent(context, HomeActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Build the notification
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_todo)
                .setContentTitle(intent.getStringExtra(TODO_TITLE))
                .setContentText(intent.getStringExtra(TODO_DESCRIPTION))
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Deliver the notification
        mNotificationManager!!.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        // Notification ID.
        private const val NOTIFICATION_ID = 0

        // Notification channel ID.
        private const val PRIMARY_CHANNEL_ID = NOTIFICATION_CHANNEL
    }
}