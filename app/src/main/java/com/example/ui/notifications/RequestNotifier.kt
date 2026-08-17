package com.example.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.prefs.AppPreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.ui.Localization

/**
 * System notifications for service-request updates.
 *
 * The app refreshes its requests every 30 seconds in the background when the
 * user is signed in, and raises a system notification whenever a new request
 * arrives (craftsman) or a request's status changes (customer).
 */
object RequestNotifier {

    private const val CHANNEL_ID = "herafi_requests"
    private const val NOTIFICATION_ID = 1042

    fun notify(context: Context, title: String, body: String) {
        ensureChannel(context)
        if (!hasPermission(context)) return

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pending = PendingIntent.getActivity(context, NOTIFICATION_ID, launchIntent, flags)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Permission revoked at runtime; ignore quietly.
        }
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                val savedLang = runBlocking {
                    AppPreferencesManager.language(context).first()
                }
                val channelLang = AppLanguage.entries.firstOrNull { it.code == savedLang } ?: AppLanguage.AR
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    Localization.Ui.text("new_notification_title", channelLang),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = Localization.Ui.text("new_notification_body", channelLang)
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    private fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
