package com.example.digitalcoin.HomeFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
import androidx.viewpager2.widget.ViewPager2;

import com.example.digitalcoin.HomeFragment.adapter.SliderImageAdapter;
import com.example.digitalcoin.HomeFragment.adapter.TopCoinRvAdapter;
import com.example.digitalcoin.HomeFragment.adapter.TopGainLoserAdapter;
import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentHomeBinding;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.models.cryptoListModel.DataItem;
import com.example.digitalcoin.viewModels.AppViewModels;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    FragmentHomeBinding fragmentHomeBinding;
    MainActivity mainActivity;
    AppViewModels appViewModels;

    @Inject
    @Named("fullName")
    String name;

    public List<String> top_wants = Arrays.asList("BTC", "ETH", "BNB", "ADA", "XRP", "DOGE", "DOT", "UNI", "LTC", "LINK");
    TopCoinRvAdapter topCoinRvAdapter;
    CompositeDisposable compositeDisposable;

    TopGainLoserAdapter topGainLoserAdapter;

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
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        appViewModels = new ViewModelProvider(requireActivity()).get(AppViewModels.class);
        compositeDisposable = new CompositeDisposable();

        setUpViewPager2();
        getAllMarketDataFromDB();
        setUpTabLayoutTopGainLose(fragmentHomeBinding.topGainIndicator, fragmentHomeBinding.topLoseIndicator);

        return fragmentHomeBinding.getRoot();
    }

    private void setUpTabLayoutTopGainLose(View topGainIndicator, View topLoseIndicator) {
        topGainLoserAdapter = new TopGainLoserAdapter(this);
        fragmentHomeBinding.viewPager2.setAdapter(topGainLoserAdapter);

        Animation gainAnimIn = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_from_left);
        Animation gainAnimOut = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_out_left);
        Animation loseAnimIn = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_from_right);
        Animation loseAnimOut = AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.slide_out_right);

        fragmentHomeBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (position == 1) {
                    topLoseIndicator.startAnimation(loseAnimOut);
                    topLoseIndicator.setVisibility(View.GONE);
                    topGainIndicator.setVisibility(View.VISIBLE);
                    topGainIndicator.startAnimation(gainAnimIn);

                } else {
                    topGainIndicator.startAnimation(gainAnimOut);
                    topGainIndicator.setVisibility(View.GONE);
                    topLoseIndicator.setVisibility(View.VISIBLE);
                    topLoseIndicator.startAnimation(loseAnimIn);
                }
            }
        });

        new TabLayoutMediator(fragmentHomeBinding.tabLayout, fragmentHomeBinding.viewPager2, (tab, position) -> {

            if (position == 0) {
                tab.setText("TopGainers");
            } else {
                tab.setText("TopLosers");
            }
        }).attach();
    }

    @SuppressLint("CheckResult")
    private void getAllMarketDataFromDB() {
        Disposable disposable = appViewModels.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(marketListEntity -> {
                    AllMarketModel allMarketModel = marketListEntity.getAllMarketModel();

                    ArrayList<DataItem> top10 = new ArrayList<>();
                    for (int i = 0; i < allMarketModel.getData().getCryptoCurrencyList().size(); i++) {
                        for (int j = 0; j < top_wants.size(); j++) {
                            String coin_name = top_wants.get(j);
                            if (allMarketModel.getData().getCryptoCurrencyList().get(i).getSymbol().equals(coin_name)) {
                                DataItem dataItem = allMarketModel.getData().getCryptoCurrencyList().get(i);
                                top10.add(dataItem);
                            }
                        }
                    }

                    if (fragmentHomeBinding.topCoinRv.getAdapter() != null) {
                        topCoinRvAdapter = (TopCoinRvAdapter) fragmentHomeBinding.topCoinRv.getAdapter();
                        topCoinRvAdapter.updateData(top10);
                    } else {
                        topCoinRvAdapter = new TopCoinRvAdapter(top10);
                        fragmentHomeBinding.topCoinRv.setAdapter(topCoinRvAdapter);
                    }

                });
        compositeDisposable.add(disposable);
    }

    private void setUpViewPager2() {
        appViewModels.getMutableLiveData().observe(requireActivity(), arrayList -> {
            fragmentHomeBinding.viewPagerImageSlider.setAdapter(new SliderImageAdapter(arrayList));
            fragmentHomeBinding.viewPagerImageSlider.setOffscreenPageLimit(3);
        });
    }

    private void setUpToolbar(View view) {
        NavController navController = Navigation.findNavController(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment)
                .setOpenableLayout(mainActivity.drawerLayout)
                .build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.homeFragment) {
                toolbar.setNavigationIcon(R.drawable.baseline_sort_24);
                toolbar.setTitle("Digital Coin");
            }
        });
    }
}