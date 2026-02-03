package com.notloco.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid

@HiltAndroidApp
class NotLocoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Sentry
        SentryAndroid.init(this) { options ->
            options.dsn = "https://cbe49d6c9a819291033c924bda80a3ff@o4507355321663488.ingest.us.sentry.io/4507355325595648"
            options.isDebug = BuildConfig.DEBUG
        }
        
        // Create notification channel
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notloco_channel"
            val channelName = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = getString(R.string.notification_channel_desc)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notloco_channel"
    }
}
