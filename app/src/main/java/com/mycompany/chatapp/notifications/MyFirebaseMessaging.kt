package com.mycompany.chatapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mycompany.chatapp.ui.MessageActivity

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var sented: String? = message.data["sented"]
        var user: String? = message.data["user"]

        var preferences: SharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        var currentUser: String? = preferences.getString("currentuser", "none")

        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented.equals(firebaseUser.uid)) {
            if (!currentUser.equals(user)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(message)
                } else {
                    sendNotification(message)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendOreoNotification(remoteMessage: RemoteMessage) {
        val user: String? = remoteMessage.data["user"]
        val icon: String = remoteMessage.data["icon"]!!
        val title: String? = remoteMessage.data["title"]
        val body: String? = remoteMessage.data["body"]

        val j: Int = user?.replace("[\\D]".toRegex(), "")!!.toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_IMMUTABLE)

        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var myNotificationManager = MyNotificationManager(this)
        var builder: Notification.Builder = myNotificationManager.getMyNotification(
            title!!,
            body!!,
            pendingIntent,
            defaultSound,
            icon
        )

        var i = 0
        if (j > 0) {
            i = j
        }
        myNotificationManager.getManager().notify(i, builder.build())

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val user: String? = remoteMessage.data["user"]
        val icon: String = remoteMessage.data["icon"]!!
        val title: String? = remoteMessage.data["title"]
        val body: String? = remoteMessage.data["body"]

        val j: Int = user?.replace("[\\D]".toRegex(), "")!!.toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_IMMUTABLE)

        val defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val newNotification: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if (j > 0) {
            i = j
        }
        newNotification.notify(i, builder.build())

    }
}