package com.example.digitalcoin.Room.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.digitalcoin.Models.cryptoMarketDataModel.CryptoMarketDataModel;

@Entity(tableName = "CryptoData")
public class MarketDataEntity {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "cryptoMarketDataModel")
    public CryptoMarketDataModel cryptoMarketDataModel;

    public MarketDataEntity(CryptoMarketDataModel cryptoMarketDataModel) {
        this.cryptoMarketDataModel = cryptoMarketDataModel;
    }

    public int getUid() {
        return uid;
    }

    public CryptoMarketDataModel getCryptoMarketDataModel() {
        return cryptoMarketDataModel;
    }
}
