package com.example.digitalcoin.room.converters;

import androidx.room.TypeConverter;

import com.example.digitalcoin.models.cryptoMarketDataModel.CryptoMarketDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class CryptoDataModelConverter {

    @TypeConverter
    public String toJson(CryptoMarketDataModel cryptoMarketDataModel) {
        if (cryptoMarketDataModel == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<CryptoMarketDataModel>() {}.getType();
        return gson.toJson(cryptoMarketDataModel, type);
    }

    @TypeConverter
    public CryptoMarketDataModel toDataClass(String data) {
        if (data == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<CryptoMarketDataModel>() {}.getType();
        return gson.fromJson(data, type);
    }

}
