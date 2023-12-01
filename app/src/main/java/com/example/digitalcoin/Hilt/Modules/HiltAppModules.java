package com.example.digitalcoin.Hilt.Modules;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

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
    String ProvieFullName() {
        return "Sajjad Zarrini";
    }
}
