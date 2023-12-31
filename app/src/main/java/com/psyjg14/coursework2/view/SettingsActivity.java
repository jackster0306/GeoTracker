package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivitySettingsBinding;
import com.psyjg14.coursework2.viewmodel.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setViewModel(settingsViewModel);
        binding.setLifecycleOwner(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void onBack(){
        finish();
    }

}