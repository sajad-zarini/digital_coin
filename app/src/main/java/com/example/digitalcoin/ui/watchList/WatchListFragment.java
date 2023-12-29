package com.example.digitalcoin.ui.watchList;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentWatchListBinding;
import com.example.digitalcoin.models.cryptoListModel.AllMarketModel;
import com.example.digitalcoin.models.cryptoListModel.DataItem;
import com.example.digitalcoin.room.entities.MarketListEntity;
import com.example.digitalcoin.viewmodel.AppViewModels;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WatchListFragment extends Fragment {

    FragmentWatchListBinding fragmentWatchListBinding;
    MainActivity mainActivity;

    CompositeDisposable compositeDisposable;
    AppViewModels appViewModels;
    WatchlistRVAdapter watchlistRVAdapter;

    ArrayList<String> bookmarksArray;
    ArrayList<DataItem> finalData;
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
        // Inflate the layout for this fragment
        fragmentWatchListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_watch_list, container, false);
        appViewModels = new ViewModelProvider(requireActivity()).get(AppViewModels.class);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity().getApplicationContext());

        finalData = new ArrayList<>();

        compositeDisposable = new CompositeDisposable();

        //Read Data watch listed in shared Preference
        ReadDataStore();

        //if data is empty show "there is no item" txt
        if (bookmarksArray.size() == 0) {
            fragmentWatchListBinding.watchlistNoItemTxt.setVisibility(View.VISIBLE);
        }

        //get all Data list from RoomDB
        getMarketListDataFromDb();
        return fragmentWatchListBinding.getRoot();
    }

    private void getMarketListDataFromDb() {
        Disposable disposable = appViewModels.getAllMarketData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MarketListEntity>() {
                    @Override
                    public void accept(MarketListEntity roomMarketEntity) {
                        AllMarketModel allMarketModel = roomMarketEntity.getAllMarketModel();
                        dataItemList = allMarketModel.getData().getCryptoCurrencyList();

                        finalData.clear();
                        for (int i = 0; i < bookmarksArray.size(); i++) {
                            for (int j = 0; j < dataItemList.size(); j++) {
                                if (bookmarksArray.get(i).equals(dataItemList.get(j).getSymbol())) {
                                    finalData.add(dataItemList.get(j));
                                }
                            }
                        }


                        if (fragmentWatchListBinding.watchlistRv.getAdapter() != null) {
                            watchlistRVAdapter = (WatchlistRVAdapter) fragmentWatchListBinding.watchlistRv.getAdapter();
                            watchlistRVAdapter.updateData(finalData);
                        } else {
                            watchlistRVAdapter = new WatchlistRVAdapter(finalData);
                            fragmentWatchListBinding.watchlistRv.setAdapter(watchlistRVAdapter);
                        }

                        // check recyclerView changes like remove item and ....
                        watchlistRVAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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

                            // check if RV adapter is empty show "there is no item" txt
                            void checkEmpty() {
                                if (watchlistRVAdapter.getItemCount() == 0) {
                                    fragmentWatchListBinding.watchlistNoItemTxt.setVisibility(View.VISIBLE);
                                } else {
                                    fragmentWatchListBinding.watchlistNoItemTxt.setVisibility(View.GONE);
                                }
                            }
                        });


                        //setup swiping and delete
                        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                            @Override
                            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                int pos = viewHolder.getAdapterPosition();
                                fragmentWatchListBinding.watchlistRv.removeViewAt(pos);
                                finalData.remove(pos);

                                // remove Item from Array (arraylist from shared Preference)
                                bookmarksArray.remove(pos);
                                writeToDataStore(bookmarksArray);
                                watchlistRVAdapter.notifyItemRemoved(pos);
                            }
                        };
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(fragmentWatchListBinding.watchlistRv);

                    }
                });

        compositeDisposable.add(disposable);
    }

    /// Write BookMarks ArrayList From Shared Preference
    private void writeToDataStore(ArrayList<String> newArray) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(newArray);

        editor.putString("bookmarks", json);
        editor.apply();
    }

    /// read new BookMark on Shared Preference
    private void ReadDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        Gson gson = new Gson();
        String json = sharedPrefs.getString("bookmarks", String.valueOf(new ArrayList<String>()));
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        bookmarksArray = gson.fromJson(json, type);
    }

    private void setUpToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.watchListFragment)
                .setOpenableLayout(mainActivity.drawerLayout)
                .build();

        Toolbar toolbar = view.findViewById(R.id.toolbar);


        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        /// set Click listener for profile menu Btn
        toolbar.setOnMenuItemClickListener(item -> NavigationUI.onNavDestinationSelected(item, navController));

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.watchListFragment) {
                toolbar.setNavigationIcon(R.drawable.baseline_sort_24);
                toolbar.setTitle("WatchList");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}