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
    public String romName;
    public String version;
    public String changelog;
    public String url;
    public String md5;

    public RomInfo(String romName, String version, String changelog, String downurl, String md5) {
        this.romName = romName;
        this.version = version;
        this.changelog = changelog;
        this.url = downurl;
        this.md5 = md5;
    }

    public static RomInfo fromIntent(Intent i) {
    	return new RomInfo(
    			i.getStringExtra("info_rom"),
    			i.getStringExtra("info_version"),
    			i.getStringExtra("info_changelog"),
    			i.getStringExtra("info_url"),
    			i.getStringExtra("info_md5"));
    }

    public void addToIntent(Intent i) {
    	i.putExtra("info_rom", romName);
    	i.putExtra("info_version", version);
    	i.putExtra("info_changelog", changelog);
    	i.putExtra("info_url", url);
    	i.putExtra("info_md5", md5);
    }
}