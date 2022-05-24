package com.zizo.zizotodo.Settings;

import static android.content.Context.MODE_PRIVATE;

import static com.zizo.zizotodo.Main.MainFragment.THEME_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;


import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.zizo.zizotodo.Main.MainFragment;
import com.zizo.zizotodo.R;

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_layout);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("NightModePreference")){
            SharedPreferences themePreferences = getActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor = themePreferences.edit();
            //We tell our MainLayout to recreate itself because mode has changed
            themeEditor.putBoolean(MainFragment.RECREATE_ACTIVITY, true);

            CheckBoxPreference checkBoxPreference = findPreference("NightModePreference");
            if (checkBoxPreference.isChecked()) {
                themeEditor.putString(MainFragment.THEME_SAVED, MainFragment.DARKTHEME);
            } else {
                themeEditor.putString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME);
            }
            themeEditor.apply();

            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
