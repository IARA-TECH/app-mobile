package com.mobile.app_iara.ui.notifications

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE timestamp >= :todayStartTimestamp ORDER BY timestamp DESC")
    fun getTodaysNotifications(todayStartTimestamp: Long): LiveData<List<NotificationEntity>>

    @Query("DELETE FROM notifications WHERE timestamp < :todayStartTimestamp")
    suspend fun clearOldNotifications(todayStartTimestamp: Long)
}