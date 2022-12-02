package com.example.igor.restaurantmobile.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.igor.restaurantmobile.data.database.models.NotificationModel

@Dao
interface NotificationDao {
    @Query("SELECT * FROM local_notification ORDER BY tag DESC")
    suspend fun getNotification(): List<NotificationModel>

    @Insert
    suspend fun insertNotification(user: NotificationModel)

    @Query("UPDATE local_notification SET viewed = 0 , viewedTime = :timeViewed WHERE tag = :tag ")
    suspend fun updateNotification(tag: String, timeViewed: String)
}