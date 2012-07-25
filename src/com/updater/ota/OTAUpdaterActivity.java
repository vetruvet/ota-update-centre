/*
 * Copyright (C) 2012 OTA Updater
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.updater.ota.FetchRomInfoTask.RomInfoListener;

public class OTAUpdaterActivity extends PreferenceActivity {
    protected static final String NOTIF_ACTION = "com.updater.ota.action.NOTIF_ACTION";

    private boolean ignoredDataWarn = false;
    
    private boolean checkOnResume = false;
    private Config cfg;
    
    private boolean fetching = false;
    private Preference availUpdatePref;

    /** Called when the activity is first created. */
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cfg = Config.getInstance(getApplicationContext());
        
        if (!Utils.isROMSupported()) {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setTitle(R.string.alert_unsupported_title);
        	alert.setMessage(R.string.alert_unsupported_message);
        	alert.setCancelable(false);
        	alert.setPositiveButton(R.string.alert_exit, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
        	alert.create().show();
        	
        	if (Utils.marketAvailable(this)) {
        	    GCMRegistrar.checkDevice(getApplicationContext());
                GCMRegistrar.checkManifest(getApplicationContext());
                final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
                if (regId.length() != 0) {
                    GCMRegistrar.unregister(getApplicationContext());
                }
        	}
        } else { 
            if (Utils.marketAvailable(this)) {
                GCMRegistrar.checkDevice(getApplicationContext());
                GCMRegistrar.checkManifest(getApplicationContext());
                final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
                if (regId.length() != 0) {
                    if (cfg.upToDate()) {
                        Log.v("OTAUpdater::GCMRegister", "Already registered");
                    } else {
                        Log.v("OTAUpdater::GCMRegister", "Already registered, out-of-date, reregistering");
                        GCMRegistrar.unregister(getApplicationContext());
                        GCMRegistrar.register(getApplicationContext(), Config.GCM_SENDER_ID);
                        cfg.setValuesToCurrent();
                        Log.v("OTAUpdater::GCMRegister", "GCM registered");
                    }
                } else {
                    GCMRegistrar.register(getApplicationContext(), Config.GCM_SENDER_ID);
                    Log.v("OTAUpdater::GCMRegister", "GCM registered");
                }
            } else {
                UpdateCheckReceiver.setAlarm(getApplicationContext());
            }

            checkOnResume = true;
            addPreferencesFromResource(R.xml.main);
            
            Intent i = getIntent();
            if (i.getAction().equals(NOTIF_ACTION)) {
            	showUpdateDialog(RomInfo.fromIntent(i));
            }
    
            String romVersion = Utils.getOtaVersion();
            if (romVersion == null) romVersion = android.os.Build.ID;
            Date romDate = Utils.getOtaDate();
            if (romDate != null) {
                romVersion += " (" + DateFormat.getDateTimeInstance().format(romDate) + ")";
            }
            
            final Preference device = findPreference("device_view");
            device.setSummary(android.os.Build.DEVICE.toLowerCase());
            final Preference rom = findPreference("rom_view");
            rom.setSummary(android.os.Build.DISPLAY);
            final Preference version = findPreference("version_view");
            version.setSummary(romVersion);
            final Preference build = findPreference("otaid_view");
            build.setSummary(Utils.getRomID());
            
            availUpdatePref = findPreference("avail_updates");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        boolean connected = ni != null && ni.isConnected();
        if (!connected || ni.getType() == ConnectivityManager.TYPE_MOBILE && !ignoredDataWarn) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(connected ? R.string.alert_nowifi_title : R.string.alert_nodata_title);
            alert.setMessage(connected ? R.string.alert_nowifi_message : R.string.alert_nodata_message);
            alert.setPositiveButton(R.string.alert_wifi_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            alert.setNeutralButton(R.string.alert_ignore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ignoredDataWarn = true;
                }
            });
            alert.setNegativeButton(R.string.alert_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert.create().show();
        }

        if (checkOnResume) {
            checkForRomUpdates();
            checkOnResume = false;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == availUpdatePref) {
            if (!fetching) checkForRomUpdates();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
        case R.id.view:
            i = new Intent(this, ListFilesActivity.class);
            startActivity(i);
            break;
        case R.id.settings:
            i = new Intent(this, UpdaterSettings.class);
            startActivity(i);
            break;
        case R.id.exit:
            finish();
            break;
        case R.id.refresh:
            checkForRomUpdates();
            break;
        }
        return true;
    }

    private void checkForRomUpdates() {
        if (fetching) return;
        new FetchRomInfoTask(this, new RomInfoListener() {
            @Override
            public void onStartLoading() {
                fetching = true;
            }
            @Override
			public void onLoaded(RomInfo info) {
                fetching = false;
                String buildVersion = android.os.Build.ID;
                if (info == null) {
                    availUpdatePref.setSummary(getString(R.string.main_updates_error, "Unknown error"));
                	Toast.makeText(OTAUpdaterActivity.this, R.string.toast_fetch_error, Toast.LENGTH_SHORT).show();
                } else if (info.romName != null && info.romName.length() != 0 && !buildVersion.equals(info.romName)) {
                    showUpdateDialog(info);
                } else {
                    availUpdatePref.setSummary(R.string.main_updates_none);
                    Toast.makeText(OTAUpdaterActivity.this, R.string.toast_no_updates, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(String error) {
                fetching = false;
                availUpdatePref.setSummary(getString(R.string.main_updates_error, error));
                Toast.makeText(OTAUpdaterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void showUpdateDialog(final RomInfo info) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(OTAUpdaterActivity.this);
        alert.setTitle(R.string.alert_update_title);
        alert.setMessage(getString(R.string.alert_update_to, info.romName, info.version));
        availUpdatePref.setSummary(getString(R.string.main_updates_new, info.romName, info.version));

        alert.setPositiveButton(R.string.alert_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();

            	final File file = new File(Config.DL_PATH + info.romName + "_" + info.version + ".zip");
            	if (file.exists()) file.delete();

            	final ProgressDialog progressDialog = new ProgressDialog(OTAUpdaterActivity.this);
            	progressDialog.setTitle(R.string.alert_downloading);
            	progressDialog.setMessage("Changelog: " + info.changelog);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setProgress(0);
                
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                final WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, UpdateCheckReceiver.class.getName());
                wl.acquire();
                
                final AsyncTask<Void, Integer, Integer> dlTask = new AsyncTask<Void, Integer, Integer>() {
                    private int scale = 1048576;
                    
					@Override
					protected void onPreExecute() {
						progressDialog.show();
					}

					@Override
					protected Integer doInBackground(Void... params) {
					    InputStream is = null;
					    OutputStream os = null;
						try {
                            URL getUrl = new URL(info.url);
                            long startTime = System.currentTimeMillis();
                            Log.d("Download Manager", "download beginning: " + startTime);
                            Log.d("Download Manager", "download url: " + getUrl);
                            Log.d("Download Manager", "file name: " + file);

                            URLConnection conn = getUrl.openConnection();
                            final int lengthOfFile = conn.getContentLength();
                            if (lengthOfFile < 10000000) scale = 1024; //if less than 10 mb, scale using kb
                            publishProgress(0, lengthOfFile);

                            MessageDigest digest = MessageDigest.getInstance("MD5");
                            
                            conn.connect();
                            is = new BufferedInputStream(getUrl.openStream());
                            os = new FileOutputStream(file);

                            byte[] data = new byte[4096];
                            int nRead = -1;
                            int totalRead = 0;
                            while ((nRead = is.read(data)) != -1) {
                            	if (this.isCancelled()) break;
                                os.write(data, 0, nRead);
                                digest.update(data, 0, nRead);
                                totalRead += nRead;
                                publishProgress(totalRead);
                            }

                            if (isCancelled()) {
                            	file.delete();
                            	return 2;
                            }
                            
                            String dlMd5 = Utils.byteArrToStr(digest.digest());
                            Log.d("Download Manager", "downloaded md5: " + dlMd5);
                            if (!info.md5.equalsIgnoreCase(dlMd5)) {
                                file.delete();
                                return 1;
                            }

                            long finishTime = System.currentTimeMillis();
                            Log.d("Download Manager", "download finished: " + finishTime);
                            return 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                            file.delete();
                        } finally {
                            if (is != null) {
                                try { is.close(); }
                                catch (Exception e) { }
                            }
                            if (os != null) {
                                try { os.flush(); os.close(); }
                                catch (Exception e) { }
                            }
                        }
                        return -1;
					}

					@Override
                    protected void onCancelled(Integer result) {
					    progressDialog.dismiss();
					    wl.release();
					    wl.acquire(Config.WAKE_TIMEOUT);
					    
                        switch (result) {
                        case 0:
                            break;
                        case 1:
                            Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_md5_mismatch, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_interrupted, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_error, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
					protected void onPostExecute(Integer result) {
						progressDialog.dismiss();
						wl.release();
                        wl.acquire(Config.WAKE_TIMEOUT);
                        
						switch (result) {
						case 0:
						    ListFilesActivity.installFileDialog(OTAUpdaterActivity.this, file);
						    break;
						case 1:
						    Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_md5_mismatch, Toast.LENGTH_SHORT).show();
						    break;
						case 2:
						    Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_interrupted, Toast.LENGTH_SHORT).show();
						    break;
						default:
						    Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_error, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						if (values.length == 0) return;
                        progressDialog.setProgress(values[0] / scale);
                        if (values.length == 1) return;
                        progressDialog.setMax(values[1] / scale);
					}
				};
				dlTask.execute();
				
				progressDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        dlTask.cancel(true);
                    }
                });
            }
        });

        alert.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }
}