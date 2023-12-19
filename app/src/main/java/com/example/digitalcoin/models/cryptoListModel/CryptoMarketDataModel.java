package com.example.digitalcoin.models.cryptoListModel;

public class CryptoMarketDataModel {

    private String cryptos;
    private String exchanges;
    private String marketCap;
    private String vol_24h;
    private String btc_dominance;
    private String eth_dominance;

    public CryptoMarketDataModel(String cryptos, String exchanges, String marketCap, String vol_24h, String btc_dominance, String eth_dominance) {
        this.cryptos = cryptos;
        this.exchanges = exchanges;
        this.marketCap = marketCap;
        this.vol_24h = vol_24h;
        this.btc_dominance = btc_dominance;
        this.eth_dominance = eth_dominance;
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
}
