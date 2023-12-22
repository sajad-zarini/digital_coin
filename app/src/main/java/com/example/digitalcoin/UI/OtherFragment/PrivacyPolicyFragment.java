package com.example.digitalcoin.UI.OtherFragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentPrivacyPolicyBinding;

import io.reactivex.rxjava3.annotations.NonNull;
import me.ibrahimsn.lib.SmoothBottomBar;

public class PrivacyPolicyFragment extends Fragment {

    FragmentPrivacyPolicyBinding fragmentPrivacyPolicyBinding;
    SmoothBottomBar bottomNavigationBar;

    MainActivity mainActivity;


    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPrivacyPolicyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_policy,container,false);
        HideBottomNavigationBar();


        // Inflate the layout for this fragment
        return fragmentPrivacyPolicyBinding.getRoot();
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.privacy_policy_fragment)
                .build();
        Toolbar toolbar = view.findViewById(R.id.empty_toolbar);


        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.privacy_policy_fragment){
                toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
                toolbar.setTitle(R.string.privacy_policy);
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