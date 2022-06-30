package com.mycompany.chatapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mycompany.chatapp.MessageActivity

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var sented: String? = message.data["sented"]

        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented.equals(firebaseUser.uid)) {
            sendNotification(message)
        }

    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var user: String? = remoteMessage.data["user"]
        var icon: String? = remoteMessage.data["icon"]
        var title: String? = remoteMessage.data["title"]
        var body: String? = remoteMessage.data["body"]

        var notification: RemoteMessage.Notification? = remoteMessage.notification

        var j: Int = Integer.parseInt(user?.replace("[\\D]", ""))
        var intent: Intent = Intent(this, MessageActivity::class.java)
        var bundle: Bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        var defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(notification?.title)
            .setContentText(notification?.body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        var newNotification: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if (j > 0) {
            i = j
        }
        newNotification.notify(i, builder.build())

    }
}