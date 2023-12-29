package com.example.digitalcoin.ui.detail;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.DetailFragmentRvItemsBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;

public class DetailRvAdapter extends RecyclerView.Adapter<DetailRvAdapter.DetailRvHolder> {

    ArrayList<String> keys;
    ArrayList<String> values;
    LayoutInflater layoutInflater;

    public DetailRvAdapter(ArrayList<String> keys, ArrayList<String> values) {
        this.keys = keys;
        this.values = values;
    }

    @androidx.annotation.NonNull
    @NonNull
    @Override
    public DetailRvHolder onCreateViewHolder(@androidx.annotation.NonNull @NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        DetailFragmentRvItemsBinding detailFragmentRvItemsBinding = DataBindingUtil.inflate(layoutInflater, R.layout.detail_fragment_rv_items, parent, false);
        return new DetailRvHolder(detailFragmentRvItemsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailRvHolder holder, int position) {
        holder.bind(keys.get(position), values.get(position));

    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    static class DetailRvHolder extends RecyclerView.ViewHolder {

        DetailFragmentRvItemsBinding detailFragmentRvItemsBinding;

        public DetailRvHolder(@NonNull DetailFragmentRvItemsBinding detailFragmentRvItemsBinding) {
            super(detailFragmentRvItemsBinding.getRoot());
            this.detailFragmentRvItemsBinding = detailFragmentRvItemsBinding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(String key, String value) {
            if (key.equals("PercentChange 7d") || key.equals("PercentChange 30d")) {
                setColorText(value, detailFragmentRvItemsBinding.detailValues);
                detailFragmentRvItemsBinding.detailValues.setText(value + "%");
            } else {
                detailFragmentRvItemsBinding.detailValues.setText(value);
            }
            detailFragmentRvItemsBinding.detailKeys.setText(key);
            detailFragmentRvItemsBinding.executePendingBindings();
        }

        private void setColorText(String value, TextView detailValues) {
            if (Float.parseFloat(value) < 0) {
                detailValues.setTextColor(Color.RED);
            } else if (Float.parseFloat(value) > 0) {
                detailValues.setTextColor(Color.GREEN);
            } else {
                detailValues.setTextColor(Color.WHITE);
            }

        }
    }
}
