package com.example.digitalcoin.Models.cryptoListModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllMarketModel {
    @SerializedName("data")
    private RoomData data;

    @SerializedName("status")
    private ListStatus listStatus;

    public RoomData getData() {
        return data;
    }

    public ListStatus getListStatus() {
        return listStatus;
    }

    public class ListStatus {
        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("error_code")
        private String errorCode;

        @SerializedName("error_message")
        private String errorMessage;

        @SerializedName("elapsed")
        private String elapsed;

        @SerializedName("credit_count")
        private int creditCount;


        public String getTimestamp() {
            return timestamp;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getElapsed() {
            return elapsed;
        }

        public int getCreditCount() {
            return creditCount;
        }
    }

    public class RoomData {

        @SerializedName("cryptoCurrencyList")
        private List<DataItem> cryptoCurrencyList;

        @SerializedName("totalCount")
        private String totalCount;

        public List<DataItem> getCryptoCurrencyList() {
            return cryptoCurrencyList;
        }

        public String getTotalCount() {
            return totalCount;
        }
    }
}
