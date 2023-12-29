package com.example.digitalcoin.ui.home.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.digitalcoin.ui.home.TopGainLoserFragment;

public class TopGainLoserAdapter extends FragmentStateAdapter {

    public TopGainLoserAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new TopGainLoserFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
