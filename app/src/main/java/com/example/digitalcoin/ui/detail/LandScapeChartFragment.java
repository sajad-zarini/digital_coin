package com.example.digitalcoin.ui.detail;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.models.cryptoListModel.DataItem;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentLandScapeChartBinding;

import me.ibrahimsn.lib.SmoothBottomBar;

public class LandScapeChartFragment extends Fragment {

    SmoothBottomBar bottomNavigationBar;
    FragmentLandScapeChartBinding fragmentLandScapeChartBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        fragmentLandScapeChartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_land_scape_chart, container, false);

        // get Data from Bundle
        DataItem dataItem = getArguments() != null ? getArguments().getParcelable("model") : null;


        HideBottomNavigationBar();

        if (dataItem != null) {
            setupChart(dataItem);
        }

        // Inflate the layout for this fragment
        return fragmentLandScapeChartBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        bottomNavigationBar.setVisibility(View.VISIBLE);
    }

    private void HideBottomNavigationBar() {
        bottomNavigationBar = requireActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setVisibility(View.GONE);
    }

    // setup Chart (WebView) for first Time
    @SuppressLint("SetJavaScriptEnabled")
    private void setupChart(DataItem dataItem) {
        fragmentLandScapeChartBinding.chartWebView.getSettings().setJavaScriptEnabled(true);
        fragmentLandScapeChartBinding.chartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        fragmentLandScapeChartBinding.chartWebView.loadUrl("https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + dataItem.getSymbol() + "USD&interval=D&hidesidetoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT");

    }

}