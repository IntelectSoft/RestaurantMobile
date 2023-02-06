package md.edi.mobilewaiter.data.database.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_notification")
data class NotificationModel (
    @NonNull
    @PrimaryKey
    val tag: String,
    var body: String,
    var title: String,
    var billId: String,
    var viewed: Int,
    var viewedTime: String
)
