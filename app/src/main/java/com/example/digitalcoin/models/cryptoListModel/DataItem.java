package com.example.digitalcoin.models.cryptoListModel;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataItem implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("slug")
    private String slug;

    @SerializedName("cmcRank")
    private int cmcRank;

    @SerializedName("marketPairCount")
    private int marketPairCount;

    @SerializedName("circulatingSupply")
    private double circulatingSupply;

    @SerializedName("selfReportedCirculatingSupply")
    private double selfReportedCirculatingSupply;

    @SerializedName("totalSupply")
    private Number totalSupply;

    @SerializedName("maxSupply")
    private double maxSupply;

    @SerializedName("ath")
    private double ath;

    @SerializedName("atl")
    private double atl;

    @SerializedName("high24h")
    private double high24h;

    @SerializedName("low24h")
    private double low24h;

    @SerializedName("isActive")
    private int isActive;

    @SerializedName("lastUpdated")
    private String lastUpdated;

    @SerializedName("dateAdded")
    private String dateAdded;

    @SerializedName("quotes")
    private List<ListUSD> listQuotes;

    @SerializedName("isAudited")
    private boolean isAudited;

    protected DataItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        symbol = in.readString();
        slug = in.readString();
        cmcRank = in.readInt();
        marketPairCount = in.readInt();
        circulatingSupply = in.readDouble();
        selfReportedCirculatingSupply = in.readInt();
        totalSupply = in.readDouble();
        maxSupply = in.readDouble();
        ath = in.readDouble();
        atl = in.readDouble();
        high24h = in.readDouble();
        low24h = in.readDouble();
        isActive = in.readInt();
        lastUpdated = in.readString();
        dateAdded = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isAudited = in.readBoolean();
        }
    }

    public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel in) {
            return new DataItem(in);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(symbol);
        dest.writeString(slug);
        dest.writeInt(cmcRank);
        dest.writeInt(marketPairCount);
        dest.writeDouble(circulatingSupply);
        dest.writeDouble(selfReportedCirculatingSupply);
        dest.writeDouble(maxSupply);
        dest.writeDouble(ath);
        dest.writeDouble(atl);
        dest.writeDouble(high24h);
        dest.writeDouble(low24h);
        dest.writeInt(isActive);
        dest.writeString(lastUpdated);
        dest.writeString(dateAdded);
        dest.writeByte((byte) (isAudited ? 1 : 0));
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSlug() {
        return slug;
    }

    public int getCmcRank() {
        return cmcRank;
    }

    public int getMarketPairCount() {
        return marketPairCount;
    }

    public double getCirculatingSupply() {
        return circulatingSupply;
    }

    public double getSelfReportedCirculatingSupply() {
        return selfReportedCirculatingSupply;
    }

    public Number getTotalSupply() {
        return totalSupply;
    }

    public double getMaxSupply() {
        return maxSupply;
    }

    public double getAth() {
        return ath;
    }

    public double getAtl() {
        return atl;
    }

    public double getHigh24h() {
        return high24h;
    }

    public double getLow24h() {
        return low24h;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public List<ListUSD> getQuotes() {
        return listQuotes;
    }

    public boolean isAudited() {
        return isAudited;
    }
}
