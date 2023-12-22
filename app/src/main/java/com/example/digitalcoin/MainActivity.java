package com.example.digitalcoin;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.Models.cryptoMarketDataModel.CryptoMarketDataModel;
import com.example.digitalcoin.ViewModels.AppViewModels;
import com.example.digitalcoin.databinding.ActivityMainBinding;
import com.example.digitalcoin.databinding.DrawerHeaderLayoutBinding;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

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

    @Inject
    ActivityMainBinding activityMainBinding;

    DrawerHeaderLayoutBinding drawerHeaderLayoutBinding;

    NavController navController;
    NavHostFragment navHostFragment;

    AppBarConfiguration appBarConfiguration;

    public DrawerLayout drawerLayout;
    AppViewModels appViewModels;

    @Inject
    @Named("MainActivityCompositeDisposable")
    CompositeDisposable compositeDisposable;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    NetworkRequest networkRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawerHeaderLayoutBinding = DrawerHeaderLayoutBinding.bind(activityMainBinding.navigationView.getHeaderView(0));

        drawerLayout = activityMainBinding.drawerLayout;

        setupSmoothBottomComponent();
        checkConnection();
        setUpViewModel();
        initDrawerHeader();
    }

    @SuppressLint("SetTextI18n")
    private void initDrawerHeader() {
        /// get Data SharedPreference
        String imgFromStore = sharedPrefs.getString("profileImg", null);
        String firstname = sharedPrefs.getString("firstname", "");
        String lastname = sharedPrefs.getString("lastname", "");
        String mail = sharedPrefs.getString("male", "");


        /// set profile image
        if (imgFromStore == null) {
            drawerHeaderLayoutBinding.drawerHeaderImage.setImageResource(R.drawable.profile_placeholder);
        } else {
            drawerHeaderLayoutBinding.drawerHeaderImage.setImageBitmap(decodeBase64(imgFromStore));
        }

        /// set firstname and lastname
        if (firstname.equals("") || lastname.equals("")) {
            drawerHeaderLayoutBinding.drawerHeaderName.setText(R.string.set_your_profile_name);
        } else {
            drawerHeaderLayoutBinding.drawerHeaderName.setText(firstname + lastname);
        }

        /// set mail
        if (mail.equals("")) {
            drawerHeaderLayoutBinding.drawerHeaderMail.setText(R.string.example_mail_com);
        } else {
            drawerHeaderLayoutBinding.drawerHeaderMail.setText(mail);
        }
    }

    // decode string to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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


        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private void callCryptoMarketApiRequest() {
        Completable.fromRunnable(() -> {
                    try {
                        Document pageSrc = Jsoup.connect("https://coinmarketcap.com/").get();

                        // Scraping Market Data like (marketCap,Dominance,...)
                        Elements scrapeMarketData = pageSrc.getElementsByClass("cmc-link");

                        //for splitting BTC and ETH dominance in txt
                        String[] dominance_txt = scrapeMarketData.get(4).text().split(" ");

                        // Scraping Market number of changes like (Market cap Change,volumeChange,...)
                        Elements ScrapeMarketChange = pageSrc.getElementsByClass("sc-8a0bb4db-2 dojvi");
                        String[] changePercent = ScrapeMarketChange.text().split(" ");
                        String[] changePercentModified = new String[changePercent.length - 3];
                        int currentIndex = 0;

                        for (int i = 0; i < changePercent.length; i++) {
                            if (i != 0 && i != 1 && i != 3) {
                                changePercentModified[currentIndex] = changePercent[i];
                                currentIndex++;
                            }
                        }

                        // Scraping All span Tag
                        Elements ScrapeChangeIcon = pageSrc.getElementsByTag("span");

                        // get all span Tag wth Icon (class= caretUp and caretDown)
                        ArrayList<String> iconList = new ArrayList<>();
                        for (Element i : ScrapeChangeIcon) {
                            if (i.hasClass("icon-Caret-down") || i.hasClass("icon-Caret-up")) {
                                iconList.add(i.attr("class"));
                            }
                        }

                        // matching - or + element of PercentChanges
                        ArrayList<String> finalChangePercent = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            if (iconList.get(i).equals("icon-Caret-up")) {
                                finalChangePercent.add(changePercent[i]);
                            } else {
                                finalChangePercent.add("-" + changePercent[i]);
                            }
                        }

                        // initialize all data
                        String Cryptos = scrapeMarketData.get(0).text();
                        String Exchanges = scrapeMarketData.get(1).text();
                        String MarketCap = scrapeMarketData.get(2).text();
                        String Vol_24h = scrapeMarketData.get(3).text();

                        String BTC_Dominance = dominance_txt[1];
                        String ETH_Dominance = dominance_txt[3];

                        String MarketCap_change = changePercentModified[0];
                        String vol_change = changePercentModified[1];
                        String BTCD_change = changePercentModified[2];

                        CryptoMarketDataModel cryptoMarketDataModel = new CryptoMarketDataModel(Cryptos, Exchanges, MarketCap, Vol_24h, BTC_Dominance, ETH_Dominance, MarketCap_change, vol_change, BTCD_change);
                        /// insert model class to RoomDatabase
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
                        Log.e("TAG", "onError:" + e.toString());
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
        navController = navHostFragment != null ? navHostFragment.getNavController() : null;

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.marketFragment, R.id.watchListFragment)
                .setOpenableLayout(activityMainBinding.drawerLayout)
                .build();

        NavigationUI.setupWithNavController(activityMainBinding.navigationView, navController);

        activityMainBinding.navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.exit) {
                finish();
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
                activityMainBinding.drawerLayout.closeDrawers();
            }
            return false;
        });

        setupSmoothBottomMenu();
    }

    private void setupSmoothBottomMenu() {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.bottom_navigation_menu);
        Menu menu = popupMenu.getMenu();

        activityMainBinding.bottomNavigation.setupWithNavController(menu, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, activityMainBinding.drawerLayout) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}