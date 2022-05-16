package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.R

/**
 * Builds and Delivers Notificationos
 **/

val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, status: String, applicationContext: Context) {

//    println("Testing Notification")
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("repo", messageBody)
    contentIntent.putExtra("downloadState", status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationAction = NotificationCompat.Action(
        R.drawable.ic_baseline_pageview_24,
        "Status Page",
        contentPendingIntent
    )

    val downloadIcon = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.img
    )

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(downloadIcon)
        .bigLargeIcon(null)

    //Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    )
        // set title, text, and icon to builder
        .setSmallIcon(R.drawable.ic_baseline_downloading_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(bigPicStyle)
        .setLargeIcon(downloadIcon)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(notificationAction)

    //call notify
//    with(NotificationManagerCompat.from(applicationContext)) {
    notify(NOTIFICATION_ID, builder.build())
//    }

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}