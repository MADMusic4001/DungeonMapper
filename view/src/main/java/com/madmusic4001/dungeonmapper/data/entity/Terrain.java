/**
 * Copyright (C) 2014 MadMusic4001
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.madmusic4001.dungeonmapper.data.entity;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class Terrain {
	private int id = -1;
	private String name = "New terrain";
	private boolean	userCreated;
    private int appResourceId;
	private Map<String, String> localeDisplayNames = new HashMap<>();
	private Bitmap image;
	private boolean solid;
	private boolean connect;

	/**
	 * Constructs a new Terrain object.
	 */
	public Terrain(@NonNull String name) {
		super();
        this.name = name;
	}

    /**
     * Get the display name for the given locale string.
     *
     * @param localeName the name of the locale (same as locale.toString())
     * @return the display name for the given locale string or null if it does not exist.
     */
    public String getDisplayNameForLocaleName(@NonNull String localeName) {
        return localeDisplayNames.get(localeName);
    }

    /**
     * Gets the display name for the default locale.
     *
     * @return the display name for the Terrain in the default language and country,
     * the default language or in US English if the display name does not exist in the default
     * locale.
     */
    public String getDisplayName() {
        return getDisplayName(Locale.getDefault());
    }
	/*
	 * 	Gets the display name for the desired locale.
     *
	 * @param locale the desired Locale to use for the display name.
	 * @return the display name for the Terrain in the desired language and country,
     * the desired language or in US English if the display name does not exist in the desired
     * language.
	 */
	public String getDisplayName(@NonNull Locale locale) {
        String displayName = localeDisplayNames.get(locale.toString());
        if(displayName == null) {
            displayName = localeDisplayNames.get(new Locale(locale.getLanguage()).toString());
            if(displayName == null) {
                displayName = localeDisplayNames.get(Locale.US.toString());
                if(displayName == null) {
                    displayName = localeDisplayNames.get(Locale.ENGLISH.toString());
                    if(displayName == null) {
                        displayName = getName();
                    }
                }
            }
        }
        return displayName;
	}

    /**
     * Add a name for the terrain that can be displayed to the user in the language for the given
     * locale
     *
     * @param localeName the string name for a Locale instance.
     * @param displayName the terrain name for the specified localName.
     */
	public void addDisplayName(String localeName, String displayName) {
		localeDisplayNames.put(localeName, displayName);
	}

    public Map<String, String> getLocaleDisplayNames() {
        return localeDisplayNames;
    }
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isUserCreated() {
		return userCreated;
	}
	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}
    public int getAppResourceId() {
        return appResourceId;
    }
    public void setAppResourceId(int appResourceId) {
        this.appResourceId = appResourceId;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
	public boolean isSolid() {
		return solid;
	}
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	public boolean canConnect() {
		return connect;
	}
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
}
