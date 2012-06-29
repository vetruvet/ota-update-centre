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

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	protected GCMIntentService() {
		super("1068482628480");
	}

	@Override
	protected void onError(Context ctx, String errorID) {
		Log.e("OTA::GCM", errorID);
	}

	@Override
	protected void onMessage(Context ctx, Intent payload) {
		RomInfo info = RomInfo.fromIntent(payload);

		Intent i = new Intent(getApplicationContext(), OTAUpdaterActivity.class);
		i.setAction(OTAUpdaterActivity.NOTIF_ACTION);
		info.addToIntent(i);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, i, 0);

        Notification.Builder builder = new Notification.Builder(ctx);
        builder.setContentIntent(contentIntent);
        builder.setContentTitle(ctx.getString(R.string.notif_source));
        builder.setContentText(ctx.getString(R.string.notif_text_rom));
        builder.setTicker(ctx.getString(R.string.notif_text_rom));
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.updates);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.getNotification());
	}

	@Override
	protected void onRegistered(Context ctx, String regID) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("do", "register"));
		params.add(new BasicNameValuePair("reg_id", regID));
		params.add(new BasicNameValuePair("device", android.os.Build.DEVICE.toLowerCase()));
		params.add(new BasicNameValuePair("rom_id", System.getProperty("otaupdater.otaid")));

		try {
			HttpClient http = new DefaultHttpClient();
			HttpPost req = new HttpPost("http://sensation-devs.org/romupdater2/pages/regdevice.php");
			req.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse resp = http.execute(req);
			if (resp.getStatusLine().getStatusCode() != 200) {
				Log.w("OTA::GCM", "registration response non-200");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onUnregistered(Context ctx, String regID) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("do", "unregister"));
		params.add(new BasicNameValuePair("reg_id", regID));

		try {
			HttpClient http = new DefaultHttpClient();
			HttpPost req = new HttpPost("http://sensation-devs.org/romupdater2/pages/regdevice.php");
			req.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse resp = http.execute(req);
			if (resp.getStatusLine().getStatusCode() != 200) {
				Log.w("OTA::GCM", "unregistration response non-200");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
