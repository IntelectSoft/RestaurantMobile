package md.edi.mobilewaiter.data.database.repository

import android.content.Context
import android.util.Log
import md.edi.mobilewaiter.data.database.ApplicationDb
import md.edi.mobilewaiter.data.database.models.NotificationModel

class RepositoryNotification(context: Context) {
    private val appDb = ApplicationDb.getInstance(context)

    suspend fun insertNotification(notificationModel: NotificationModel) {
        Log.e("TAG", "insertNotification: $notificationModel")
        appDb.notificationDao().insertNotification(notificationModel)
    }

    suspend fun getAllNotification(): List<NotificationModel> {
        return appDb.notificationDao().getNotification()
    }

    suspend fun updateNotification(tag: String, timeViewed: String){
        appDb.notificationDao().updateNotification(tag, timeViewed)
    }
}