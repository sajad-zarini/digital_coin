package com.example.digitalcoin.Room.Converters;

import androidx.room.TypeConverter;

import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AllMarketModelConverters {

    @TypeConverter
    public String toJson(AllMarketModel allMarketModel) {
        if (allMarketModel == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<AllMarketModel>() {}.getType();
        String json = gson.toJson(allMarketModel, type);
        return json;
    }

    @TypeConverter
    public AllMarketModel toDataClass(String allMarket) {
        if (allMarket == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<AllMarketModel>() {}.getType();
        AllMarketModel allMarketModel = gson.fromJson(allMarket, type);
        return allMarketModel;
    }
}
