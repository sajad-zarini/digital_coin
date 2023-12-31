package com.example.digitalcoin.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.ui.home.adapter.GainLoseRvAdapter;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentTopGainLoserBinding;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.models.cryptoListModel.DataItem;
import com.example.digitalcoin.viewmodel.AppViewModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TopGainLoserFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentTopGainLoserBinding fragmentTopGainLoserBinding;
    AppViewModels appViewModels;
    CompositeDisposable compositeDisposable;
    List<DataItem> data;
    GainLoseRvAdapter gainLoseRvAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTopGainLoserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_gain_loser, container, false);
        compositeDisposable = new CompositeDisposable();

        Bundle args = getArguments();
        int pos = args.getInt("pos");

        appViewModels = new ViewModelProvider(requireActivity()).get(AppViewModels.class);

        setupRecyclerView(pos);

        return fragmentTopGainLoserBinding.getRoot();
    }

    public void setupRecyclerView(int pos) {

        Disposable disposable = appViewModels.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(roomMarketEntity -> {
                    AllMarketModel allMarketModel = roomMarketEntity.getAllMarketModel();
                    data = allMarketModel.getData().getCryptoCurrencyList();

                    // sort Model list by change percent (lowest to highest)
                    Collections.sort(data, new Comparator<DataItem>() {
                        @Override
                        public int compare(DataItem o1, DataItem o2) {
                            return Integer.valueOf((int) o1.getQuotes().get(0).getPercentChange24h()).compareTo((int) o2.getQuotes().get(0).getPercentChange24h());
                        }
                    });

                    try {
                        ArrayList<DataItem> dataItems = new ArrayList<>();
                        //if page was top Gainers
                        if (pos == 0){
                            //get 10 last Item
                            for (int i = 0;i < 10;i++){
                                dataItems.add(data.get(data.size() - 1 - i));
                            }

                            //if page was top Losers
                        }else if (pos == 1){
                            //get 10 first Item
                            for (int i = 0;i < 10;i++){
                                dataItems.add(data.get(i));
                            }
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        fragmentTopGainLoserBinding.gainLoseRv.setLayoutManager(linearLayoutManager);

                        if (fragmentTopGainLoserBinding.gainLoseRv.getAdapter() == null){
                            gainLoseRvAdapter = new GainLoseRvAdapter(dataItems);
                            fragmentTopGainLoserBinding.gainLoseRv.setAdapter(gainLoseRvAdapter);
                        }else {
                            gainLoseRvAdapter = (GainLoseRvAdapter) fragmentTopGainLoserBinding.gainLoseRv.getAdapter();
                            gainLoseRvAdapter.updateData(dataItems);
                        }
                        fragmentTopGainLoserBinding.gainloseTashieLoader.setVisibility(View.GONE);

                    }catch (Exception e){
                        Log.e("exception", "setupRecyclerView: " + e.getMessage());
                    }

                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}