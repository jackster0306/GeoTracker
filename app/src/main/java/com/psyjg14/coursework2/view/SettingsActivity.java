package com.psyjg14.coursework2.view;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.psyjg14.coursework2.NavBarManager;
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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsFragment, new SettingsFragment())
                .commit();

        BottomNavigationView navBar;
        navBar = binding.settingsNavBar;

        navBar.setSelectedItemId(R.id.settingsMenu);

        navBar.setOnItemSelectedListener(item -> {
            NavBarManager instance = NavBarManager.getInstance();
            int itemId = item.getItemId();
            return instance.navBarItemPressed(SettingsActivity.this, itemId, R.id.settingsMenu);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);




    }




    public static class SettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            loadPreferences();
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Handle the change in preferences
            if (key.equals(getString(R.string.pref_location_accuracy_key))
                    || key.equals(getString(R.string.pref_unit_system_key))
                    || key.equals(getString(R.string.pref_update_periods_key))) {
                updatePreferenceSummary(findPreference(key));
            }
        }

        private void loadPreferences() {
            updatePreferenceSummary(findPreference(getString(R.string.pref_location_accuracy_key)));
            updatePreferenceSummary(findPreference(getString(R.string.pref_unit_system_key)));
            updatePreferenceSummary(findPreference(getString(R.string.pref_update_periods_key)));
        }

        private void updatePreferenceSummary(Preference preference) {
            // Update the summary of the preference based on the current value
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                if(listPreference.getKey().equals(getString(R.string.pref_location_accuracy_key))){
                    String originalSummary = getString(R.string.pref_location_accuracy_summary);
                    String summary =  String.format("%s<br/><b>%s</b>", originalSummary, listPreference.getEntry());
                    preference.setSummary(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
                else if(listPreference.getKey().equals(getString(R.string.pref_unit_system_key))){
                    String originalSummary = getString(R.string.pref_unit_system_summary);
                    String summary =  String.format("%s<br/><b>%s</b>", originalSummary, listPreference.getEntry());
                    preference.setSummary(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
                else if(listPreference.getKey().equals(getString(R.string.pref_update_periods_key))) {
                    String originalSummary = getString(R.string.pref_update_periods_summary);
                    String summary = String.format("%s<br/><b>%s</b>", originalSummary, listPreference.getEntry());
                    preference.setSummary(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
            }
        }
    }





    public void onBack(){
        NavBarManager.getInstance().onBackPressed(this, R.id.statsMenu);
        finish();
    }

    /**
     * Called when the activity is destroyed.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the activity is restored.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}