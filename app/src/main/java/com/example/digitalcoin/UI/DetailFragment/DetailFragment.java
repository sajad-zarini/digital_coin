package com.example.digitalcoin.UI.DetailFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.Models.cryptoListModel.DataItem;
import com.example.digitalcoin.R;
import com.example.digitalcoin.ViewModels.CryptoDetailViewModel;
import com.example.digitalcoin.databinding.FragmentDetailBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.rxjava3.annotations.NonNull;
import me.ibrahimsn.lib.SmoothBottomBar;

public class DetailFragment extends Fragment {

    boolean watchlistIsChecked = false;

    ArrayList<String> bookmarksArray;

    SmoothBottomBar bottomNavigationBar;


    FragmentDetailBinding fragmentDetailBinding;
    CryptoDetailViewModel viewModel;

    ArrayList<String> detailKeysArray;
    ArrayList<String> detailValuesArray;
    DetailRvAdapter detailRvAdapter;

    MainActivity mainActivity;

    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setLocale(mainActivity, "en");

        fragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        // setup FireBase Analytics
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity().getApplicationContext());

        HideBottomNavigationBar();

        // ViewModel Setup
        viewModel = new ViewModelProvider(requireActivity()).get(CryptoDetailViewModel.class);
//        fragmentDetailBinding.setViewmodel(viewModel);

        // get Data from Bundle and set to data binding
        DataItem dataItem = getArguments().getParcelable("model");
//        fragmentDetailBinding.setModel(dataItem);


