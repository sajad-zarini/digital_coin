package com.example.digitalcoin;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
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
import com.example.digitalcoin.models.cryptoListModel.CryptoMarketDataModel;
import com.example.digitalcoin.viewModels.AppViewModels;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
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

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    NetworkRequest networkRequest;

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

        checkConnection();

        setUpViewModel();
    }

    private void checkConnection() {
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@androidx.annotation.NonNull Network network) {
                Log.e("TAG", "onAvailable: ");
                CallListApiRequest();
                callCryptoMarketApiRequest();
            }

            @Override
            public void onLost(@androidx.annotation.NonNull Network network) {
                Log.e("TAG", "onLost: ");
                Snackbar.make(activityMainBinding.mainCon, "Internet Connection Lost.", 2000).show();
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }
    }

    private void callCryptoMarketApiRequest() {
        Completable.fromRunnable(() -> {
                    try {
                        Document pageSrc = Jsoup.connect("https://coinmarketcap.com/").get();

                        // Scraping Market Data like (marketCap,Dominance,...)
                        Elements scrapeMarketData = pageSrc.getElementsByClass("cmc-link");

                        //for splitting BTC and ETH dominance in txt
                        String[] dominance_txt = scrapeMarketData.get(4).text().split(" ");

                        // Scraping All span Tag
                        Elements ScrapeChangeIcon = pageSrc.getElementsByTag("span");

                        // get all span Tag wth Icon (class= caretUp and caretDown)
                        ArrayList<String> iconList = new ArrayList<>();
                        for (Element i : ScrapeChangeIcon) {
                            if (i.hasClass("icon-Caret-down") || i.hasClass("icon-Caret-up")) {
                                iconList.add(i.attr("class"));
                            }
                        }

                        // initialize all data
                        String Cryptos = scrapeMarketData.get(0).text();
                        String Exchanges = scrapeMarketData.get(1).text();
                        String MarketCap = scrapeMarketData.get(2).text();
                        String Vol_24h = scrapeMarketData.get(3).text();

                        String BTC_Dominance = dominance_txt[1];
                        String ETH_Dominance = dominance_txt[3];

                        CryptoMarketDataModel cryptoMarketDataModel = new CryptoMarketDataModel(Cryptos, Exchanges, MarketCap, Vol_24h, BTC_Dominance, ETH_Dominance);
//                         insert model class to RoomDatabase
                        appViewModels.insertCryptoDataMarket(cryptoMarketDataModel);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: jsoup done");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError11:" + e.toString());
                    }
                });
    }

    private void setUpViewModel() {
        appViewModels = new ViewModelProvider(this).get(AppViewModels.class);
    }

    private void CallListApiRequest() {
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

                        appViewModels.insertAllMarket(allMarketModel);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("TAG", "onComplete: ");
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

        activityMainBinding.bottomNavigation.setupWithNavController(menu, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}