package com.example.digitalcoin.HomeFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentTopGainLoserBinding;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class TopGainLoserFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FragmentTopGainLoserBinding fragmentTopGainLoserBinding;

    CompositeDisposable compositeDisposable;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTopGainLoserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_gain_loser, container, false);
        compositeDisposable = new CompositeDisposable();


        Bundle args = getArguments();
        int pos = args.getInt("pos");
        fragmentTopGainLoserBinding.textview3.setText("This is " + pos);



        return fragmentTopGainLoserBinding.getRoot();
    }
}