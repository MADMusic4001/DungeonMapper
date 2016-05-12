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

import android.support.annotation.NonNull;

import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class World {
	public static final int MAX_REGION_WIDTH  = 32;
	public static final int MAX_REGION_HEIGHT = 32;

	private int id = -1;
	private String 				name;
	private int                 originOffset   = 0;
	@DataConstants.OriginLocation
	private int                 originLocation = DataConstants.SOUTHWEST;
	private int                 regionWidth    = 16;
	private int                 regionHeight   = 16;
	private Calendar            createTs       = Calendar.getInstance();
	private Calendar            modifiedTs     = Calendar.getInstance();
	private Map<String, Region> regionNameMap  = new HashMap<>();

	/**
	 * Creates a new instance with the give name.
	 */
	public World(@NonNull String name) {
		this.name = name;
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
		if (name == null) {
			throw new IllegalArgumentException("World.name cannot be null");
		}
		this.name = name;
	}

	public int getOriginOffset() {
		return originOffset;
	}
	public void setOriginOffset(int originOffset) {
		this.originOffset = originOffset;
	}
	public @DataConstants.OriginLocation int getOriginLocation() {
		return originLocation;
	}
	public void setOriginLocation(@DataConstants.OriginLocation int originLocation) {
		this.originLocation = originLocation;
	}
	public int getRegionWidth() {
		return regionWidth;
	}
	public void setRegionWidth(int regionWidth) {
		if (regionWidth <= 0 ) {
			throw new IllegalArgumentException("regionWidth must be > 0");
		}
		if(regionWidth > MAX_REGION_WIDTH) {
			throw new IllegalArgumentException("regionWidth must be <= " + MAX_REGION_WIDTH);
		}
		this.regionWidth = regionWidth;
	}
	public int getRegionHeight() {
		return regionHeight;
	}
	public void setRegionHeight(int regionHeight) {
		if (regionHeight <= 0 ) {
			throw new IllegalArgumentException("regionHeight must be > 0");
		}
		if(regionHeight > MAX_REGION_HEIGHT) {
			throw new IllegalArgumentException("regionHeight must be <= " + MAX_REGION_HEIGHT);
		}
		this.regionHeight = regionHeight;
	}
	public Calendar getCreateTs() {
		return createTs;
	}
	public void setCreateTs(Calendar createTs) {
		this.createTs = createTs;
	}
	public Calendar getModifiedTs() {
		return modifiedTs;
	}
	public void setModifiedTs(Calendar modifiedTs) {
		this.modifiedTs = modifiedTs;
	}
	public Map<String, Region> getRegionNameMap() {
		return regionNameMap;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		World world = (World) o;

		if (id != world.id) {
			return false;
		}
		if (originOffset != world.originOffset) {
			return false;
		}
		if (originLocation != world.originLocation) {
			return false;
		}
		if (regionWidth != world.regionWidth) {
			return false;
		}
		if (regionHeight != world.regionHeight) {
			return false;
		}
		if (!name.equals(world.name)) {
			return false;
		}
		if (!createTs.equals(world.createTs)) {
			return false;
		}
		if (!modifiedTs.equals(world.modifiedTs)) {
			return false;
		}
		return regionNameMap.equals(world.regionNameMap);

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + name.hashCode();
		result = 31 * result + originOffset;
		result = 31 * result + originLocation;
		result = 31 * result + regionWidth;
		result = 31 * result + regionHeight;
		result = 31 * result + createTs.hashCode();
		result = 31 * result + modifiedTs.hashCode();
		result = 31 * result + regionNameMap.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("modifiedTs", modifiedTs)
				.append("id", id)
				.append("name", name)
				.append("originOffset", originOffset)
				.append("originLocation", originLocation)
				.append("regionWidth", regionWidth)
				.append("regionHeight", regionHeight)
				.append("createTs", createTs)
				.toString();
	}
}
