package com.example.notificationrestorer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationrestorer.model.SavedNotification
import com.example.notificationrestorer.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SortedBy
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotificationRepository.getInstance(application)

    data class UiState(
        val activeNotifications: List<SavedNotification> = emptyList(),
        val dismissedNotifications: List<SavedNotification> = emptyList(),
        val filterQuery: String = "",
        val selectedTab: Tab = Tab.ACTIVE,
        val sortBy: SortBy = SortBy.TIME_DESC
    )

    enum class Tab {
        ACTIVE, DISMISSED
    }

    enum class SortBy {
        TIME_DESC, TIME_ASC, APP_NAME
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.notifications.collect { notifications ->
                updateNotifications(notifications)
            }
        }
    }

    private fun updateNotifications(notifications: List<SavedNotification>) {
        val currentState = _uiState.value
        
        val active = notifications.filter { !it.isDismissed }
        val dismissed = notifications.filter { it.isDismissed }
        
        _uiState.value = currentState.copy(
            activeNotifications = filterAndSort(active, currentState.filterQuery, currentState.sortBy),
            dismissedNotifications = filterAndSort(dismissed, currentState.filterQuery, currentState.sortBy)
        )
    }

    fun setSelectedTab(tab: Tab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun setFilterQuery(query: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            filterQuery = query,
            activeNotifications = filterAndSort(
                repository.getActiveNotifications(),
                query,
                currentState.sortBy
            ),
            dismissedNotifications = filterAndSort(
                repository.getDismissedNotifications(),
                query,
                currentState.sortBy
            )
        )
    }

    fun setSortBy(sortBy: SortBy) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            sortBy = sortBy,
            activeNotifications = filterAndSort(
                repository.getActiveNotifications(),
                currentState.filterQuery,
                sortBy
            ),
            dismissedNotifications = filterAndSort(
                repository.getDismissedNotifications(),
                currentState.filterQuery,
                sortBy
            )
        )
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun clearDismissed() {
        viewModelScope.launch {
            repository.clearDismissed()
        }
    }

    fun restoreNotification(notification: SavedNotification) {
        try {
            notification.pendingIntent?.send()
        } catch (e: Exception) {
            // Manejar error
        }
    }

    private fun filterAndSort(
        notifications: List<SavedNotification>,
        query: String,
        sortBy: SortBy
    ): List<SavedNotification> {
        var filtered = if (query.isBlank()) {
            notifications
        } else {
            notifications.filter {
                it.appName.contains(query, ignoreCase = true) ||
                        it.title?.contains(query, ignoreCase = true) == true ||
                        it.text?.contains(query, ignoreCase = true) == true
            }
        }

        return when (sortBy) {
            SortBy.TIME_DESC -> filtered.sortedByDescending { it.timestamp }
            SortBy.TIME_ASC -> filtered.sortedBy { it.timestamp }
            SortBy.APP_NAME -> filtered.sortedBy { it.appName }
        }
    }
}
