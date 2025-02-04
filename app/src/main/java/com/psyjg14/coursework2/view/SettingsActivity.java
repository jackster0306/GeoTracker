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
import com.psyjg14.coursework2.model.NavBarManager;
import com.psyjg14.coursework2.R;
import com.psyjg14.coursework2.databinding.ActivitySettingsBinding;
import com.psyjg14.coursework2.viewmodel.SettingsViewModel;


/**
 * Activity responsible for displaying and managing application settings.
 */
public class SettingsActivity extends AppCompatActivity {
    private SettingsViewModel settingsViewModel;

    /**
     * Called when the activity is created. Initializes the UI components and sets up the ViewModel.
     *
     * @param savedInstanceState The saved state of the activity.
     */
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




    /**
     * A fragment that displays the application settings.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        /**
         * Called to create the preferences screen.
         *
         * @param savedInstanceState The saved state of the fragment.
         * @param rootKey            The key of the preference root (if it exists).
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }

        /**
         * Called when the fragment is resumed. Registers the preference change listener and loads preferences.
         */
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            loadPreferences();
        }

        /**
         * Called when the fragment is paused. Unregisters the preference change listener.
         */
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Called when a shared preference is changed.
         *
         * @param sharedPreferences The SharedPreferences that received the change.
         * @param key               The key of the preference that was changed.
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Handle the change in preferences
            if (key.equals(getString(R.string.pref_location_accuracy_key))
                    || key.equals(getString(R.string.pref_unit_system_key))
                    || key.equals(getString(R.string.pref_update_periods_key))
                    || key.equals(getString(R.string.pref_tracking_key))) {
                updatePreferenceSummary(findPreference(key));
            }
        }

        /**
         * Loads the preferences and updates their summaries.
         */
        private void loadPreferences() {
            updatePreferenceSummary(findPreference(getString(R.string.pref_location_accuracy_key)));
            updatePreferenceSummary(findPreference(getString(R.string.pref_unit_system_key)));
            updatePreferenceSummary(findPreference(getString(R.string.pref_update_periods_key)));
            updatePreferenceSummary(findPreference(getString(R.string.pref_tracking_key)));
        }

        /**
         * Updates the summary of the given preference based on its current value.
         *
         * @param preference The preference to update.
         */
        private void updatePreferenceSummary(Preference preference) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                if(listPreference.getEntry() == null){
                    listPreference.setValueIndex(0);
                }
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
                } else if (listPreference.getKey().equals(getString(R.string.pref_tracking_key))) {
                    String originalSummary = getString(R.string.pref_tracking_summary);
                    String summary = String.format("%s<br/><b>%s</b>", originalSummary, listPreference.getEntry());
                    preference.setSummary(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                }
            }
        }
    }




    /**
     * Handles the back button press, navigating to the previous screen.
     */
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