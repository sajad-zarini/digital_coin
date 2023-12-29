package com.example.digitalcoin.hilt.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;

import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.ActivityMainBinding;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@Module
@InstallIn(ActivityComponent.class)
public class HiltAppModules {

    @Provides
    ActivityMainBinding ProvideActivityMainBinding(Activity activity){
        return DataBindingUtil.setContentView(activity, R.layout.activity_main);
    }

    @Provides
    @Named("MainActivityCompositeDisposable")
    CompositeDisposable ProvideCompositeDisposable(){
        return new CompositeDisposable();
    }

    @Provides
    SharedPreferences ProvideSharedPreferences(Activity activity){
        return PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Provides
    ConnectivityManager provideConnectivityManager(@ActivityContext Context context) {
        return (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkRequest provideNetworkRequest() {
        return new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
    }
}
