package com.example.digitalcoin.UI.MarketFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentMarketBinding;
import com.example.digitalcoin.Models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.Models.cryptoMarketDataModel.CryptoMarketDataModel;
import com.example.digitalcoin.Models.cryptoListModel.DataItem;
import com.example.digitalcoin.ViewModels.AppViewModels;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MarketFragment extends Fragment {

    FragmentMarketBinding fragmentMarketBinding;

    MainActivity mainActivity;
    CollapsingToolbarLayout collapsingToolbarLayout;

    AppViewModels appViewModels;

    MarketRVAdapter marketRVAdapter;
    CompositeDisposable compositeDisposable;

    List<DataItem> dataItemList;
    ArrayList<DataItem> filteredList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpToolbar(view);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentMarketBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_market, container, false);
        compositeDisposable = new CompositeDisposable();

        filteredList = new ArrayList<>();

        setupSearchBox();
        setupViewModel();
        getMarketListDataFromDb();
        getCryptoMarketDataFromDb();

        return fragmentMarketBinding.getRoot();
    }

    private void setupSearchBox() {
        fragmentMarketBinding.searchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String name) {
        filteredList.clear();

        if (dataItemList != null) {

            for (DataItem item : dataItemList) {
                if (item.getSymbol().toLowerCase().contains(name.toLowerCase()) || item.getName().toLowerCase().contains(name.toLowerCase())) {
                    filteredList.add(item);
                }
            }

            marketRVAdapter.updateData(filteredList);
            marketRVAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkEmpty();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    checkEmpty();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    checkEmpty();
                }

                void checkEmpty() {
                    if (marketRVAdapter.getItemCount() == 0) {
                        fragmentMarketBinding.itemNotFoundTxt.setVisibility(View.VISIBLE);
                    } else {
                        fragmentMarketBinding.itemNotFoundTxt.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void setupViewModel() {
        appViewModels = new ViewModelProvider(requireActivity()).get(AppViewModels.class);
    }

    private void getMarketListDataFromDb() {
        Disposable disposable = appViewModels.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marketListEntity -> {
                    AllMarketModel allMarketModel = marketListEntity.getAllMarketModel();
                    dataItemList = allMarketModel.getData().getCryptoCurrencyList();

                    if (fragmentMarketBinding.marketRv.getAdapter() == null) {
                        marketRVAdapter = new MarketRVAdapter((ArrayList<DataItem>) dataItemList);
                        fragmentMarketBinding.marketRv.setAdapter(marketRVAdapter);
                    } else {
                        marketRVAdapter = (MarketRVAdapter) fragmentMarketBinding.marketRv.getAdapter();

                        if (filteredList.isEmpty() || filteredList.size() == 1000) {
                            marketRVAdapter.updateData((ArrayList<DataItem>) dataItemList);
                        } else {
                            // get All new Data when user searching and filtering
                            marketRVAdapter.updateData((ArrayList<DataItem>) filteredList);
                        }
                    }


                });
        compositeDisposable.add(disposable);
    }

    private void getCryptoMarketDataFromDb() {
        Disposable disposable = appViewModels.getCryptoMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomMarketEntity -> {
                    CryptoMarketDataModel cryptoMarketDataModel = roomMarketEntity.getCryptoMarketDataModel();

                    //set BTC.D on UI
                    fragmentMarketBinding.CryptoBTCD.setText(cryptoMarketDataModel.getBtc_dominance());
                    String[] str3 = cryptoMarketDataModel.getBcd_change().split(getString(R.string.percent));
                    if (Float.parseFloat(str3[0]) > 0){
                        fragmentMarketBinding.BTCDIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        fragmentMarketBinding.BTCChange.setTextColor(Color.GREEN);
                    }else if (Float.parseFloat(str3[0]) < 0){
                        fragmentMarketBinding.BTCDIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        fragmentMarketBinding.BTCChange.setTextColor(Color.RED);
                    }else {
                        fragmentMarketBinding.BTCDIcon.setBackgroundResource(R.drawable.ic_baseline_horizontal_rule_24);
                        fragmentMarketBinding.BTCChange.setTextColor(Color.WHITE);
                    }
                    fragmentMarketBinding.BTCChange.setText(cryptoMarketDataModel.getBcd_change());


                    //set market cap  on UI
                    fragmentMarketBinding.CryptoMarketCap.setText(cryptoMarketDataModel.getMarketCap());
                    // get market cap without %
                    String[] str = cryptoMarketDataModel.getMarketCap_change().split(getString(R.string.percent));

                    if (Float.parseFloat(str[0]) > 0){
                        fragmentMarketBinding.marketCapIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        fragmentMarketBinding.MarketCapChange.setTextColor(Color.GREEN);
                    }else if (Float.parseFloat(str[0]) < 0){
                        fragmentMarketBinding.marketCapIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        fragmentMarketBinding.MarketCapChange.setTextColor(Color.RED);
                    }else {
                        fragmentMarketBinding.marketCapIcon.setBackgroundResource(R.drawable.ic_baseline_horizontal_rule_24);
                        fragmentMarketBinding.MarketCapChange.setTextColor(Color.WHITE);
                    }
                    fragmentMarketBinding.MarketCapChange.setText(cryptoMarketDataModel.getMarketCap_change());


                    //set market Vol on UI
                    fragmentMarketBinding.CryptoVolume.setText(cryptoMarketDataModel.getVol_24h());
                    //get VolumeChange without %
                    String[] str2 = cryptoMarketDataModel.getVol_change().split(getString(R.string.percent));
                    str2[0] = "23";
                    if (Float.parseFloat(str2[0]) > 0){
                        fragmentMarketBinding.VolumeIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                        fragmentMarketBinding.VolumeChange.setTextColor(Color.GREEN);
                    }else if (Float.parseFloat(str2[0]) < 0){
                        fragmentMarketBinding.VolumeIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                        fragmentMarketBinding.VolumeChange.setTextColor(Color.RED);
                    }else {
                        fragmentMarketBinding.VolumeIcon.setBackgroundResource(R.drawable.ic_baseline_horizontal_rule_24);
                        fragmentMarketBinding.VolumeChange.setTextColor(Color.WHITE);
                    }
                    fragmentMarketBinding.VolumeChange.setText(cryptoMarketDataModel.getVol_change());
                });

        compositeDisposable.add(disposable);
    }

    private void setUpToolbar(View view) {
        NavController navController = Navigation.findNavController(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.marketFragment)
                .setOpenableLayout(mainActivity.drawerLayout)
                .build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_market_tb);

        NavigationUI.setupWithNavController(collapsingToolbarLayout, toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.marketFragment) {
                collapsingToolbarLayout.setTitleEnabled(false);
                toolbar.setNavigationIcon(R.drawable.baseline_sort_24);
                toolbar.setTitle("Market");
                toolbar.setTitleTextColor(Color.WHITE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}