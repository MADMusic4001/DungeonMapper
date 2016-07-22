/**
 * Copyright (C) 2016 MadInnovations
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madmusic4001.dungeonmapper.controller.rxhandlers;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.RegionDao;
import com.madmusic4001.dungeonmapper.data.entity.Region;

import java.util.Collection;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Creates reactive observable for requesting operations on {@link Region} instances with persistent storage.
 */
public class RegionRxHandler {
	private RegionDao dao;

	/**
	 * Creates a new RegionRxHandler instance
	 *
	 * @param dao  a {@link RegionDao} instance to use to access persistent storage
	 */
	public RegionRxHandler(RegionDao dao) {
		this.dao = dao;
	}

	/**
	 * Creates an Observable that, when subscribed to, will query persistent storage for a Region instance with the given id.
	 *
	 * @param id  the id of the region to retrieve from persistent storage
	 * @return an {@link Observable} instance that can be subscribed to in order to retrieve a Region instance.
	 */
	public Observable<Region> getRegion(final int id) {
		return Observable.create(
				new Observable.OnSubscribe<Region>() {
					@Override
					public void call(Subscriber<? super Region> subscriber) {
						try {
							subscriber.onNext(dao.load(id));
							subscriber.onCompleted();
						}
						catch (Exception e) {
							subscriber.onError(e);
						}
					}
				}
		)
		.subscribeOn(Schedulers.io());
	}

	/**
	 * Creates an Observable that, when subscribed to, will query persistent storage for a collection of Region instances that match the given
	 * filters.
	 *
	 * @param filters  the filters to use when querying persistent storage
	 * @return an {@link Observable} instance that can be subscribed to in order to retrieve a collection of Region
	 * instances.
	 */
	public Observable<Collection<Region>> getRegions(final Collection<DaoFilter> filters) {
		return Observable.create(
				new Observable.OnSubscribe<Collection<Region>>() {
					@Override
					public void call(Subscriber<? super Collection<Region>> subscriber) {
						try {
							subscriber.onNext(dao.load(filters));
							subscriber.onCompleted();
						}
						catch (Exception e) {
							subscriber.onError(e);
						}
					}
				}
		)
				.subscribeOn(Schedulers.io());
	}

	/**
	 * Creates an Observable that, when subscribed to, will save a Region instance to persistent storage.
	 *
	 * @param region  the region instance to be saved
	 * @return an {@link Observable} instance that can be subscribed to in order to save the Region instance.
	 */
	public Observable<Region> saveRegion(final Region region) {
		return Observable.create(
				new Observable.OnSubscribe<Region>() {
					@Override
					public void call(Subscriber<? super Region> subscriber) {
						try {
							dao.save(region);
							subscriber.onNext(region);
							subscriber.onCompleted();
						}
						catch(Exception e) {
							subscriber.onError(e);
						}
					}
				}
		)
				.subscribeOn(Schedulers.io());
	}

	/**
	 * Creates an Observable that, when subscribed to, will delete all Region instances that match the given filters.
	 *
	 * @param filters  the filters to use when deleting the Region instances from persistent storage
	 * @return an {@link Observable} instance that can be subscribed to in order to delete the Region instances.
	 */
	public Observable<Collection<Region>> deleteRegions(final Collection<DaoFilter> filters) {
		return Observable.create(
				new Observable.OnSubscribe<Collection<Region>>() {
					@Override
					public void call(Subscriber<? super Collection<Region>> subscriber) {
						try {
							Collection<Region> regionsDeleted = dao.load(filters);
							dao.delete(filters);
							subscriber.onNext(regionsDeleted);
							subscriber.onCompleted();
						}
						catch(Exception e) {
							subscriber.onError(e);
						}
					}
				}
		)
				.subscribeOn(Schedulers.io());
	}
}
