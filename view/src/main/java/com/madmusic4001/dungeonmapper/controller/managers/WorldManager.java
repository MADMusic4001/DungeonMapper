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

package com.madmusic4001.dungeonmapper.controller.managers;

import android.content.Context;
import android.util.Log;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.dao.impl.WorldFileDao;
import com.madmusic4001.dungeonmapper.data.entity.Cell;
import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;
import com.madmusic4001.dungeonmapper.data.util.DataConstants;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class WorldManager {
    private final Context		context;
    private final WorldDao		worldDao;
    private final RegionDao		regionDao;
	private final CellDao		cellDao;
	private final WorldFileDao	worldFileDao;
    private Map<String, World> worldNameMap;
	public enum SortBy {
		NAME,
		NAME_DESCENDING,
		CREATED_TS,
		CREATED_TS_DESCENDING,
		MODIFIED_TS,
		MODIFIED_TS_DESCENDING
	}
	private SortBy currentWorldSortBy  = SortBy.NAME;
	private SortBy currentRegionSortBy = SortBy.NAME;
	private static final Comparator<World> worldNameComparator = new Comparator<World>() {
		@Override
		public int compare(World lhs, World rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	};
	private static final Comparator<World> worldNameDescendingComparator = new Comparator<World>
			() {
		@Override
		public int compare(World lhs, World rhs) {
			return rhs.getName().compareTo(lhs.getName());
		}
	};
	private static final Comparator<World> worldCreateTsComparator = new Comparator<World>() {
		@Override
		public int compare(World lhs, World rhs) {
			return lhs.getCreateTs().compareTo(rhs.getCreateTs());
		}
	};
	private static final Comparator<World> worldCreateTsDescendingComparator = new
			Comparator<World>() {
		@Override
		public int compare(World lhs, World rhs) {
			return rhs.getCreateTs().compareTo(lhs.getCreateTs());
		}
	};
	private static final Comparator<World> worldModifiedTsComparator = new Comparator<World>() {
		@Override
		public int compare(World lhs, World rhs) {
			return lhs.getModifiedTs().compareTo(rhs.getModifiedTs());
		}
	};
	private static final Comparator<World> worldModifiedTsDescendingComparator = new
			Comparator<World>() {
		@Override
		public int compare(World lhs, World rhs) {
			return rhs.getModifiedTs().compareTo(lhs.getModifiedTs());
		}
	};
	private static final Comparator<Region> regionNameComparator = new Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}
	};
	private static final Comparator<Region> regionNameDescendingComparator = new
			Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return rhs.getName().compareTo(lhs.getName());
		}
	};
	private static final Comparator<Region> regionCreateTsComparator = new Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return lhs.getCreateTs().compareTo(rhs.getCreateTs());
		}
	};
	private static final Comparator<Region> regionCreateTsDescendingComparator = new
			Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return rhs.getCreateTs().compareTo(lhs.getCreateTs());
		}
	};
	private static final Comparator<Region> regionModifiedTsComparator = new Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return lhs.getModifiedTs().compareTo(rhs.getModifiedTs());
		}
	};
	private static final Comparator<Region> regionModifiedTsDescendingComparator = new
			Comparator<Region>() {
		@Override
		public int compare(Region lhs, Region rhs) {
			return rhs.getModifiedTs().compareTo(lhs.getModifiedTs());
		}
	};

	/**
	 /**
	 * Creates a new (@code WorldManager} instance. This should only be called by the Dagger
	 * framework.
	 *
	 * @param context  the application {@link android.content.Context} instance
	 * @param worldDao  a {@link WorldDao} implementation instance
	 * @param regionDao  a {@link RegionDao} implementation instance
	 * @param cellDao  a {@link CellDao} implentation instance
	 * @param worldFileDao  a {@link WorldFileDao} instance
	 */
	@Inject
	public WorldManager(final Context context, final WorldDao worldDao, final RegionDao regionDao,
						final CellDao cellDao, final WorldFileDao worldFileDao) {
		this.context = context;
		this.worldDao = worldDao;
		this.regionDao = regionDao;
		this.cellDao = cellDao;
		this.worldFileDao = worldFileDao;
	}

	/**
	 * Creates a new {@code World} instance with a unique name and add it to the {@code World}
	 * collection.
	 *
	 * @return the new {@link World} instance
	 */
	public World createNewWorld(String newWorldName) {
		World newWorld;
		int worldNum = 1;

		if (newWorldName == null) {
			newWorldName = context.getString(R.string.defaultWorldName);
		}

		while (getWorldNameMap().get(newWorldName) != null) {
			newWorldName = newWorldName + worldNum++;
		}
		newWorld = new World(newWorldName);
		newWorld.setCreateTs(Calendar.getInstance());
		newWorld.setModifiedTs(newWorld.getCreateTs());
		newWorld.setRegionWidth(16);
		newWorld.setRegionHeight(16);
		newWorld.setOriginLocation(DataConstants.SOUTHWEST);
		newWorld.setOriginOffset(0);
		worldDao.save(newWorld);
		getWorldNameMap().put(newWorldName, newWorld);
		return newWorld;
	}

	/**
	 * Create a new {@code Region} for the given {@code World} with a unique name and add it to the
	 * {@code Collection} of Region instances.
	 *
	 * @param world  the {@link World} to create the {@link Region} in.
	 * @param baseRegionName  the base name to use for the new {@link Region} instance.
	 * @return the new {@link Region} instance.
	 */
	public Region createNewRegion(World world, String baseRegionName) {
		int regionNum = 1;
		Region newRegion;

		if(baseRegionName == null) {
			baseRegionName = context.getString(R.string.defaultRegionName);
		}
		String regionName = baseRegionName;
		Map<String, Region> regionNameMap = world.getRegionNameMap();

		while (regionNameMap.get(regionName) != null) {
			regionName = baseRegionName + regionNum++;
		}

		newRegion = new Region(regionName, world);
		newRegion.setWidth(world.getRegionWidth());
		newRegion.setHeight(world.getRegionHeight());
		newRegion.setCreateTs(Calendar.getInstance());
		newRegion.setModifiedTs(newRegion.getCreateTs());
		regionDao.save(newRegion);
		regionNameMap.put(regionName, newRegion);
		return newRegion;
	}

	/**
	 * Deletes a {@code World} instance from persistent storage.
	 *
	 * @param world  the {@link World} instance to delete
	 */
	public Collection<World> deleteWorld(World world) {
		getWorldNameMap().remove(world.getName());
        worldDao.delete(world);
		world.setId(-1);
		return getWorldNameMap().values();
    }

	/**
	 * Deletes a {@code Region} instance from persistent storage.
	 *
	 * @param region  the {@link Region} instance to delete
	 */
	public Collection<Region> deleteRegion(Region region) {
		World world = region.getParent();
		world.getRegionNameMap().remove(region.getName());
		regionDao.delete(region);
		region.setId(-1);
		return world.getRegionNameMap().values();
	}

	/**
	 * Writes all {@code World} instance in the database to file storage.
	 *
	 * @return the name of the directory where the worlds were written.
	 */
	public String exportDatabase(){
		String fileName = null;
		for(World aWorld : getWorldNameMap().values()) {
			fileName = worldFileDao.saveWorldToFile(aWorld);
		}
		return fileName;
	}

	public boolean importDatabase(final String filePath, final boolean overwrite){
		worldFileDao.loadFromFile(this, filePath, overwrite);
		return true;
	}

    /*
	 * Gets a {@code World} instance from the worlds collection.
	 * 
	 * @param name the name of the desired {@code World}.
	 * @return the World instance with the specified id or null if not found.
     */
	public World getWorldWithName(String name) {
		return getWorldNameMap().get(name);
	}

    /**
     * Returns the list of already loaded {@link World} instances or loads them from persistent
     * storage if not already loaded or a refresh is requested by the forceReload parameter.
     *
     * @param forceReload true if the worldNameMap should be recreated from storage even if it
     *                    already exists.
     */
    @SuppressWarnings("unused")
    public Collection<World> getAllWorlds(boolean forceReload) {
        // If forceReload is true then set worldNameMap to null to force the getWorldNameMap
        // method to reload from persistent storage
		if(forceReload) {
			worldNameMap = null;
		}

		return getWorldNameMap().values();
    }

    /**
     * Load all of the {@link Region} instances for the given {@link World}.
     *
     * @param world the {@link World} for which the Regions need to be loaded.
	 * @return a {@link Collection} of {@link Region} instance.
     */
    public Collection<Region> getRegionsForWorld(World world) {
		Collection<Region> regions;

		// Only get from storage if maps are not already loaded
        if(world.getRegionNameMap().isEmpty()) {
			Region filter = new Region(null, world);
			regions = regionDao.loadWithFilter(filter);
        }
        else {
			regions = world.getRegionNameMap().values();
        }

		return regions;
    }

	/**
	 * Load the {@code Region} with the given region name for the {@code World} with the given
	 * world name.
	 *
	 * @param worldName  the name of the {@link World} with the Region to be loaded.
	 * @param regionName  the name of the {@link Region} to be loaded.
	 * @return a {@link Region} instance or null if not found.
	 */
	public Region getRegionForWorld(String worldName, String regionName) {
		Region region = null;

		World world = getWorldNameMap().get(worldName);
		if(world != null) {
			Map<String, Region> regionMap = world.getRegionNameMap();
			if(regionMap == null || regionMap.isEmpty()) {
				getRegionsForWorld(world);
				regionMap = world.getRegionNameMap();
			}
			if(regionName != null) {
				region = regionMap.get(regionName);
			}
			else if(regionMap != null && !regionMap.isEmpty()) {
				region = regionMap.values().toArray(new Region[regionMap.size()])[0];
			}
		}

		return region;
	}

	/**
     * Load all of the {@code MapCell} instances for a {@code Map} from persistent storage.
     *
     * @param region the {@link Region} for which the Cells are to be loaded.
	 * @return a {@link Collection} of {@link Cell} instances.
     */
    @SuppressWarnings("unused")
    public Collection<Cell> getCellsForRegion(Region region) {
        Collection<Cell> cells;

		if(region.getCells().isEmpty()) {
			Log.d(this.getClass().getName(), "Loading cells for region " + region.getName());
			Cell filterCell = new Cell();
			filterCell.setParent(region);
			cells = cellDao.loadWithFilter(filterCell);
			region.setCells((List<Cell>)cells);
		}
		else {
			cells = region.getCells();
		}

		return cells;
    }

    /**
     * Saves a {@link World} to persistent storage.
     *
     * @param world the {@link World} to be saved to persistent storage.
	 * @return the {@link World} that was to be saved.
     */
    public World saveWorld(World world) {
		worldDao.save(world);
		return world;
    }

    /**
     * Saves a {@link Region} to persistent storage
     *
     * @param region the {@link Region} to be saved.
	 * @return the {@link Region} that was saved.
     */
    public Region saveRegion(Region region, String oldName) {
		boolean newRegion = region.getId() < 0;
		regionDao.save(region);
		if(newRegion) {
			region.getParent().getRegionNameMap().put(region.getName(), region);
		}
		else if(!region.getName().equals(oldName)) {
			region.getParent().getRegionNameMap().put(region.getName(), region);
			region.getParent().getRegionNameMap().remove(oldName);
		}
		return region;
    }

    /**
     * Save a {@link Cell} to persistent storage.
     *
     * @param cell the {@link Cell} that needs to be saved.
     */
    @SuppressWarnings("unused")
    public Cell saveCell(Cell cell) {
		boolean newCell = cell.getId() <= 0;
		cellDao.save(cell);
		if(newCell) {
			cell.getParent().putCell(cell);
		}
		return cell;
    }

	/**
	 * Get a {@link Comparator<World>} that will sort {@link World} instances by {@link
	 * World#getName()}. If already sorted by {@link World#getName()} ascending then it will sort
	 * by {@link World#getName()} descending.
	 *
	 * @return a {@link Comparator<World>} that can be used to sort a collection of {@link World}
	 * instances.
	 */
	public Comparator<World> getWorldNameComparator() {
		Comparator<World> result;

		if(currentWorldSortBy == SortBy.NAME) {
			currentWorldSortBy = SortBy.NAME_DESCENDING;
			result = worldNameDescendingComparator;
		}
		else {
			currentWorldSortBy = SortBy.NAME;
			result = worldNameComparator;
		}

		return result;
	}

	/**
	 * Get a {@link Comparator<World>} that will sort {@link World} instances by {@link
	 * World#getCreateTs()}. If already sorted by {@link World#getCreateTs()} ascending then it
	 * will sort by {@link World#getCreateTs()} descending.
	 *
	 * @return a {@link Comparator<World>} that can be used to sort a collection of {@link World}
	 * instances.
	 */
	public Comparator<World> getWorldCreateTsComparator() {
		Comparator<World> result;

		if(currentWorldSortBy == SortBy.CREATED_TS) {
			currentWorldSortBy = SortBy.CREATED_TS_DESCENDING;
			result = worldCreateTsDescendingComparator;
		}
		else {
			currentWorldSortBy = SortBy.CREATED_TS;
			result = worldCreateTsComparator;
		}

		return result;
	}

	/**
	 * Get a {@link Comparator<World>} that will sort {@link World} instances by {@link
	 * World#getModifiedTs()}. If already sorted by {@link World#getModifiedTs()} ascending then
	 * it will sort by {@link World#getModifiedTs()} descending.
	 *
	 * @return a {@link Comparator<World>} that can be used to sort a collection of {@link World}
	 * instances.
	 */
	public Comparator<World> getWorldModifiedTsCompartor() {
		Comparator<World> result;

		if(currentWorldSortBy == SortBy.MODIFIED_TS) {
			currentWorldSortBy = SortBy.MODIFIED_TS_DESCENDING;
			result = worldModifiedTsDescendingComparator;
		}
		else {
			currentWorldSortBy = SortBy.MODIFIED_TS;
			result = worldModifiedTsComparator;
		}

		return result;
	}

	/**
	 * Get a {@link Comparator<Region>} that will sort {@link Region} instances by {@link
	 * Region#getName()}. If already sorted by {@link Region#getName()} ascending then it will sort
	 * by {@link Region#getName()} descending.
	 *
	 * @return a {@link Comparator<Region>} that can be used to sort a collection of {@link Region}
	 * instances.
	 */
	public Comparator<Region> getRegionNameComparator() {
		Comparator<Region> result;

		if(currentRegionSortBy == SortBy.NAME) {
			currentRegionSortBy = SortBy.NAME_DESCENDING;
			result = regionNameDescendingComparator;
		}
		else {
			currentRegionSortBy = SortBy.NAME;
			result = regionNameComparator;
		}

		return result;
	}

	/**
	 * Get a {@link Comparator<Region>} that will sort {@link Region} instances by {@link
	 * Region#getCreateTs()}. If already sorted by {@link Region#getCreateTs()} ascending then it
	 * will sort by {@link Region#getCreateTs()} descending.
	 *
	 * @return a {@link Comparator<Region>} that can be used to sort a collection of {@link Region}
	 * instances.
	 */
	public Comparator<Region> getRegionCreateTsComparator() {
		Comparator<Region> result;

		if(currentRegionSortBy == SortBy.CREATED_TS) {
			currentRegionSortBy = SortBy.CREATED_TS_DESCENDING;
			result = regionCreateTsDescendingComparator;
		}
		else {
			currentRegionSortBy = SortBy.CREATED_TS;
			result = regionCreateTsComparator;
		}

		return result;
	}

	/**
	 * Get a {@link Comparator<Region>} that will sort {@link Region} instances by {@link
	 * Region#getModifiedTs()}. If already sorted by {@link Region#getModifiedTs()} ascending then
	 * it will sort by {@link Region#getModifiedTs()} descending.
	 *
	 * @return a {@link Comparator<Region>} that can be used to sort a collection of {@link Region}
	 * instances.
	 */
	public Comparator<Region> getRegionModifiedTsCompartor() {
		Comparator<Region> result;

		if(currentRegionSortBy == SortBy.MODIFIED_TS) {
			currentRegionSortBy = SortBy.MODIFIED_TS_DESCENDING;
			result = regionModifiedTsDescendingComparator;
		}
		else {
			currentRegionSortBy = SortBy.MODIFIED_TS;
			result = regionModifiedTsComparator;
		}

		return result;
	}

	private Map<String, World> getWorldNameMap() {
		if(worldNameMap == null) {
			Collection<World> worlds = worldDao.loadAll();
			worldNameMap = new HashMap<>();
			for(World aWorld : worlds) {
				worldNameMap.put(aWorld.getName(), aWorld);
			}
		}
		return worldNameMap;
	}
}
