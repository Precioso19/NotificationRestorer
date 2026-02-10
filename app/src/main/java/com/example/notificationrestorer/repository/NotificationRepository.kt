package com.example.notificationrestorer.repository

import android.content.Context
import com.example.notificationrestorer.model.SavedNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NotificationRepository private constructor(context: Context) {

    private val _notifications = MutableStateFlow<List<SavedNotification>>(emptyList())
    val notifications: StateFlow<List<SavedNotification>> = _notifications.asStateFlow()

    private val mutex = Mutex()
    private val maxNotifications = 100

    companion object {
        @Volatile
        private var instance: NotificationRepository? = null

        fun getInstance(context: Context): NotificationRepository {
            return instance ?: synchronized(this) {
                instance ?: NotificationRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    suspend fun saveNotification(notification: SavedNotification) {
        mutex.withLock {
            val currentList = _notifications.value.toMutableList()
            
            // Evitar duplicados
            currentList.removeAll { it.id == notification.id }
            
            // Añadir la nueva notificación al principio
            currentList.add(0, notification)
            
            // Limitar el número de notificaciones guardadas
            if (currentList.size > maxNotifications) {
                currentList.subList(maxNotifications, currentList.size).clear()
            }
            
            _notifications.value = currentList
        }
    }

    suspend fun markAsDismissed(key: String) {
        mutex.withLock {
            val currentList = _notifications.value.toMutableList()
            val index = currentList.indexOfFirst { "${it.packageName}_${it.id}_${it.timestamp}" == key }
            
            if (index != -1) {
                currentList[index] = currentList[index].copy(isDismissed = true)
                _notifications.value = currentList
            }
        }
    }

    suspend fun deleteNotification(id: Long) {
        mutex.withLock {
            _notifications.value = _notifications.value.filter { it.id != id }
        }
    }

    suspend fun clearAll() {
        mutex.withLock {
            _notifications.value = emptyList()
        }
    }

    suspend fun clearDismissed() {
        mutex.withLock {
            _notifications.value = _notifications.value.filter { !it.isDismissed }
        }
    }

    fun getActiveNotifications(): List<SavedNotification> {
        return _notifications.value.filter { !it.isDismissed }
    }

    fun getDismissedNotifications(): List<SavedNotification> {
        return _notifications.value.filter { it.isDismissed }
    }
}
