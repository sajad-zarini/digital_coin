package com.example.digitalcoin.MarketFragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.MarketFragRvItemBinding;
import com.example.digitalcoin.models.cryptoListModel.DataItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;

public class MarketRVAdapter extends RecyclerView.Adapter<MarketRVAdapter.MarketRVHolder> {

    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public MarketRVAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @NotNull
    @Override
    public MarketRVHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        MarketFragRvItemBinding marketFragRvItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.market_frag_rv_item,parent,false);
        return new MarketRVHolder(marketFragRvItemBinding);
    }

    @Override
    public void onBindViewHolder(MarketRVHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataItems.get(position),position);

        /// set onclick for RecyclerView Items

        holder.marketFragRvItemBinding.marketRVCon.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("model",dataItems.get(position));

//                Navigation.findNavController(v).navigate(R.id.action_marketFragment_to_detailFragment,bundle);


        });
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<DataItem> newdata) {
        dataItems = newdata;
        notifyDataSetChanged();

    }

    static class MarketRVHolder extends RecyclerView.ViewHolder {
        MarketFragRvItemBinding marketFragRvItemBinding;

        public MarketRVHolder(MarketFragRvItemBinding marketFragRvItemBinding) {
            super(marketFragRvItemBinding.getRoot());
            this.marketFragRvItemBinding = marketFragRvItemBinding;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(DataItem dataItem, int position){

            loadCoinlogo(dataItem);
            loadChart(dataItem);
            SetColorText(dataItem);
            marketFragRvItemBinding.marketCoinNumber.setText(String.valueOf(position + 1));
            marketFragRvItemBinding.marketCoinName.setText(dataItem.getName());
            marketFragRvItemBinding.marketCoinSymbol.setText(dataItem.getSymbol());
            SetDecimalsForPrice(dataItem);
            //set + or - before precent change
            if (dataItem.getQuotes().get(0).getPercentChange24h() > 0){
                marketFragRvItemBinding.MarketUpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                marketFragRvItemBinding.marketCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }else if (dataItem.getQuotes().get(0).getPercentChange24h() < 0){
                marketFragRvItemBinding.MarketUpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                marketFragRvItemBinding.marketCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }else {
                marketFragRvItemBinding.marketCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }
            marketFragRvItemBinding.executePendingBindings();
        }

        private void loadCoinlogo(DataItem dataItem) {
            Glide.with(marketFragRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/32x32/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(marketFragRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(marketFragRvItemBinding.coinLogo);
        }

        private void loadChart(DataItem dataItem) {

            Glide.with(marketFragRvItemBinding.getRoot().getContext())
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/" + dataItem.getId() + ".png")
                    .into(marketFragRvItemBinding.chartImage);
        }

        /// set different decimals for different price
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getQuotes().get(0).getPrice() < 1){
                marketFragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.6f",dataItem.getQuotes().get(0).getPrice()));
            }else if (dataItem.getQuotes().get(0).getPrice() < 10){
                marketFragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.4f",dataItem.getQuotes().get(0).getPrice()));
            }else {
                marketFragRvItemBinding.marketCoinPrice.setText("$" + String.format("%.2f",dataItem.getQuotes().get(0).getPrice()));
            }
        }

        /// set Color Green and Red for price and chart
        private void SetColorText(DataItem dataItem){
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getQuotes().get(0).getPercentChange24h() < 0){
                marketFragRvItemBinding.chartImage.setColorFilter(redColor);
                marketFragRvItemBinding.marketCoinChange.setTextColor(Color.RED);
            }else if (dataItem.getQuotes().get(0).getPercentChange24h() > 0){
                marketFragRvItemBinding.chartImage.setColorFilter(greenColor);
                marketFragRvItemBinding.marketCoinChange.setTextColor(Color.GREEN);
            }else {
                marketFragRvItemBinding.chartImage.setColorFilter(whiteColor);
                marketFragRvItemBinding.marketCoinChange.setTextColor(Color.WHITE);
            }
        }
    }

}
