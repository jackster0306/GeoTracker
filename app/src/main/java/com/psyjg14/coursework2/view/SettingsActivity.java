package com.psyjg14.coursework2.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivityMainBinding;
import com.psyjg14.coursework2.databinding.ActivitySettingsBinding;
import com.psyjg14.coursework2.databinding.ActivityViewDataBinding;
import com.psyjg14.coursework2.viewmodel.MainActivityViewModel;
import com.psyjg14.coursework2.viewmodel.SettingsViewModel;
import com.psyjg14.coursework2.viewmodel.ViewDataViewModel;

public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding.setViewModel(settingsViewModel);
        binding.setLifecycleOwner(this);
    }
}