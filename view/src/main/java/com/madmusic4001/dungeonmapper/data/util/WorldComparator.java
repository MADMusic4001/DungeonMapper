package com.madmusic4001.dungeonmapper.data.util;

import com.madmusic4001.dungeonmapper.data.entity.Region;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Comparator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by madanle on 5/19/16.
 */
@Singleton
public class WorldComparator {
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
     */
	@Inject
	public WorldComparator() {
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
}
