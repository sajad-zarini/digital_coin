package com.example.digitalcoin;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.digitalcoin.databinding.ActivityMainBinding;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.viewModels.AppViewModels;

import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    NavController navController;
    NavHostFragment navHostFragment;

    AppBarConfiguration appBarConfiguration;

    public DrawerLayout drawerLayout;
    CompositeDisposable compositeDisposable;

    AppViewModels appViewModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.marketFragment, R.id.watchListFragment)
                .setOpenableLayout(activityMainBinding.drawerLayout)
                        .build();

        setupSmoothBottomComponent();

        compositeDisposable = new CompositeDisposable();

        NavigationUI.setupWithNavController(activityMainBinding.navigationView, navController);

        drawerLayout = activityMainBinding.drawerLayout;
        
        CallListApiRequest();
        setUpViewModel();
    }

    private void setUpViewModel() {
        appViewModels = new ViewModelProvider(this).get(AppViewModels.class);
    }

    private void CallListApiRequest() {
        Log.e("TAG", "CallListApiRequest: ");
        Observable.interval(20, TimeUnit.SECONDS)
                .flatMap(n -> appViewModels.marketFutureCall().get())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AllMarketModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AllMarketModel allMarketModel) {
                        Log.e("TAG", "onNext: " + allMarketModel.getData().getCryptoCurrencyList().get(0).getName());
                        Log.e("TAG", "onNext: " + allMarketModel.getData().getCryptoCurrencyList().get(1).getName());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: " + e );
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: " );
                    }
                });
    }

    private void setupSmoothBottomComponent() {
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        setupSmoothBottomMenu();
    }

    private void setupSmoothBottomMenu() {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.bottom_navigation_menu);
        Menu menu = popupMenu.getMenu();

        activityMainBinding.bottomNavigation.setupWithNavController(menu,navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}