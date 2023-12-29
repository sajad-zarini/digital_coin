package com.example.digitalcoin.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.digitalcoin.room.converters.AllMarketModelConverters;
import com.example.digitalcoin.room.converters.CryptoDataModelConverter;
import com.example.digitalcoin.room.entities.MarketDataEntity;
import com.example.digitalcoin.room.entities.MarketListEntity;

@TypeConverters({AllMarketModelConverters.class, CryptoDataModelConverter.class})
@Database(entities = {MarketListEntity.class, MarketDataEntity.class},version = 2)
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
