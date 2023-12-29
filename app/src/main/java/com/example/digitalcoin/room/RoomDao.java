package com.example.digitalcoin.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.digitalcoin.room.entities.MarketDataEntity;
import com.example.digitalcoin.room.entities.MarketListEntity;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MarketListEntity marketListEntity);

    @Query("SELECT * FROM AllMarket")
    Flowable<MarketListEntity> getAllMarketData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MarketDataEntity marketDataEntity);

    @Query("SELECT * FROM CryptoData")
    Flowable<MarketDataEntity> getCryptoMarketData();

}
