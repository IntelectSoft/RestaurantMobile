package md.edi.mobilewaiter.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import md.edi.mobilewaiter.R
import md.edi.mobilewaiter.data.database.models.NotificationModel
import md.edi.mobilewaiter.data.database.repository.RepositoryNotification
import md.edi.mobilewaiter.presentation.launch.LaunchActivity
import md.edi.mobilewaiter.presentation.main.MainActivity
import md.edi.mobilewaiter.utils.HelperApp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotificationChannel()
        val notifyRepository = RepositoryNotification(this)


        val data: Map<String, String> = message.data

        val messageBody = data["body"]
        val messageTitle = data["title"]
        val billId = data["billId"]

        val notificationTime = System.currentTimeMillis()
        val notificationId = Random().nextInt(9999)

        Log.e("TAG", " generated notificationId: $notificationId")

        CoroutineScope(Dispatchers.IO).launch {

            notifyRepository.insertNotification(
                NotificationModel(
                    tag = notificationTime.toString(),
                    title = messageTitle ?: "",
                    body = messageBody ?: "",
                    billId = billId ?: "",
                    viewed = 1,
                    viewedTime = ""
                )
            )
        }
        val notifyIntent =
            if (HelperApp.isAppRunning(this)) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LaunchActivity::class.java)
            }
        notifyIntent.putExtra("billId", billId)
        notifyIntent.putExtra("tag", notificationTime.toString())
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val notifyPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "44").setContentTitle(messageTitle)
                .setContentText(messageBody).setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notifyPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.icon_product_done).setShowWhen(true).setOngoing(true)
                .setAutoCancel(true).setWhen(notificationTime).setSound(defaultSoundUri)

        // notificationId is a unique int for each notification that you must define
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, mBuilder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Notification"
            val description = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("44", name, importance)
            channel.description = description
            channel.lightColor = Color.GREEN
            channel.enableLights(true)
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}