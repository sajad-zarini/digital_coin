package com.example.digitalcoin.UI.HomeFragment.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.GainLoseRvItemBinding;
import com.example.digitalcoin.Models.cryptoListModel.DataItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class GainLoseRvAdapter extends RecyclerView.Adapter<GainLoseRvAdapter.GainLoseRvHolder> {

    ArrayList<DataItem> dataItems;
    LayoutInflater layoutInflater;

    public GainLoseRvAdapter(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public GainLoseRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        GainLoseRvItemBinding gainLoseRvItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.gain_lose_rv_item,parent,false);
        return new GainLoseRvHolder(gainLoseRvItemBinding);
    }

    @Override
    public void onBindViewHolder(GainLoseRvHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(dataItems.get(position));

        // set onclick for RecyclerView Items
        holder.gainLoseRvItemBinding.GainLoseRVCon.setOnClickListener(v -> { });

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

    static class GainLoseRvHolder extends RecyclerView.ViewHolder {

        GainLoseRvItemBinding gainLoseRvItemBinding;

        public GainLoseRvHolder(GainLoseRvItemBinding gainLoseRvItemBinding) {
            super(gainLoseRvItemBinding.getRoot());
            this.gainLoseRvItemBinding = gainLoseRvItemBinding;
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(DataItem dataItem){

            loadCoinLogo(dataItem);
            loadChart(dataItem);
            SetColorText(dataItem);
            gainLoseRvItemBinding.GLCoinName.setText(dataItem.getName());
            gainLoseRvItemBinding.glCoinSymbol.setText(dataItem.getSymbol());
            SetDecimalsForPrice(dataItem);
            //set + or - before percent change
            if (dataItem.getQuotes().get(0).getPercentChange24h() > 0){
                gainLoseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up_24);
                gainLoseRvItemBinding.glCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }else if (dataItem.getQuotes().get(0).getPercentChange24h() < 0){
                gainLoseRvItemBinding.UpDownIcon.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_down_24);
                gainLoseRvItemBinding.glCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }else {
                gainLoseRvItemBinding.glCoinChange.setText(String.format("%.2f",dataItem.getQuotes().get(0).getPercentChange24h()) + "%");
            }
            gainLoseRvItemBinding.executePendingBindings();
        }

        private void loadCoinLogo(DataItem dataItem) {
            Glide.with(gainLoseRvItemBinding.getRoot().getContext())
                    .load("https://s2.coinmarketcap.com/static/img/coins/32x32/" + dataItem.getId() + ".png")
                    .thumbnail(Glide.with(gainLoseRvItemBinding.getRoot().getContext()).load(R.drawable.loading))
                    .into(gainLoseRvItemBinding.gainLoseCoinLogo);
        }

        private void loadChart(DataItem dataItem) {

            SvgLoaderTask task = new SvgLoaderTask(gainLoseRvItemBinding.imageView);
            task.execute("https://s3.coinmarketcap.com/generated/sparklines/web/7d/2781/" + dataItem.getId() + ".svg");
        }

        private static class SvgLoaderTask extends AsyncTask<String, Void, Bitmap> {

            @SuppressLint("StaticFieldLeak")
            private final ImageView imageView;

            public SvgLoaderTask(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String svgUrl = params[0];
                try {
                    InputStream inputStream = new URL(svgUrl).openStream();
                    SVG svg = SVG.getFromInputStream(inputStream);

                    Picture picture = svg.renderToPicture();
                    int width = picture.getWidth();
                    int height = picture.getHeight();

                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawPicture(picture);

                    inputStream.close();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SVGParseException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }

        //set different decimals for different price
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        private void SetDecimalsForPrice(DataItem dataItem) {
            if (dataItem.getQuotes().get(0).getPrice() < 1){
                gainLoseRvItemBinding.glCoinPrice.setText("$" + String.format("%.6f",dataItem.getQuotes().get(0).getPrice()));
            }else if (dataItem.getQuotes().get(0).getPrice() < 10){
                gainLoseRvItemBinding.glCoinPrice.setText("$" + String.format("%.4f",dataItem.getQuotes().get(0).getPrice()));
            }else {
                gainLoseRvItemBinding.glCoinPrice.setText("$" + String.format("%.2f",dataItem.getQuotes().get(0).getPrice()));
            }
        }

        //set Color Green and Red for price
        private void SetColorText(DataItem dataItem){
            int greenColor = Color.parseColor("#FF00FF40");
            int redColor = Color.parseColor("#FFFF0000");
            int whiteColor = Color.parseColor("#FFFFFF");
            if (dataItem.getQuotes().get(0).getPercentChange24h() < 0){
                gainLoseRvItemBinding.imageView.setColorFilter(redColor);
                gainLoseRvItemBinding.glCoinChange.setTextColor(Color.RED);
            }else if (dataItem.getQuotes().get(0).getPercentChange24h() > 0){
                gainLoseRvItemBinding.imageView.setColorFilter(greenColor);
                gainLoseRvItemBinding.glCoinChange.setTextColor(Color.GREEN);
            }else {
                gainLoseRvItemBinding.imageView.setColorFilter(whiteColor);
                gainLoseRvItemBinding.glCoinChange.setTextColor(Color.WHITE);
            }
        }
    }
}
