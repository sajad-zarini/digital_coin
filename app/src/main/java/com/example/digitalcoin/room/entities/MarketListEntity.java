package com.example.digitalcoin.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;

@Entity(tableName = "AllMarket")
public class MarketListEntity {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "AllMarket")
    public AllMarketModel allMarketModel;

    public MarketListEntity(AllMarketModel allMarketModel) {
        this.allMarketModel = allMarketModel;
    }

    public int getUid() {
        return uid;
    }

    public AllMarketModel getAllMarketModel() {
        return allMarketModel;
    }
}
