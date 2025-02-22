package com.fieldbook.tracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;
import com.fieldbook.tracker.R;
import com.fieldbook.tracker.preferences.AppearancePreferencesFragment;
import com.fieldbook.tracker.preferences.GeneralKeys;
import com.fieldbook.tracker.preferences.PreferencesFragment;
import com.fieldbook.tracker.preferences.ProfilePreferencesFragment;

public class PreferencesActivity extends ThemedActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, SearchPreferenceResultListener {

    private PreferencesFragment prefsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.settings_advanced));
            getSupportActionBar().getThemedContext();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        prefsFragment = new PreferencesFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, prefsFragment).commit();

        //parse passed bundle and check if the person should be updated.
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean(GeneralKeys.PERSON_UPDATE, false)) {

            //starts fragment with bundle that tells the fragment to open the person setting
            Fragment profile = new ProfilePreferencesFragment();
            Bundle personUpdate = new Bundle();
            personUpdate.putBoolean(GeneralKeys.PERSON_UPDATE, true);
            profile.setArguments(personUpdate);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, profile).commit();

        } else if (extras != null && extras.getBoolean(GeneralKeys.MODIFY_PROFILE_SETTINGS, false)) {

            //starts profile fragment without opening the person setting
            Fragment profile = new ProfilePreferencesFragment();
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, profile).commit();

        }

        boolean flag = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GeneralKeys.THEME_FLAG, false);
        if (flag) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new AppearancePreferencesFragment()).addToBackStack("PrefsFrag").commit();
        }
    }

    @Override
    public void onSearchResultClicked(SearchPreferenceResult result) {
        prefsFragment = new PreferencesFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, prefsFragment).addToBackStack("PrefsFragment").commit(); // Allow to navigate back to search

        new Handler().post(new Runnable() { // Allow fragment to get created
            @Override
            public void run() {
                prefsFragment.onSearchResultClicked(result);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, androidx.preference.Preference pref) {

        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(getClassLoader(), pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);

        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).addToBackStack(null).commit();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}