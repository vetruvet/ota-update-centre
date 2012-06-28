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

public class RomInfo {
    public String mRom;
    public String mChange;
    public String mUrl;
    public String mBuild;

    public RomInfo(String rom, String changelog, String downurl, String build) {
        mRom = rom;
        mChange = changelog;
        mUrl = downurl;
        mBuild = build;
    }
}