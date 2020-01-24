package com.project.segunfrancis.notifymekotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        const val NOTIFICATION_ID = 0
        const val ACTION_UPDATE_NOTIFICATION =
            "com.project.segunfrancis.notifymekotlin.ACTION_UPDATE_NOTIFICATION"
    }

    private var mReceiver = NotificationReceiver()
    private lateinit var mNotifyManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))

        createNotificationChannel()
        button_notify.setOnClickListener {
            sendNotification()
        }
        button_update.setOnClickListener {
            updateNotification()
        }
        button_cancel.setOnClickListener {
            cancelNotification()
        }
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            updateIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated")
        )
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
    }

    private fun cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Test Notification from Mascot"
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifyBuilder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
        notifyBuilder.apply {
            setContentTitle("You've been notified")
            setContentText("This is your notification text.")
            setSmallIcon(R.drawable.ic_android)
            setContentIntent(notificationPendingIntent)
            setAutoCancel(true)
            //Setting auto-cancel to true closes the notification when user taps on it.
            priority = NotificationCompat.PRIORITY_MAX
            setDefaults(NotificationCompat.DEFAULT_ALL)
            // The high-priority notification will not drop down in front
            // of the active screen unless both the priority and the defaults
            // are set. Setting the priority alone is not enough.
        }
        return notifyBuilder
    }

    private fun setNotificationButtonState(
        isNotifyEnabled: Boolean,
        isUpdateEnabled: Boolean,
        isCancelEnabled: Boolean
    ) {
        button_notify.isEnabled = isNotifyEnabled
        button_update.isEnabled = isUpdateEnabled
        button_cancel.isEnabled = isCancelEnabled
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNotification()
        }
    }
}
