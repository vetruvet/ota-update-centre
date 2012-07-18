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

import android.content.Intent;

public class RomInfo {
    public String mRom;
    public String mChange;
    public String mUrl;
    public String mBuild;
    public String md5;

    public RomInfo(String rom, String changelog, String downurl, String build, String md5) {
        mRom = rom;
        mChange = changelog;
        mUrl = downurl;
        mBuild = build;
        this.md5 = md5;
    }

    public static RomInfo fromIntent(Intent i) {
    	return new RomInfo(
    			i.getStringExtra("info_rom"),
    			i.getStringExtra("info_changelog"),
    			i.getStringExtra("info_url"),
    			i.getStringExtra("info_build"),
    			i.getStringExtra("info_md5"));
    }

    public void addToIntent(Intent i) {
    	i.putExtra("info_rom", mRom);
    	i.putExtra("info_changelog", mChange);
    	i.putExtra("info_url", mUrl);
    	i.putExtra("info_build", mBuild);
    	i.putExtra("info_md5", md5);
    }
}