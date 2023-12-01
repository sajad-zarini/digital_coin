package com.example.digitalcoin.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.digitalcoin.R;

import java.util.ArrayList;

public class AppViewModels extends AndroidViewModel {

    MutableLiveData<ArrayList<Integer>> mutableLiveData = new MutableLiveData<>();

    public AppViewModels(@NonNull Application application) {
        super(application);
        getViewPagerData();
    }

    MutableLiveData<ArrayList<Integer>> getViewPagerData() {
        ArrayList<Integer> pics = new ArrayList<>();
        pics.add(R.drawable.p1);
        pics.add(R.drawable.p2);
        pics.add(R.drawable.p3);
        pics.add(R.drawable.p4);
        pics.add(R.drawable.p5);

        mutableLiveData.postValue(pics);

        return mutableLiveData;
    }

    public MutableLiveData<ArrayList<Integer>> getMutableLiveData() {
        return mutableLiveData;
    }
}
