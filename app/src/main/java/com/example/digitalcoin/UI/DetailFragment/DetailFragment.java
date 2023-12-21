package com.example.digitalcoin.UI.DetailFragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {

    FragmentDetailBinding fragmentDetailBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        fragmentDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);


        fragmentDetailBinding.bckbtn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return fragmentDetailBinding.getRoot();
    }
}