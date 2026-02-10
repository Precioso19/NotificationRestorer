package com.example.notificationrestorer.model

import android.app.PendingIntent
import android.graphics.Bitmap

data class SavedNotification(
    val id: Long = System.currentTimeMillis(),
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val subText: String?,
    val bigText: String?,
    val icon: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val pendingIntent: PendingIntent? = null,
    val isDismissed: Boolean = false,
    val isOngoing: Boolean = false,
    val category: String? = null,
    val priority: Int = 0
) {
    fun getDisplayTitle(): String = title ?: appName
    
    fun getDisplayText(): String = when {
        bigText != null -> bigText
        text != null -> text
        subText != null -> subText
        else -> "Sin contenido"
    }
    
    fun getFormattedTimestamp(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60_000 -> "Hace un momento"
            diff < 3600_000 -> "Hace ${diff / 60_000} min"
            diff < 86400_000 -> "Hace ${diff / 3600_000} h"
            else -> "Hace ${diff / 86400_000} días"
        }
    }
}
