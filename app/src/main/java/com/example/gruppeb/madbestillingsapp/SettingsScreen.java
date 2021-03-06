package com.example.gruppeb.madbestillingsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

public class SettingsScreen extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private Preference aboutScreen;
    private Preference voiceOverSwitch;

    private Boolean voiceOverStatus;

    SharedPreferences settingsSharedPreferences;
    SharedPreferences.Editor editorSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        settingsSharedPreferences = getSharedPreferences("settingsPref", Context.MODE_PRIVATE);

        aboutScreen = findPreference("aboutScreen");
        aboutScreen.setOnPreferenceClickListener(this);

        voiceOverSwitch = findPreference("changeVoiceOver");
        voiceOverSwitch.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == aboutScreen) {
            showAbout();
        }

        if (preference == voiceOverSwitch) {
            voiceOverStatusSwitch();
        }

        return true;
    }

    private void voiceOverStatusSwitch() {
        SwitchPreference soundSwitch = (SwitchPreference) findPreference("soundEffectsSwitch");
        if (soundSwitch != null) {
            soundSwitch.setOnPreferenceChangeListener((preference, o) -> {
                voiceOverStatus = settingsSharedPreferences.getBoolean("soundPref", true);

                if (voiceOverStatus) {
                    editorSettings = settingsSharedPreferences.edit();
                    editorSettings.putBoolean("soundPref", voiceOverStatus);
                    editorSettings.apply();
                } else {
                    editorSettings = settingsSharedPreferences.edit();
                    editorSettings.putBoolean("soundPref", voiceOverStatus);
                    editorSettings.apply();
                }
                return true;
            });
        }
    }

    private void showAbout() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Madbestillingsappen er udviklet af Gruppe B, ITØ 2018.");
        dlgAlert.setTitle("Om appen");
        dlgAlert.create().show();
    }

}
