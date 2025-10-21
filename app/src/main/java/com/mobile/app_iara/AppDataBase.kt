package com.mobile.app_iara
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobile.app_iara.ui.notifications.NotificationDAO
import com.mobile.app_iara.ui.notifications.NotificationEntity

@Database(entities = [NotificationEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDAO(): NotificationDAO

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}