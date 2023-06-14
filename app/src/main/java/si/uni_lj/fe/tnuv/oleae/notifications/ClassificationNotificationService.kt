package si.uni_lj.fe.tnuv.oleae.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import si.uni_lj.fe.tnuv.oleae.Home
import si.uni_lj.fe.tnuv.oleae.R


class ClassificationNotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(detection: String) {
        val activityIntent = Intent(context, Home::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(context, CLASSIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.mosquito_34)
            .setContentTitle("Mosquito detection")
            .setContentText(
                if (detection == "1") {
                    "Mosquito detected!"
                } else {
                    "Currently not detected!"
                }
            )
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            1, notification
        )
    }

    companion object {
        const val CLASSIFICATION_CHANNEL_ID = "classification_channel"
    }
}