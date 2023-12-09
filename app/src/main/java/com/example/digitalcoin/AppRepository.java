package com.example.digitalcoin;

import com.example.digitalcoin.Retrofit.RequestAPI;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;

public class AppRepository {

    RequestAPI requestAPI;

    public AppRepository(RequestAPI requestAPI) {
        this.requestAPI = requestAPI;
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
}
