package com.mycompany.chatapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
        var s = firebaseUser?.uid
        Log.d("fff",s!!)

        if (firebaseUser != null && sented.equals(firebaseUser.uid)) {
            sendNotification(message)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var user: String? = remoteMessage.data["user"]
        var icon: String? = remoteMessage.data["icon"]
        var title: String? = remoteMessage.data["title"]
        var body: String? = remoteMessage.data["body"]

        val j: Int = user?.replace("[\\D]".toRegex(), "")!!.toInt()
        var intent  = Intent(this, MessageActivity::class.java)
        var bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_MUTABLE)

        var defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
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