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

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class Config {
    public static final String GCM_SENDER_ID = "1068482628480";
    public static final String GCM_REGISTER_URL = "https://www.otaupdatecenter.pro/pages/regdevice.php";
    public static final String PULL_URL = "https://www.otaupdatecenter.pro/pages/romupdate.php";
    public static final String OTA_ID_PROP = "otaupdater.otaid";
    public static final String OTA_VER_PROP = "otaupdater.otaver";
    public static final String OTA_DATE_PROP = "otaupdater.otatime";
    
    public static final int WAKE_TIMEOUT = 30000;
    
    public static final String DL_PATH = Environment.getExternalStorageDirectory() + "/OTA-Updater/download/";
    public static final File DL_PATH_FILE = new File(Config.DL_PATH);
    static {
        DL_PATH_FILE.mkdirs();
    }
    
    private int lastVersion = -1;
    private String lastDevice = null;
    private String lastRomID = null;
    
    private int curVersion = -1;
    private String curDevice = null;
    private String curRomID = null;
    
    private static final String PREFS_NAME = "prefs";
    private final SharedPreferences PREFS;
    
    private Config(Context ctx) {
        PREFS = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        
        lastVersion = PREFS.getInt("version", lastVersion);
        lastDevice = PREFS.getString("device", lastDevice);
        lastRomID = PREFS.getString("romid", lastRomID);
        
        try {
            curVersion = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
        }
        curDevice = android.os.Build.DEVICE.toLowerCase();
        curRomID = Utils.getRomID();
    }
    private static Config instance = null;
    public static synchronized Config getInstance(Context ctx) {
        if (instance == null) instance = new Config(ctx);
        return instance;
    }
    
    public int getLastVersion() {
        return lastVersion;
    }
    
    public String getLastDevice() {
        return lastDevice;
    }
    
    public String getLastRomID() {
        return lastRomID;
    }
    
    public void setValuesToCurrent() {
        synchronized (PREFS) {
            SharedPreferences.Editor editor = PREFS.edit();
            editor.putInt("version", curVersion);
            editor.putString("device", curDevice);
            editor.putString("romid", curRomID);
            editor.commit();
        }
    }
    
    public boolean upToDate() {
        if (lastDevice == null) return false;
        if (lastRomID == null) return false;
        if (curRomID == null) return false;
        return curVersion == lastVersion && curDevice.equals(lastDevice) && curRomID.equals(lastRomID);
    }
}
