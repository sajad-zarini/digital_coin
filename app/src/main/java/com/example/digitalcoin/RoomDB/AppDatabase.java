package com.example.digitalcoin.RoomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.digitalcoin.RoomDB.Converters.AllMarketModelConverters;
import com.example.digitalcoin.RoomDB.Entities.MarketListEntity;

@TypeConverters({AllMarketModelConverters.class})
@Database(entities = {MarketListEntity.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String Db_name = "appDb";
    public static AppDatabase instance;
    public abstract RoomDao roomDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, Db_name)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
