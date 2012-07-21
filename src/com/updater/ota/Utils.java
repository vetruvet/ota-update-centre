package com.updater.ota;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;

public class Utils {
    private static String cachedRomID = null;
    
    public static boolean marketAvailable(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            pm.getPackageInfo("com.android.vending", 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isROMSupported() {
        String romID = getRomID();
        return romID != null && romID.length() != 0;
    }
    
    public static String getRomID() {
        if (cachedRomID == null) {
            ProcessBuilder pb = new ProcessBuilder("/system/bin/getprop", Config.OTA_ID_PROP);
            pb.redirectErrorStream(true);
            
            Process p = null;
            InputStream is = null;
            try {
                p = pb.start();
                is = p.getInputStream();
                String prop = new Scanner(is).next();
                if (prop.length() == 0) return null;
                cachedRomID = prop;
            } catch (NoSuchElementException e) {
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (p != null) p.destroy();
                if (is != null) {
                    try { is.close(); }
                    catch (Exception e) { }
                }
            }
        }
        return cachedRomID;
    }
    
    public static boolean dataAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo().isConnected();
    }
    
    private static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static String byteArrToStr(byte[] bytes) {
        StringBuffer str = new StringBuffer();
        for (int q = 0; q < bytes.length; q++) {
            str.append(HEX_DIGITS[(0xF0 & bytes[q]) >>> 4]);
            str.append(HEX_DIGITS[0xF & bytes[q]]);
        }
        return str.toString();
    }
}
