package com.example.digitalcoin.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.digitalcoin.Repository.AppRepository;
import com.example.digitalcoin.R;
import com.example.digitalcoin.Room.Entities.MarketDataEntity;
import com.example.digitalcoin.Room.Entities.MarketListEntity;
import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.Models.cryptoMarketDataModel.CryptoMarketDataModel;

import java.util.ArrayList;
import java.util.concurrent.Future;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;

@HiltViewModel
public class AppViewModels extends AndroidViewModel {

    MutableLiveData<ArrayList<Integer>> mutableLiveData = new MutableLiveData<>();

    @Inject
    AppRepository appRepository;

    @Inject
    public AppViewModels(@NonNull Application application) {
        super(application);
        getViewPagerData();
    }

    public Future<Observable<AllMarketModel>> marketFutureCall() {
        return appRepository.marketListFutureCall();
    }

    MutableLiveData<ArrayList<Integer>> getViewPagerData() {
        ArrayList<Integer> pics = new ArrayList<>();
        pics.add(R.drawable.p1);
        pics.add(R.drawable.p2);
        pics.add(R.drawable.p3);
        pics.add(R.drawable.p4);
        pics.add(R.drawable.p5);

        mutableLiveData.postValue(pics);

        return mutableLiveData;
    }

    public MutableLiveData<ArrayList<Integer>> getMutableLiveData() {
        return mutableLiveData;
    }

    public void insertAllMarket(AllMarketModel allMarketModel) {
        appRepository.InsertAllMarket(allMarketModel);
    }

    public Flowable<MarketListEntity> getAllMarketData() {
        return appRepository.getAllMarketData();
    }

    public void insertCryptoDataMarket(CryptoMarketDataModel cryptoMarketDataModel) {
        appRepository.InsertCryptoDataMarket(cryptoMarketDataModel);
    }

    public Flowable<MarketDataEntity> getCryptoMarketData(){
        return appRepository.getCryptoMarketData();
    }

}
