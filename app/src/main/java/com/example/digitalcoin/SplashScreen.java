package com.example.digitalcoin;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.ViewModels.AppViewModels;
import com.example.digitalcoin.databinding.ActivitySplashScreenBinding;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class SplashScreen extends AppCompatActivity {

    AppViewModels appViewModels;
    ActivitySplashScreenBinding activitySplashScreenBinding;
    CompositeDisposable compositeDisposable;

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    NetworkRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);

        activitySplashScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        compositeDisposable = new CompositeDisposable();

        setupViewModel();
        checkConnection();
        boolean isCon = isNetworkConnected();

        if (!isCon) {
            activitySplashScreenBinding.loadingTxt.setText(R.string.connection_lost);
        }

    }

    //setup ViewModels
    private void setupViewModel() {
        appViewModels = new ViewModelProvider(this).get(AppViewModels.class);
    }

    private boolean isNetworkConnected() {
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    //check phone is connecting to the internet or not?
    private void checkConnection() {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {

                runOnUiThread(() -> activitySplashScreenBinding.loadingTxt.setText(R.string.getting_data));

                callAllApiRequest();
            }

            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> activitySplashScreenBinding.loadingTxt.setText(R.string.connection_lost));
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }


    //method that include all api requests
    public void callAllApiRequest() {
        CallListApiRequest();
    }

    //api Call with RxJava (list of Coins api)
    private void CallListApiRequest() {
        try {
            appViewModels.marketFutureCall().get().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AllMarketModel>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                            Log.e("TAG", "onSubscribe");
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull AllMarketModel allMarketModel) {
                            Log.e("TAG", "onNext: " + allMarketModel.getData().getCryptoCurrencyList().size());
                            appViewModels.insertAllMarket(allMarketModel);
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            Log.e("TAG", "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}