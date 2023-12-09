package com.example.digitalcoin.Hilt.Modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(ActivityComponent.class)
public class HiltAppModules {

    @Provides
    @Named("name")
    String ProvideName() {
        return "Hi Sajjad";
    }

    @Provides
    @Named("fullName")
    String ProvideFullName() {
        return "Sajjad Zarrini";
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
