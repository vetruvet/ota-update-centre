/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may only use this file in compliance with the license and provided you are not associated with or are in co-operation anyone by the name 'X Vanderpoel'.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.updater.ota;

import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class UpdaterSettings extends PreferenceActivity implements OnPreferenceChangeListener {
    private SharedPreferences prefs;

    private CheckBoxPreference notifPref;
    private CheckBoxPreference deletePref;
    private ListPreference intervalPref;
    private CheckBoxPreference ledPref;
    private CheckBoxPreference vibratePref;
    private Preference versionPref;
    private Preference updatePref;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());;

        notifPref = (CheckBoxPreference) findPreference("pref_notif");
        notifPref.setChecked(prefs.getBoolean("notif", false));

        deletePref = (CheckBoxPreference) findPreference("pref_delete");
        deletePref.setChecked(prefs.getBoolean("clean_up", false));

        intervalPref = (ListPreference) findPreference("pref_interval");
        intervalPref.setValue(String.valueOf(prefs.getInt("notif_interval", 1)));
        intervalPref.setSummary(intervalPref.getEntries()[intervalPref.findIndexOfValue(intervalPref.getEntry().toString())]);
        intervalPref.setOnPreferenceChangeListener(this);

        ledPref = (CheckBoxPreference) findPreference("pref_led");
        ledPref.setChecked(prefs.getBoolean("notif_led", false));

        vibratePref = (CheckBoxPreference) findPreference("pref_vibrate");
        vibratePref.setChecked(prefs.getBoolean("notif_vibrate", false));

        versionPref = findPreference("pref_version");
        try {
			versionPref.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			versionPref.setSummary(R.string.pref_version_unknown);
		}

        updatePref = findPreference("pref_update");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        SharedPreferences.Editor editor = prefs.edit();

        if (preference == notifPref) {
            editor.putBoolean("notif", notifPref.isChecked());
        } else if (preference == deletePref) {
            editor.putBoolean("clean_up", deletePref.isChecked());
        } else if (preference == ledPref) {
            editor.putBoolean("notif_led", ledPref.isChecked());
        } else if (preference == vibratePref) {
            editor.putBoolean("notif_vibrate", vibratePref.isChecked());
        } else if (preference == updatePref) {
            //TODO check for app updates
        }

        editor.commit();
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences.Editor editor = prefs.edit();

        if (preference == intervalPref) {
            editor.putInt("notif_interval", Integer.parseInt(newValue.toString()));
        }

        editor.commit();
        return true;
    }
}
