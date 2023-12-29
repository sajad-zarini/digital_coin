package com.example.digitalcoin.ui.watchList;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.WatchListRvItemBinding;
import com.example.digitalcoin.models.cryptoListModel.DataItem;

import java.util.ArrayList;

public class WatchlistRVAdapter extends RecyclerView.Adapter<WatchlistRVAdapter.watchlistRVHolder> {

    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public WatchlistRVAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public WatchlistRVAdapter.watchlistRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        WatchListRvItemBinding watchlistRvItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.watch_list_rv_item, parent, false);
        return new watchlistRVHolder(watchlistRvItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistRVAdapter.watchlistRVHolder holder, int position) {
        holder.bind(dataItems.get(position));
        holder.watchListRvItemBinding.watchedCardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("model", dataItems.get(position));

            Navigation.findNavController(v).navigate(R.id.action_watchListFragment_to_detailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<DataItem> newData) {
        dataItems = newData;
        notifyDataSetChanged();
    }

    public static class watchlistRVHolder extends RecyclerView.ViewHolder {

        WatchListRvItemBinding watchListRvItemBinding;

        public watchlistRVHolder(@NonNull WatchListRvItemBinding watchListRvItemBinding) {
            super(watchListRvItemBinding.getRoot());
            this.watchListRvItemBinding = watchListRvItemBinding;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(DataItem dataItem) {
            loadCoinLogo(dataItem);
            loadCoinChart(dataItem);
            SetColorText(dataItem);
            SetDecimalsForPrice(dataItem);
            watchListRvItemBinding.watchedCoinName.setText(dataItem.getSymbol());
            watchListRvItemBinding.watchedCoinFullName.setText(dataItem.getName());

            //set + or - before percent change
            if (dataItem.getQuotes().get(0).getPercentChange24h() > 0) {
                watchListRvItemBinding.watchedCoinIconChange.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                watchListRvItemBinding.watchedCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            } else if (dataItem.getQuotes().get(0).getPercentChange24h() < 0) {
                watchListRvItemBinding.watchedCoinIconChange.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                watchListRvItemBinding.watchedCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            } else {
                watchListRvItemBinding.watchedCoinChange.setText(String.format("%.2f", dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }

            watchListRvItemBinding.executePendingBindings();
        }

        private void loadCoinLogo(DataItem dataItem) {
            Glide.with(watchListRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/64x64/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(watchListRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(watchListRvItemBinding.coinWatchLogo);
        }


        private void loadCoinChart(DataItem dataItem) {
            Glide.with(watchListRvItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(watchListRvItemBinding.watchedCoinChart);
        }

        //set Color Green and Red for price and chart
        private void SetColorText(DataItem dataItem) {
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getQuotes().get(0).getPercentChange24h() < 0) {
                watchListRvItemBinding.watchedCoinChart.setColorFilter(redColor);
                watchListRvItemBinding.watchedCoinChange.setTextColor(Color.RED);
            } else if (dataItem.getQuotes().get(0).getPercentChange24h() > 0) {
                watchListRvItemBinding.watchedCoinChart.setColorFilter(greenColor);
                watchListRvItemBinding.watchedCoinChange.setTextColor(Color.GREEN);
            } else {
                watchListRvItemBinding.watchedCoinChart.setColorFilter(whiteColor);
                watchListRvItemBinding.watchedCoinChange.setTextColor(Color.WHITE);
            }
        }

        //set different decimals for different price
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getQuotes().get(0).getPrice() < 1) {
                watchListRvItemBinding.watchedCoinPrice.setText("$" + String.format("%.6f", dataItem.getQuotes().get(0).getPrice()));
            } else if (dataItem.getQuotes().get(0).getPrice() < 10) {
                watchListRvItemBinding.watchedCoinPrice.setText("$" + String.format("%.4f", dataItem.getQuotes().get(0).getPrice()));
            } else {
                watchListRvItemBinding.watchedCoinPrice.setText("$" + String.format("%.2f", dataItem.getQuotes().get(0).getPrice()));
            }
        }

    }
}
