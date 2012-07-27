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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.updater.ota.FetchRomInfoTask.RomInfoListener;

public class UpdateCheckReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
	    final Config cfg = Config.getInstance(context.getApplicationContext());
	    
	    if (cfg.hasStoredUpdate()) {
	        RomInfo info = cfg.getStoredUpdate();
	        if (Utils.isUpdate(info)) {
	            Utils.showUpdateNotif(context, info);
	        } else {
	            cfg.clearStoredUpdate();
	        }
	    }
	    
	    if (Utils.isROMSupported()) {
	        if (Utils.marketAvailable(context)) {
        	    GCMRegistrar.checkDevice(context.getApplicationContext());
                GCMRegistrar.checkManifest(context.getApplicationContext());
                final String regId = GCMRegistrar.getRegistrationId(context.getApplicationContext());
                if (regId.equals("")) {
                    GCMRegistrar.register(context.getApplicationContext(), Config.GCM_SENDER_ID);
                    Log.v("OTAUpdater::GCM", "GCM registered");
                } else {
                    Log.v("OTAUpdater::GCM", "Already registered");
                }
	        } else {
	            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
	                setAlarm(context);
	            }

	            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	            final WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UpdateCheckReceiver.class.getName());
	            wl.acquire();

	            new FetchRomInfoTask(context, new RomInfoListener() {
	                @Override
	                public void onStartLoading() { }
	                @Override
	                public void onLoaded(RomInfo info) {
	                    if (Utils.isUpdate(info)) {
	                        cfg.storeUpdate(info);
	                        Utils.showUpdateNotif(context, info);
	                    } else {
                            cfg.clearStoredUpdate();
	                    }

	                    wl.release();
	                }
	                @Override
	                public void onError(String error) {
	                    wl.release();
	                }
	            }).execute();
	        }
	    } else {
	        Log.w("OTAUpdater", "Unsupported ROM");
	    }
	}

    protected static void setAlarm(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, UpdateCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.cancel(pendingIntent);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
