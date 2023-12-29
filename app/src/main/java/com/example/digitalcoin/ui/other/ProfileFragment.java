package com.example.digitalcoin.ui.other;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digitalcoin.MainActivity;
import com.example.digitalcoin.R;
import com.example.digitalcoin.databinding.FragmentProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import me.ibrahimsn.lib.SmoothBottomBar;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding fragmentProfileBinding;

    MainActivity mainActivity;

    SmoothBottomBar bottomNavigationBar;
    String picturePath;
    String first_name, last_name, mail;
    String ImgFromStore;

    ActivityResultLauncher<Intent> GetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Handle the returned Uri
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = requireActivity().getContentResolver().query(Objects.requireNonNull(selectedImage), filePathColumn, null, null, null);
                        Objects.requireNonNull(cursor).moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);
                        cursor.close();


                        fragmentProfileBinding.roundedImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    }
                }
            });

    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);


        ReadDataStore();
        setDefaultValue();

        HideBottomNavigationBar();
        getPhotoFromGallery();
        setupSaveBtn();

        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
    }

    private void setupToolbar(View view) {
        NavController navController = Navigation.findNavController(view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.profile_fragment)
                .build();
        Toolbar toolbar = view.findViewById(R.id.profile_toolbar);


        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.profile_fragment) {
                toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
                toolbar.setTitle("Profile");
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

    private void getPhotoFromGallery() {
        fragmentProfileBinding.changeImg.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                GetContent.launch(cameraIntent);
            }
        });
    }

    private void setupSaveBtn() {
        fragmentProfileBinding.saveBtn.setOnClickListener(v -> {
            writeToDataStore();
            Snackbar.make(fragmentProfileBinding.profileCon, "Changes saved", 1500).show();
        });
    }

    private void ReadDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        ImgFromStore = sharedPrefs.getString("profileImg", null);
        first_name = sharedPrefs.getString("firstname", "");
        last_name = sharedPrefs.getString("lastname", "");
        mail = sharedPrefs.getString("male", "");
    }


    // Write BookMarks ArrayList From Shared Preference
    private void writeToDataStore() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        if (picturePath != null) {
            editor.putString("profileImg", encodeToBase64(BitmapFactory.decodeFile(picturePath)));
        }
        editor.putString("firstname", fragmentProfileBinding.editTextTextPersonName.getText().toString());
        editor.putString("lastname", fragmentProfileBinding.editTextTextPersonName2.getText().toString());
        editor.putString("male", fragmentProfileBinding.editTextTextPersonName3.getText().toString());
        editor.apply();
    }

    // decode string to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // method for bitmap to base64
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void setDefaultValue() {
        if (ImgFromStore == null) {
            fragmentProfileBinding.roundedImageView.setImageResource(R.drawable.profile_placeholder);
        } else {
            fragmentProfileBinding.roundedImageView.setImageBitmap(decodeBase64(ImgFromStore));
        }
        fragmentProfileBinding.editTextTextPersonName.setText(first_name);
        fragmentProfileBinding.editTextTextPersonName2.setText(last_name);
        fragmentProfileBinding.editTextTextPersonName3.setText(mail);
    }
}