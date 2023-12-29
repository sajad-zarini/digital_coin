package com.example.digitalcoin.models.cryptoMarketDataModel;

public class CryptoMarketDataModel {

    private final String cryptos;
    private final String exchanges;
    private final String marketCap;
    private final String vol_24h;
    private final String btc_dominance;
    private final String eth_dominance;
    private final String marketCap_change;
    private final String vol_change;
    private final String bcd_change;

    public CryptoMarketDataModel(String cryptos, String exchanges, String marketCap, String vol_24h, String btc_dominance, String eth_dominance, String marketCap_change, String vol_change, String btcd_change) {
        this.cryptos = cryptos;
        this.exchanges = exchanges;
        this.marketCap = marketCap;
        this.vol_24h = vol_24h;
        this.btc_dominance = btc_dominance;
        this.eth_dominance = eth_dominance;
        this.marketCap_change = marketCap_change;
        this.vol_change = vol_change;
        this.bcd_change = btcd_change;
    }

    public String getCryptos() {
        return cryptos;
    }

    public String getExchanges() {
        return exchanges;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public String getVol_24h() {
        return vol_24h;
    }

    public String getBtc_dominance() {
        return btc_dominance;
    }

    public String getEth_dominance() {
        return eth_dominance;
    }

    public String getMarketCap_change() {
        return marketCap_change;
    }

    public String getVol_change() {
        return vol_change;
    }

    public String getBcd_change() {
        return bcd_change;
    }
}
