package com.example.digitalcoin.MarketFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentMarketBinding;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.models.cryptoListModel.DataItem;
import com.example.digitalcoin.viewModels.AppViewModels;
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

        setupViewModel();
        getMarketListDataFromDb();

        return fragmentMarketBinding.getRoot();
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

//                        if (filteredList.isEmpty() || filteredList.size() == 1000){
                        marketRVAdapter.updateData((ArrayList<DataItem>) dataItemList);
//                        }else {
                        //get All new Data when user searching and filtering
//                            marketRVAdapter.updateData((ArrayList<DataItem>) filteredList);
//                        }
                    }


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