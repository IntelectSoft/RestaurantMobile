package com.example.igor.restaurantmobile.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.igor.restaurantmobile.data.database.dao.NotificationDao;
import com.example.igor.restaurantmobile.data.database.models.NotificationModel;

@Database(entities = {NotificationModel.class}, version = 1)
public abstract class ApplicationDb extends RoomDatabase {

    private static volatile ApplicationDb INSTANCE;

    public abstract NotificationDao notificationDao();

    public static ApplicationDb getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ApplicationDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ApplicationDb.class, "rest_mobile.db")
//                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE message ADD COLUMN convId TEXT NOT NULL DEFAULT ''");
        }
    };
}