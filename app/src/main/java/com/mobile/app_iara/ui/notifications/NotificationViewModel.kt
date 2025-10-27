package com.mobile.app_iara.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.local.AppDatabase
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository

    val notifications: LiveData<List<NotificationEntity>>
    private val _pendingApprovals = MutableLiveData<List<ApprovalModal>>()
    val pendingApprovals: LiveData<List<ApprovalModal>> = _pendingApprovals

    init {
        val dao = AppDatabase.getDatabase(application).notificationDAO()
        repository = NotificationRepository(dao)
        notifications = repository.allNotifications
        loadPendingApprovals()
    }

    fun addNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.saveNotification(notification)
        }
    }

    private fun loadPendingApprovals() {
        val mockApprovals = listOf(
            ApprovalModal(time = "Registrado às 14:30", link = "Ver"),
            ApprovalModal(time = "Registrado às 11:15", link = "Ver"),
            ApprovalModal(time = "Registrado às 09:02", link = "Ver")
        )
        _pendingApprovals.value = mockApprovals
    }
}