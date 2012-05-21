/*
 * Copyright (C) 2012 VillainROM
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


package jieehd.villain.updater;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
 
public class villainsettings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            Preference clean_up = (Preference) findPreference("clean_up");
            clean_up.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("clean_up","User would like to clean old files");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            Preference update_notify = (Preference) findPreference("update_notify");
            update_notify.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("update_notify","User would like push notifications of updates");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            
            Preference update_interval = (Preference) findPreference("list_interval");
            update_interval.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("list_interval","User has chosen how often to look for updates");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            
            Preference info_show = (Preference) findPreference("info_show");
            info_show.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("info_show","User has chosen whether or not to display device information");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            
            Preference pulse_light = (Preference) findPreference("pulse_light");
            pulse_light.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("pulse_light","User has chosen whether or not to pulse notification light");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            Preference vibrate_notify = (Preference) findPreference("vibrate_notify");
            vibrate_notify.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("vibrate_notify","User has chosen whether or not to vibrate for notifications");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
            
            Preference check_update = (Preference) findPreference("tv_check_update");
            check_update.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                                    public boolean onPreferenceClick(Preference preference) {
                                            SharedPreferences customSharedPreference = getSharedPreferences("preferences_Shared", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = customSharedPreference.edit();
                                            editor.putString("info_show","User has chosen whether or not to display device information");
                                            editor.commit();
                                            return true;
                                    }

                            });
            
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

	public void onSharedPreferenceChanged(SharedPreferences prefs, String info_show) {
		// TODO Auto-generated method stub
		
	}
    
}
