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

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;


public class FetchRomInfoTask extends AsyncTask<Void, Void, RomInfo> {
    public static final String ROM_UPDATE_URL = "http://sensation-devs.org/romupdater2/pages/romupdate.php";

    private RomInfoListener callback = null;
    private Context context = null;

    public FetchRomInfoTask(Context ctx) {
    	this(ctx, null);
    }

    public FetchRomInfoTask(Context ctx, RomInfoListener callback) {
    	this.context = ctx;
    	this.callback = callback;
    }

    @Override
    public void onPreExecute() {
        if (callback != null) callback.onStartLoading();
    }

    @Override
    protected RomInfo doInBackground(Void... notused) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!cm.getActiveNetworkInfo().isConnected()) return null;

        try {
            String device = android.os.Build.DEVICE.toLowerCase();
            String romID = System.getProperty("otaupdater.otaid");

            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("device", device));
            params.add(new BasicNameValuePair("rom", romID));

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(ROM_UPDATE_URL + "?" + URLEncodedUtils.format(params, "UTF-8"));
            HttpResponse r = client.execute(get);
            int status = r.getStatusLine().getStatusCode();
            HttpEntity e = r.getEntity();
            if (status == 200) {
                String data = EntityUtils.toString(e);
                JSONObject json = new JSONObject(data);

                return new RomInfo(json.getString("version"), json.getString("changelog"), json.getString("url"), json.getString("rom"));
            } else {
                if (e != null) e.consumeContent();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(RomInfo result) {
        if (callback != null) callback.onLoaded(result);
    }

    public static interface RomInfoListener {
        void onStartLoading();
        void onLoaded(RomInfo info);
    }
}