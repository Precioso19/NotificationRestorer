package com.example.notificationrestorer.service

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.notificationrestorer.model.SavedNotification
import com.example.notificationrestorer.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: NotificationRepository

    companion object {
        private const val TAG = "NotificationListener"
        const val ACTION_NOTIFICATION_POSTED = "com.example.notificationrestorer.NOTIFICATION_POSTED"
        const val ACTION_NOTIFICATION_REMOVED = "com.example.notificationrestorer.NOTIFICATION_REMOVED"
    }

    override fun onCreate() {
        super.onCreate()
        repository = NotificationRepository.getInstance(applicationContext)
        Log.d(TAG, "NotificationListenerService creado")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val notification = sbn.notification ?: return
            
            // Filtrar notificaciones propias y del sistema
            if (sbn.packageName == packageName || shouldIgnoreNotification(sbn)) {
                return
            }

            val savedNotification = createSavedNotification(sbn, notification)
            
            serviceScope.launch {
                repository.saveNotification(savedNotification)
                Log.d(TAG, "Notificación guardada: ${savedNotification.appName}")
                
                // Broadcast para actualizar la UI
                sendBroadcast(Intent(ACTION_NOTIFICATION_POSTED))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al procesar notificación", e)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        try {
            val key = "${sbn.packageName}_${sbn.id}_${sbn.postTime}"
            
            serviceScope.launch {
                repository.markAsDismissed(key)
                Log.d(TAG, "Notificación marcada como eliminada")
                
                sendBroadcast(Intent(ACTION_NOTIFICATION_REMOVED))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al marcar notificación eliminada", e)
        }
    }

    private fun createSavedNotification(
        sbn: StatusBarNotification,
        notification: Notification
    ): SavedNotification {
        val extras = notification.extras
        val appName = getAppName(sbn.packageName)
        
        return SavedNotification(
            id = sbn.postTime,
            packageName = sbn.packageName,
            appName = appName,
            title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
            bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
            icon = getNotificationIcon(sbn),
            timestamp = sbn.postTime,
            pendingIntent = notification.contentIntent,
            isOngoing = notification.flags and Notification.FLAG_ONGOING_EVENT != 0,
            category = notification.category,
            priority = notification.priority
        )
    }

    private fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    private fun getNotificationIcon(sbn: StatusBarNotification): Bitmap? {
        return try {
            val icon = sbn.notification.smallIcon
            val drawable = icon.loadDrawable(this)
            drawableToBitmap(drawable)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener icono", e)
            null
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun shouldIgnoreNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        
        // Ignorar notificaciones del sistema de Android
        if (sbn.packageName.startsWith("com.android")) {
            return true
        }
        
        // Ignorar notificaciones sin contenido
        if (notification.extras.getCharSequence(Notification.EXTRA_TITLE) == null &&
            notification.extras.getCharSequence(Notification.EXTRA_TEXT) == null) {
            return true
        }
        
        return false
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
        Log.d(TAG, "NotificationListenerService destruido")
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "NotificationListenerService conectado")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "NotificationListenerService desconectado")
    }
}
