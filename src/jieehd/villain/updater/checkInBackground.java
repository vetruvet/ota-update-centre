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

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class checkInBackground extends BroadcastReceiver {
		
		public Context mContext;
	 	NotificationManager nm;
		HttpClient client;
		JSONObject json;
		JSONObject device;
		final static String URL = "http://dl.dropbox.com/u/44265003/update.json";
		boolean available = false;
		
	    public JSONObject getVersion() 
	    		throws ClientProtocolException, IOException, JSONException{
			HttpClient client = new DefaultHttpClient();
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
	    
	    public boolean haveNetworkConnection(Context mContext) {
	        boolean haveConnectedWifi = false;
	        boolean haveConnectedMobile = false;

	        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	        for (NetworkInfo ni : netInfo) {
	            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	                if (ni.isConnected())
	                    haveConnectedWifi = true;
	            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	                if (ni.isConnected())
	                    haveConnectedMobile = true;
	            		
	        }
	        return haveConnectedWifi || haveConnectedMobile;
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
	    
	 
	 
	 public class Read extends AsyncTask<String, Integer, Display> {
	    	

			@Override
			protected Display doInBackground(String... params) {
				String buildDevice = android.os.Build.DEVICE.toUpperCase();
				// TODO Auto-generated method stub
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
							String rom = "Could not retrieve device information";
							String changelog = "Could not retrieve information from server";
							String downurl = "Could not retrieve information from server";
							String build = "Could not retrieve information from server";
			    	    	return new Display(rom, changelog, downurl, build);

					}
							
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
				}
			
			
			@Override
			public void onPostExecute(final Display result) {
		        boolean haveConnectedWifi = false;
		        boolean haveConnectedMobile = false;

		        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		        for (NetworkInfo ni : netInfo) {
		            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
		                if (ni.isConnected())
		                    haveConnectedWifi = true;
		            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		                if (ni.isConnected())
		                    haveConnectedMobile = true;
		        }
					final String buildVersion = android.os.Build.ID;
					if (haveConnectedWifi == false && haveConnectedMobile == false)  {
						available = false;
					}else{
						if (buildVersion.equals(result.mRom)) {
							available = false;
						}else if (result.mRom != null && !result.mRom.isEmpty()) {
							available = true;
						}else if (result.mRom.equals("Could not retrieve device information")){
							available = false;
						}else if(buildVersion != (result.mRom)){
							available = false;
						}}
			;}
	 }
	 
	 public void notifyUser() {
		 	  final NotificationManager nm;
			  nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			  CharSequence from = "Villain Updater";
			  CharSequence message = "New Updates!";
			  PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
			  Notification notif = new Notification(R.drawable.updates, "New updates!", System.currentTimeMillis());
			  notif.setLatestEventInfo(mContext, from, message, contentIntent);
			  nm.notify(1, notif);
	 }
	 


	 @Override
	 public void onReceive(Context context, Intent intent) {
		 new Read().execute();
		 mContext = context;
	 }



	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
}

	
