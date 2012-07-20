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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
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

    private boolean checkOnResume = false;
    private Config cfg;

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
                checkOnResume = true;
            }

            addPreferencesFromResource(R.xml.main);
            
            Intent i = getIntent();
            if (i.getAction().equals(NOTIF_ACTION)) {
            	showUpdateDialog(RomInfo.fromIntent(i));
            }
    
            if (Config.getInstance(getApplicationContext()).getPruneFiles()) {
                pruneFiles();
            }
    
            String buildDevice = android.os.Build.DEVICE.toLowerCase();
            String buildVer = android.os.Build.ID;
            String buildRom = android.os.Build.DISPLAY;
            String buildPrint = android.os.Build.FINGERPRINT;
    
            final Preference device = findPreference("device_view");
            device.setSummary(buildDevice);
            final Preference rom = findPreference("rom_view");
            rom.setSummary(buildRom);
            final Preference version = findPreference("version_view");
            version.setSummary(buildVer);
            final Preference build = findPreference("build_view");
            build.setSummary(buildPrint);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (!ni.isConnected()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.alert_nodata_title);
            alert.setMessage(R.string.alert_nodata_message);
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
        new FetchRomInfoTask(this, new RomInfoListener() {
            @Override
            public void onStartLoading() { }
            @Override
			public void onLoaded(RomInfo info) {
                String buildVersion = android.os.Build.ID;
                if (info == null) {
                	Toast.makeText(OTAUpdaterActivity.this, R.string.toast_fetch_error, Toast.LENGTH_SHORT).show();
                } else if (info.mRom != null && info.mRom.length() != 0 && !buildVersion.equals(info.mRom)) {
                    showUpdateDialog(info);
                } else {
                    Toast.makeText(OTAUpdaterActivity.this, R.string.toast_no_updates, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(String error) {
                Toast.makeText(OTAUpdaterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @SuppressWarnings("deprecation")
    private void showUpdateDialog(final RomInfo info) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(OTAUpdaterActivity.this);
        alert.setTitle(R.string.alert_update_title);
        //TODO redo this...
        alert.setMessage("Changelog: " + info.mChange);
        final Preference build = findPreference("avail_updates");
        build.setSummary("New updates: " + info.mRom);

        alert.setPositiveButton(R.string.alert_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.dismiss();

            	final File file = new File(Config.DL_PATH + "_" + info.mBuild + "_"  + info.mRom + ".zip");
            	if (file.exists()) file.delete();

            	final ProgressDialog progressDialog = new ProgressDialog(OTAUpdaterActivity.this);
            	progressDialog.setTitle(R.string.alert_downloading);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(true);
                progressDialog.setProgress(0);

                new AsyncTask<Void, Integer, Boolean>() {
                    private int scale = 1048576;
                    
					@Override
					protected void onPreExecute() {
						progressDialog.show();
					}

					@Override
					protected Boolean doInBackground(Void... params) {
						try {
                            URL getUrl = new URL(info.mUrl);
                            long startTime = System.currentTimeMillis();
                            Log.d("Download Manager", "download beginning: " + startTime);
                            Log.d("Download Manager", "download url: " + getUrl);
                            Log.d("Download Manager", "file name: " + file);

                            URLConnection conn = getUrl.openConnection();
                            final int lengthOfFile = conn.getContentLength();
                            if (lengthOfFile < 10000000) scale = 1024; //if less than 10 mb, scale using kb
                            publishProgress(0, lengthOfFile);

                            conn.connect();
                            InputStream is = new BufferedInputStream(getUrl.openStream());
                            OutputStream os = new FileOutputStream(file);

                            byte[] data = new byte[4096];
                            int nRead = -1;
                            int totalRead = 0;
                            while ((nRead = is.read(data)) != -1) {
                            	if (this.isCancelled()) break;
                                os.write(data, 0, nRead);
                                totalRead += nRead;
                                publishProgress(totalRead);
                            }

                            os.flush();
                            os.close();
                            is.close();

                            if (isCancelled()) {
                            	file.delete();
                            	return false;
                            }

                            long finishTime = System.currentTimeMillis();
                            Log.d("Download Manager", "download finished: " + finishTime);
                            return true;
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                            file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                            file.delete();
                        }
                        return false;
					}

					@Override
					protected void onPostExecute(Boolean result) {
						progressDialog.dismiss();
						if (!result) {
							Toast.makeText(OTAUpdaterActivity.this, R.string.toast_download_error, Toast.LENGTH_SHORT).show();
						} else {
							ListFilesActivity.installFileDialog(OTAUpdaterActivity.this, file);
						}
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						if (values.length == 0) return;
                        progressDialog.setProgress(values[0] / scale);
                        if (values.length == 1) return;
                        progressDialog.setMax(values[1] / scale);
					}
				}.execute();
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

    private void pruneFiles() {
        final long MAXFILEAGE = 2592000000L; // 1 month in milliseconds
        File dir = new File(Config.DL_PATH);
        File[] files = dir.listFiles();

        boolean success = true;
        for (File f : files) {
            final Long lastmodified = f.lastModified();
            if (lastmodified + MAXFILEAGE < System.currentTimeMillis()) {
                if (!f.delete()) success = false;
            }
        }

        if (success) {
            Toast.makeText(getApplicationContext(), R.string.toast_prune, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_prune_error, Toast.LENGTH_SHORT).show();
        }
    }
}