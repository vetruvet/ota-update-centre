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

package com.updater.ota;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.updater.ota.FetchRomInfoTask.RomInfoListener;

public class UpdateCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setAlarm(context);
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        wl.acquire();

        new FetchRomInfoTask(context, new RomInfoListener() {
            @Override
            public void onStartLoading() { }
            @Override
            public void onLoaded(RomInfo info) {
                boolean available = false;
                String buildVersion = android.os.Build.ID;
                if (info != null && info.mRom != null && !info.mRom.isEmpty() && !buildVersion.equals(info.mRom)) {
                    available = true;
                }

                if (available) {
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setContentIntent(contentIntent);
                    builder.setContentTitle(context.getString(R.string.notif_source));
                    builder.setContentText(context.getString(R.string.notif_text_rom));
                    builder.setTicker(context.getString(R.string.notif_text_rom));
                    builder.setWhen(System.currentTimeMillis());
                    builder.setSmallIcon(R.drawable.updates);
                    nm.notify(1, builder.getNotification());
                }

                wl.release();
            }
        }).execute();
	}

    protected static void setAlarm(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, UpdateCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.cancel(pendingIntent);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
