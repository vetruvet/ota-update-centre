package com.updater.ota;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

public class Utils {
    public static boolean marketAvailable(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.google.vending") ||
                packageInfo.packageName.equals("com.google.market")) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isROMSupported() {
        return System.getProperty(Config.OTA_ID_PROP) != null;
    }
    
    public static boolean dataAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo().isConnected();
    }
}
