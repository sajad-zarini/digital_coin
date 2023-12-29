package com.example.digitalcoin.ui.other;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentSettingBinding;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import me.ibrahimsn.lib.SmoothBottomBar;

public class SettingFragment extends Fragment {

    FragmentSettingBinding fragmentSettingBinding;
    MainActivity mainActivity;

    ArrayList<String> langString = new ArrayList<>();

    SmoothBottomBar bottomNavigationBar;

    String versionName;

    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSettingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting,container,false);
        HideBottomNavigationBar();

        setupSpinner();
        setVersionTxt();

        // Inflate the layout for this fragment
        return fragmentSettingBinding.getRoot();
    }

    private void setVersionTxt() {
        try {
            versionName = requireActivity().getApplicationContext().getPackageManager().getPackageInfo(requireActivity().getApplicationContext().getPackageName(), 0).versionName;
            fragmentSettingBinding.versionTxt.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupSpinner() {
        langString.add("English");
        langString.add("Persian");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(),android.R.layout.simple_spinner_item,langString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentSettingBinding.spinner.setEnabled(true);
        fragmentSettingBinding.spinner.setAdapter(adapter);

        fragmentSettingBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fragmentSettingBinding.switchButton.setEnableEffect(true);
        fragmentSettingBinding.switchButton.setEnabled(true);
        fragmentSettingBinding.switchButton.setOnCheckedChangeListener((view, isChecked) -> {

        });
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.setting_fragment)
                .build();
        Toolbar toolbar = view.findViewById(R.id.empty_toolbar);


        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.setting_fragment){
                toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
                toolbar.setTitle("Settings");
            }
        });
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull @NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
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
}