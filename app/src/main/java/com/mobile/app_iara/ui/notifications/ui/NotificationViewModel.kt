package com.mobile.app_iara.ui.notifications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.local.AppDatabase
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import com.mobile.app_iara.data.repository.NotificationRepository
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.notifications.data.NotificationEntity
import com.mobile.app_iara.util.Event
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository
    val notifications: LiveData<List<NotificationEntity>>

    private val abacusPhotoRepository: AbacusPhotoRepository
    private val userRepository: UserRepository

    private val _pendingApprovals = MutableLiveData<List<AbacusPhotoData>>()
    val pendingApprovals: LiveData<List<AbacusPhotoData>> = _pendingApprovals

    private val _userMap = MutableLiveData<Map<String, String>>()
    val userMap: LiveData<Map<String, String>> = _userMap

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _isApprovalDataReady = MediatorLiveData<Boolean>().apply {
        value = false
        var mapReady = false
        var approvalsReady = false

        fun update() {
            if (mapReady && approvalsReady) {
                value = true
            }
        }

        addSource(_userMap) {
            mapReady = true
            update()
        }
        addSource(_pendingApprovals) {
            approvalsReady = true
            update()
        }
    }
    val isApprovalDataReady: LiveData<Boolean> = _isApprovalDataReady


    init {
        val dao = AppDatabase.getDatabase(application).notificationDAO()
        repository = NotificationRepository(dao)
        notifications = repository.allNotifications
        abacusPhotoRepository = AbacusPhotoRepository()
        userRepository = UserRepository()
    }

    fun fetchUserMap(factoryId: Int) {
        viewModelScope.launch {
            val result = userRepository.getUsersByFactory(factoryId)

            result.onSuccess { userList ->
                val map = userList.associateBy({ it.id }, { it.name })
                _userMap.postValue(map)

            }.onFailure {
                _toastEvent.postValue(Event("Erro ao buscar usuários: ${it.message}"))
                _userMap.postValue(emptyMap())
            }
        }
    }

    fun fetchPendingApprovals(factoryId: Int) {
        viewModelScope.launch {
            val result = abacusPhotoRepository.getPendingPhotosByFactory(factoryId)

            result.onSuccess { photoList ->
                _pendingApprovals.postValue(photoList)
            }.onFailure { exception ->
                _toastEvent.postValue(Event(exception.message ?: "Erro ao buscar aprovações"))
                _pendingApprovals.postValue(emptyList())
            }
        }
    }

    fun approvePhoto(photoId: String, validatorName: String, factoryId: Int) {
        viewModelScope.launch {
            val result = abacusPhotoRepository.approvePhoto(photoId, validatorName)
            result.onSuccess {
                _toastEvent.postValue(Event("Foto aprovada com sucesso!"))
                fetchPendingApprovals(factoryId)
            }.onFailure {
                _toastEvent.postValue(Event("Erro ao aprovar: ${it.message}"))
            }
        }
    }

    fun denyPhoto(photoId: String, factoryId: Int) {
        viewModelScope.launch {
            val result = abacusPhotoRepository.denyPhoto(photoId)
            result.onSuccess {
                _toastEvent.postValue(Event("Foto negada com sucesso."))
                fetchPendingApprovals(factoryId)
            }.onFailure {
                _toastEvent.postValue(Event("Erro ao negar: ${it.message}"))
            }
        }
    }

    fun addNotification(title: String, description: String) {
        viewModelScope.launch {
            val now = Date()
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            val notification = NotificationEntity(
                title = title,
                description = description,
                time = timeFormatter.format(now),
                timestamp = now.time
            )
            repository.saveNotification(notification)
        }
    }
}
