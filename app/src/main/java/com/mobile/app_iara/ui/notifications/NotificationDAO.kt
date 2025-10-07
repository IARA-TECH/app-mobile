package com.mobile.app_iara.ui.notifications

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notification_history ORDER BY id DESC")
    fun getAllNotifications(): LiveData<List<NotificationEntity>>

    @Query("DELETE FROM notification_history")
    suspend fun clearAll()
}