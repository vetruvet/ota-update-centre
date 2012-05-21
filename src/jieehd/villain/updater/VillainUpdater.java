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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
//import jieehd.villain.updater.checkInBackground.Display;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
//import android.widget.TabHost;




public class VillainUpdater extends PreferenceActivity {
	
	Button btnDown;
	HttpClient client;
	JSONObject json;
	JSONObject device;
	String rom;
	JSONObject device_id;
	
	
	
	
	 
	 File sdDir = (Environment.getExternalStorageDirectory());
 	 public String PATH = sdDir + "/VillainROM/ROMs/VillainROM";
	 protected static final String Display = null;
	 
		
		//initialize progress bar
		
	    private ProgressDialog mProgressDialog;
	    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	    public static String URL = null;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);
        
        client = new DefaultHttpClient();
        setRepeatingAlarm();
        haveNetworkConnection();
        
        String buildDevice = android.os.Build.DEVICE.toUpperCase();
        String buildVer = android.os.Build.ID;
        String buildRom = android.os.Build.DISPLAY.toUpperCase();
        String buildPrint = android.os.Build.FINGERPRINT.toUpperCase();
 //       final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        final Preference device = (Preference) findPreference("device_view");
        device.setSummary(buildDevice);
        final Preference Rom = (Preference) findPreference("rom_view");
        Rom.setSummary(buildRom);        
        final Preference Version = (Preference) findPreference("version_view");
        Version.setSummary(buildVer);
        final Preference build = (Preference) findPreference("build_view");
        build.setSummary(buildPrint);
       
        
    }
    
    public void checkRomType() {

    }
    
    @SuppressWarnings("unused")
	public void requestRoot() {
    	try {
			Process root = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
	 public void setRepeatingAlarm() {
		  AlarmManager am;
		  am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		  Intent intent = new Intent(this, checkInBackground.class);
		  PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (3 * AlarmManager.INTERVAL_DAY), pendingIntent);
		 }
    
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
      	
        }
        if (haveConnectedWifi == false && haveConnectedMobile == false) {
        	Log.d("Network State", "false");
    	    AlertDialog.Builder alert = new AlertDialog.Builder(VillainUpdater.this);                 
    	    alert.setTitle("No Data Connection!");  
    	    alert.setMessage("You have no data connection, click ok to turn on WiFi or Mobile Data in order to check for OTA updates.");   
    	        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
                        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        intent.setComponent(cn);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity( intent);
					}
    	        	
    	        });
    	        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

    	            public void onClick(DialogInterface dialog, int which) {
    	                return;   
    	            }
    	        });
    	        alert.show();
        	
        }else{
        	download();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    
    public void download() {
    	new Read().execute();
    }
    
    
    public void getPrefs() {
        boolean info_show_check;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        info_show_check = prefs.getBoolean("info_show", true);
        
        if (info_show_check == true) {
        	showInfo();
        }else{

        }
    }
    
    
    private void showInfo() {
     
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.view:			Intent intent = new Intent();
									intent.setClass(getApplicationContext(), getfiles.class);
									startActivity(intent);
        						break;
            case R.id.settings:     Intent settings = new Intent();
            						settings.setClass(getApplicationContext(), villainsettings.class);
            						Intent settingsActivity = new Intent(getBaseContext(), villainsettings.class);
            						startActivity(settingsActivity);
                                break;
            case R.id.exit: 		finish();
            						System.exit(0);
                                break;
            case R.id.refresh: 		new Read().execute();
            				    break;
        }
        return true;
    }
    
    
    
    public JSONObject getVersion() 
    		throws ClientProtocolException, IOException, JSONException{
    	
    	String romType = android.os.Build.DISPLAY;
    	
    	if (romType.contains("VillainROM")) {
    		URL = "http://dl.dropbox.com/u/44265003/update.json";
    	}else if (romType.contains("VillainMOD")) {
    		URL = "http://dl.dropbox.com/u/44265003/update.json";
    	}else{
    		URL = "http://dl.dropbox.com/u/44265003/update.json";
    	}
    	
    	StringBuilder url = new StringBuilder(URL);
    	HttpGet get = new HttpGet(url.toString());
    	HttpResponse r = client.execute(get);
    	int status = r.getStatusLine().getStatusCode();
    	if (status == 200){
    		HttpEntity e = r.getEntity();
    		String data = EntityUtils.toString(e);
    		JSONObject stream = new JSONObject(data);
    		JSONObject rom = stream.getJSONObject("villain-roms");
    		return rom;
    	}else{
    		return null;
    	}
    }
    
    public class Display {
    	public String mRom;
    	public String mChange;
    	public String mUrl;
    	public String mBuild;
    	
    	public Display (String rom, String changelog, String downurl, String build) {
    		mRom = rom;
    		mChange = changelog;
    		mUrl = downurl;
    		mBuild = build;
    	
    	
    	}
    }
    
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading File...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setProgress(0);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
        }
    }

    	
    public class Read extends AsyncTask<String, Integer, Display> {
    	

		@Override
		protected Display doInBackground(String... params) {
			String buildDevice = android.os.Build.DEVICE.toUpperCase();
			try {
				if (buildDevice.equals("PYRAMID")) {
				json = getVersion();
				String rom = json.getJSONObject("device").getJSONArray("PYRAMID").getJSONObject(0).getString("version");
				String changelog = json.getJSONObject("device").getJSONArray("PYRAMID").getJSONObject(0).getString("changelog");
				String downurl = json.getJSONObject("device").getJSONArray("PYRAMID").getJSONObject(0).getString("url");
				String build = json.getJSONObject("device").getJSONArray("PYRAMID").getJSONObject(0).getString("rom");
				return new Display(rom, changelog, downurl, build);
				} else if (buildDevice.equals("MAGURO")) {
					json = getVersion();
					String rom = json.getJSONObject("device").getJSONArray("MAGURO").getJSONObject(0).getString("version");
					String changelog = json.getJSONObject("device").getJSONArray("MAGURO").getJSONObject(0).getString("changelog");
					String downurl = json.getJSONObject("device").getJSONArray("MAGURO").getJSONObject(0).getString("url");
					String build = json.getJSONObject("device").getJSONArray("MAGURO").getJSONObject(0).getString("rom");
					return new Display(rom, changelog, downurl, build);
				} else if (buildDevice.equals("I9100")) {
					json = getVersion();
					String rom = json.getJSONObject("device").getJSONArray("I9100").getJSONObject(0).getString("version");
					String changelog = json.getJSONObject("device").getJSONArray("I9100").getJSONObject(0).getString("changelog");
					String downurl = json.getJSONObject("device").getJSONArray("I9100").getJSONObject(0).getString("url");
					String build = json.getJSONObject("device").getJSONArray("I9100").getJSONObject(0).getString("rom");
					return new Display(rom, changelog, downurl, build);
				} else if (buildDevice.equals("PASSION")) {
					json = getVersion();
					String rom = json.getJSONObject("device").getJSONArray("PASSION").getJSONObject(0).getString("version");
					String changelog = json.getJSONObject("device").getJSONArray("PASSION").getJSONObject(0).getString("changelog");
					String downurl = json.getJSONObject("device").getJSONArray("PASSION").getJSONObject(0).getString("url");
					String build = json.getJSONObject("device").getJSONArray("PASSION").getJSONObject(0).getString("rom");
					return new Display(rom, changelog, downurl, build);
				} else if (buildDevice.equals("GENERIC")) {
					json = getVersion();
					String rom = json.getJSONObject("device").getJSONArray("GENERIC").getJSONObject(0).getString("version");
					String changelog = json.getJSONObject("device").getJSONArray("GENERIC").getJSONObject(0).getString("changelog");
					String downurl = json.getJSONObject("device").getJSONArray("GENERIC").getJSONObject(0).getString("url");
					String build = json.getJSONObject("device").getJSONArray("GENERIC").getJSONObject(0).getString("rom");
					return new Display(rom, changelog, downurl, build);
				}else if (buildDevice.equals("GT-N7000")) {
					json = getVersion();
					String rom = json.getJSONObject("device").getJSONArray("GT-N7000").getJSONObject(0).getString("version");
					String changelog = json.getJSONObject("device").getJSONArray("GT-N7000").getJSONObject(0).getString("changelog");
					String downurl = json.getJSONObject("device").getJSONArray("GT-N7000").getJSONObject(0).getString("url");
					String build = json.getJSONObject("device").getJSONArray("GT-N7000").getJSONObject(0).getString("rom");
					return new Display(rom, changelog, downurl, build);
				} else if (buildDevice.equals(null)) {
					String rom = "Could not retrieve device information";
					String changelog = "Could not retrieve information from server";
					String downurl = "Could not retrieve information from server";
					String build = "Could not retrieve information from server";
					return new Display(rom, changelog, downurl, build);
				}else {
					Looper.prepare();
		    	    AlertDialog.Builder alert = new AlertDialog.Builder(VillainUpdater.this);                 
		    	    alert.setTitle("Couldn't locate device");  
		    	    alert.setMessage("Could not locate your device on our servers, this usually means it is unsupported or we haven't added it to our list of OTA upgradeable devices.");   
		    	    
		    	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								finish();
							}
		    	        	
		    	        });
						String rom = "Could not retrieve device information";
						String changelog = "Could not retrieve information from server";
						String downurl = "Could not retrieve information from server";
						String build = "Could not retrieve information from server";
		    	        alert.show();
		    	    	return new Display(rom, changelog, downurl, build);

				}
						
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
			}
		
		@Override
		public void onPostExecute(final Display result) {
			
		    
		    
        	boolean file = new File(sdDir + "/VillainROM/ROMs").exists(); {
        	if (file == true) {
        		
        	}else if (file == false){
        		new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/VillainROM/ROMs").mkdirs();
        	}}
        	
        	boolean file2 = new File(sdDir + "/VillainROM/Tweaks").exists(); {
        	if (file2 == true) {
        		
        	}else if (file2 == false){
        		new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/VillainROM/Tweaks").mkdirs();
        	}}
        	

					String buildVersion = android.os.Build.ID;
					if (buildVersion.equals(result.mRom)) {
				        final Preference build = (Preference) findPreference("avail_updates");
				        build.setSummary("No new updates");
					}else if(result.mRom.equals("Could not retrieve device information")){
				        final Preference build = (Preference) findPreference("avail_updates");
				        build.setSummary("Unknown Device");
					}else if(buildVersion !=(result.mRom)){
    	        	    AlertDialog.Builder alert = new AlertDialog.Builder(VillainUpdater.this);                 
    	        	    alert.setTitle("New updates available!");  
    	        	    alert.setMessage("Changelog: " + result.mChange);
				        final Preference build = (Preference) findPreference("avail_updates");
				        build.setSummary("New updates: " + result.mRom);
				        
				        
    	        	        alert.setPositiveButton("Download", new DialogInterface.OnClickListener() {  
    	        	        public void onClick(DialogInterface dialog, int whichButton) {  
    								showDialog(DIALOG_DOWNLOAD_PROGRESS);
    								new Thread(new Runnable() {

    								public void run(){
    									try {
    										URL getUrl = new URL (result.mUrl);
    										final File file = new File(PATH + "_" + result.mBuild + "_"  + result.mRom + ".zip");
    										long startTime = System.currentTimeMillis();
    										Log.d("Download Manager", "download beginning: " + startTime);
    										Log.d("Download Manager", "download url: " + getUrl);
    										Log.d("Download Manager", "file name: " + file);
    																	
    										
    										URLConnection ucon = getUrl.openConnection();
    										final int lengthOfFile = ucon.getContentLength();
    										ucon.connect();
    										InputStream input = new BufferedInputStream(getUrl.openStream());
    										OutputStream output = new FileOutputStream(PATH + "_" + result.mBuild + "_" + result.mRom + ".zip");
    										
    										
    										byte data[] = new byte[1024];
    																		
    										int current;
    										long total = 0;
    										while ((current = input.read(data)) != -1) {
    								       		output.write(data, 0, current);
    											total += current;
    											updateProgress(total, lengthOfFile);
    											}
    										mProgressDialog.dismiss();
    										output.flush();
    										output.close();
    										input.close();
    										
    									
    										
    																											
    										long finishTime = System.currentTimeMillis();
    										Log.d("Download Manager", "download finished: " + finishTime);
    									}catch (ClientProtocolException e) {
    									e.printStackTrace();
    									} catch (IOException e) {
    									e.printStackTrace();
    									}
    								    }
    								
    								public void updateProgress(final long total, final int lengthOfFile) {
    									runOnUiThread(new Runnable() {

    										public void run() {
    									    mProgressDialog.setMax(lengthOfFile);
    									    mProgressDialog.setProgress((int) total);

    									}});
    									}
    								
    							    	
    								}).start();
    	        	            return; 
    	        	           }  
    	        	         });  

    	        	        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

    	        	            public void onClick(DialogInterface dialog, int which) {
    	        	                return;   
    	        	            }
    	        	        });
    	        	        alert.show();
						
						

						

					}
		;}

		}
		
}

