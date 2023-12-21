package com.example.digitalcoin.Repository;

import android.util.Log;

import com.example.digitalcoin.Retrofit.RequestAPI;
import com.example.digitalcoin.Room.Entities.MarketDataEntity;
import com.example.digitalcoin.Room.Entities.MarketListEntity;
import com.example.digitalcoin.Room.RoomDao;
import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.Models.cryptoMarketDataModel.CryptoMarketDataModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppRepository {

    RequestAPI requestAPI;
    RoomDao roomDao;

    public AppRepository(RequestAPI requestAPI, RoomDao roomDao) {
        this.requestAPI = requestAPI;
        this.roomDao = roomDao;
    }

    public Future<Observable<AllMarketModel>> marketListFutureCall() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        final Callable<Observable<AllMarketModel>> myNetworkCallable = () -> requestAPI.makeMarketLatestListCall();

        final Future<Observable<AllMarketModel>> futureObservable = new Future<Observable<AllMarketModel>>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (myNetworkCallable.equals(true)) {
                    executorService.shutdown();
                }

                return false;
            }

            @Override
            public boolean isCancelled() {
                return executorService.isShutdown();
            }

            @Override
            public boolean isDone() {
                return executorService.isTerminated();
            }

            @Override
            public Observable<AllMarketModel> get() throws ExecutionException, InterruptedException {
                return executorService.submit(myNetworkCallable).get();
            }

            @Override
            public Observable<AllMarketModel> get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                return executorService.submit(myNetworkCallable).get(timeout, unit);
            }
        };

        return futureObservable;
    }

    public void InsertAllMarket(AllMarketModel allMarketModel) {
        Completable.fromAction(() -> roomDao.insert(new MarketListEntity(allMarketModel)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("InsertAllMarket", "onSubscribe: ok");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("InsertAllMarket", "onComplete: ok");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("InsertAllMarket", "onError: " + e.getMessage());
                    }
                });

    }

    public Flowable<MarketListEntity> getAllMarketData() {
        return roomDao.getAllMarketData();
    }

    public Flowable<MarketDataEntity> getCryptoMarketData(){
        return roomDao.getCryptoMarketData();
    }

    public void InsertCryptoDataMarket(CryptoMarketDataModel cryptoMarketDataModel) {
        Completable.fromAction(() -> roomDao.insert(new MarketDataEntity(cryptoMarketDataModel))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e("insertAllMarket", "onSubscribe: ok");
//                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("insertAllMarket", "onComplete: ok");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("insertAllMarket", "onError: " + e.getMessage());
                    }
                });
    }
}
