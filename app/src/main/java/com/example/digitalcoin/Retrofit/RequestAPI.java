package com.example.digitalcoin.Retrofit;

import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface RequestAPI {

    @GET("https://api.coinmarketcap.com/data-api/v3/cryptocurrency/listing?start=1&limit=300&sortBy=market_cap&sortType=desc&convert=USD,BTC,ETH&cryptoType=all&tagType=all&audited=false&aux=ath,atl,high24h,low24h,num_market_pairs,cmc_rank,date_added,max_supply,circulating_supply,total_supply,volume_7d,volume_30d,self_reported_circulating_supply,self_reported_market_cap")
    Observable<AllMarketModel> makeMarketLatestListCall();
}
