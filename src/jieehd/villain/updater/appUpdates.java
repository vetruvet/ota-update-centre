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

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class appUpdates extends AsyncTask<Object, Object, Object>{
    final static String URL = "http://dl.dropbox.com/u/44265003/update.json";

    private Context mContext;
    private HttpClient client;
    private JSONObject json;

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI") && ni.isConnected())   haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE") && ni.isConnected()) haveConnectedMobile = true;
        }

        if (!haveConnectedWifi && !haveConnectedMobile) {
            Log.d("Network State", "false");
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected Object doInBackground(Object... params) {
        // TODO Auto-generated method stub
        return null;
    }
}
