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

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class deleteFiles extends PreferenceActivity {
	private Context mContext;
	
	public void checkPrefs() {
	    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
	    Boolean clean = sp.getBoolean("clean_up", false);
	    
	    if (clean == true) {
	    	delete();
	    }else{
	    	
	    }
	}
	private void delete() {
	
	final long MAXFILEAGE = 8035200000L; // 3 month in milliseconds
	File dir = new File("/VillainROM/ROMs/");{
	File[] files = dir.listFiles();
	

	for (File f : files ) {
	   final Long lastmodified = f.lastModified();
	   if(lastmodified + MAXFILEAGE<System.currentTimeMillis()) {
	      f.delete();
	   }
	}
}

}}