//        seeCoin_logEventAnalytics(dataItem);
        setupCoinLogo(dataItem);
        setupBackButtonOnClick();
        onWatchListClick(dataItem);
        setupAllText(dataItem);
        setupchart(dataItem);
        setupRecyclerView(dataItem);
        setUpFullScreenTxt(dataItem);

        // Inflate the layout for this fragment
        return fragmentDetailBinding.getRoot();
    }

    public static void setLocale(MainActivity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void HideBottomNavigationBar() {
        bottomNavigationBar = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull @NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

//    private void seeCoin_logEventAnalytics(DataItem dataItem) {
//        Bundle params = new Bundle();
//        params.putString("Coin_name", dataItem.getName());
//        params.putString("Coin_symbol", dataItem.getSymbol());
//        mFirebaseAnalytics.logEvent("Watched_Coins", params);
//    }

//    private void addCoin_logEventAnalytics(DataItem dataItem) {
//        Bundle params = new Bundle();
//        params.putString("Coin_name", dataItem.getName());
//        params.putString("Coin_symbol", dataItem.getSymbol());
//        mFirebaseAnalytics.logEvent("add_to_watchlist", params);
//    }

    private void setupCoinLogo(DataItem dataItem) {
        Glide.with(fragmentDetailBinding.getRoot().getContext())
                .load("https://s2.coinmarketcap.com/static/img/coins/64x64/" + dataItem.getId() + ".png")
                .thumbnail(Glide.with(fragmentDetailBinding.getRoot().getContext()).load(R.drawable.loading))
                .into(fragmentDetailBinding.detailCoinLogo);
    }

    private void setupRecyclerView(DataItem dataItem) {
        fillKeysArray();
        fillValuesArray(dataItem);
        detailRvAdapter = new DetailRvAdapter(detailKeysArray, detailValuesArray);
        fragmentDetailBinding.derailRV.setAdapter(detailRvAdapter);
    }

    //set different decimals for different price
    @SuppressLint("DefaultLocale")
    private String SetDecimals(double price) {
        if (price < 1) {
            return "$" + String.format("%.6f", price);
        } else if (price < 10) {
            return "$" + String.format("%.4f", price);
        } else {
            return "$" + String.format("%.2f", price);
        }
    }

    @SuppressLint("DefaultLocale")
    private void fillValuesArray(DataItem dataItem) {
        detailValuesArray = new ArrayList<>();

        // fix numbers
        String high24 = SetDecimals(dataItem.getHigh24h());
        String low24 = SetDecimals(dataItem.getLow24h());
        String ath = SetDecimals(dataItem.getAth());
        String atl = SetDecimals(dataItem.getAtl());
        // remove float section and get a int
        String marketCap = dataItem.getQuotes().get(0).getMarketCap().toString().split("\\.")[0];
        String volume24 = dataItem.getQuotes().get(0).getVolume24h().toString().split("\\.")[0];
        String totalsupply = dataItem.getTotalSupply().toString().split("\\.")[0];


        Log.e("TAG", "MarketCap detail: " + dataItem.getQuotes().get(0).getMarketCap());
        detailValuesArray.add(dataItem.getName());
        detailValuesArray.add("$" + marketCap);
        detailValuesArray.add("$" + volume24);
        detailValuesArray.add(String.format("%.2f", dataItem.getQuotes().get(0).getDominance()) + "%");
        detailValuesArray.add(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange7d()));
        detailValuesArray.add(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange30d()));
        detailValuesArray.add(high24);
        detailValuesArray.add(low24);
        detailValuesArray.add(ath);
        detailValuesArray.add(atl);
        detailValuesArray.add(totalsupply);
    }

    private void fillKeysArray() {
        detailKeysArray = new ArrayList<>();
        detailKeysArray.add("Name");
        detailKeysArray.add("Market Cap");
        detailKeysArray.add("Volume 24h");
        detailKeysArray.add("Dominance");
        detailKeysArray.add("PercentChange 7d");
        detailKeysArray.add("PercentChange 30d");
        detailKeysArray.add("High 24h");
        detailKeysArray.add("Low 24h");
        detailKeysArray.add("All Time High");
        detailKeysArray.add("All Time Low");
        detailKeysArray.add("Total Supply");
    }

    private void setupBackButtonOnClick() {
        fragmentDetailBinding.backBtn.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    // set Price and PriceChange
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setupAllText(DataItem dataItem) {
        fragmentDetailBinding.detailSymbol.setText(dataItem.getSymbol() + "/USD");
        SetDecimalsForPrice(dataItem);
        SetColorText(dataItem);

        /// Check price for set price change and price change Icon (red or green)
        if (dataItem.getQuotes().get(0).getPercentChange24h() > 0) {
            fragmentDetailBinding.detailPriceChangeIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
            fragmentDetailBinding.detailCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
        } else if (dataItem.getQuotes().get(0).getPercentChange24h() < 0) {
            fragmentDetailBinding.detailPriceChangeIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
            fragmentDetailBinding.detailCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
        } else {
            fragmentDetailBinding.detailCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
        }

    }

    // setup Chart (WebView) for first Time
    @SuppressLint("SetJavaScriptEnabled")
    private void setupchart(DataItem dataItem) {
        fragmentDetailBinding.detailChart.getSettings().setJavaScriptEnabled(true);
        fragmentDetailBinding.detailChart.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        fragmentDetailBinding.detailChart.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + dataItem.getSymbol() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT");

    }

    /// set different decimals for different price
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void SetDecimalsForPrice(DataItem dataItem) {
        if (dataItem.getQuotes().get(0).getPrice() < 1) {
            fragmentDetailBinding.detailCoinPrice.setText("$" + String.format("%.6f", dataItem.getQuotes().get(0).getPrice()));
        } else if (dataItem.getQuotes().get(0).getPrice() < 10) {
            fragmentDetailBinding.detailCoinPrice.setText("$" + String.format("%.4f", dataItem.getQuotes().get(0).getPrice()));
        } else {
            fragmentDetailBinding.detailCoinPrice.setText("$" + String.format("%.2f", dataItem.getQuotes().get(0).getPrice()));
        }
    }

    /// set Color Green and Red for price
    private void SetColorText(DataItem dataItem) {
        if (dataItem.getQuotes().get(0).getPercentChange24h() < 0) {
            fragmentDetailBinding.detailCoinChange.setTextColor(Color.RED);
        } else if (dataItem.getQuotes().get(0).getPercentChange24h() > 0) {
            fragmentDetailBinding.detailCoinChange.setTextColor(Color.GREEN);
        } else {
            fragmentDetailBinding.detailCoinChange.setTextColor(Color.WHITE);
        }
    }

    // setup Click for bookmark Btn
    public void onWatchListClick(DataItem dataItem) {

        ReadDataStore();

        /// show different Icons when get data from shared Preference
        if (bookmarksArray.contains(dataItem.getSymbol())) {
            watchlistIsChecked = true;
            fragmentDetailBinding.bookmarkBtn.setImageResource(R.drawable.baseline_star_rate_24);
        } else {
            watchlistIsChecked = false;
            fragmentDetailBinding.bookmarkBtn.setImageResource(R.drawable.baseline_star_outline_24);
        }


        /// write or delete data from Shared with click on bookmark Btn
        fragmentDetailBinding.bookmarkBtn.setOnClickListener(v -> {

            if (!watchlistIsChecked) {
                if (!bookmarksArray.contains(dataItem.getSymbol())) {
                    bookmarksArray.add(dataItem.getSymbol());
                }
                writeToDataStore();
                fragmentDetailBinding.bookmarkBtn.setImageResource(R.drawable.baseline_star_rate_24);
                watchlistIsChecked = true;

                //send event to firebase analytics
//                addCoin_logEventAnalytics(dataItem);
            } else {
                fragmentDetailBinding.bookmarkBtn.setImageResource(R.drawable.baseline_star_outline_24);
                //clear bookmark
                bookmarksArray.remove(dataItem.getSymbol());
                writeToDataStore();
                watchlistIsChecked = false;
            }
        });
    }

    // read new BookMark on Shared Preference
    private void ReadDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("bookmarks", String.valueOf(new ArrayList<String>()));
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        bookmarksArray = gson.fromJson(json, type);
        Log.e("TAG", "ReadDataStore: " + bookmarksArray);
    }

    /// Write BookMarks ArrayList From Shared Preference
    private void writeToDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(bookmarksArray);

        editor.putString("bookmarks", json);
        editor.apply();
    }

    private void setUpFullScreenTxt(DataItem dataItem) {
        String uData = "fullscreen";
        SpannableString content = new SpannableString(uData);
        content.setSpan(new UnderlineSpan(), 0, uData.length(), 0);
        fragmentDetailBinding.fullscreenTxt.setText(content);

        fragmentDetailBinding.fullscreenTxt.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("model", dataItem);

            Navigation.findNavController(v).navigate(R.id.action_detailFragment_to_landScapeChartFragment, bundle);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bottomNavigationBar.setVisibility(View.VISIBLE);
    }
}