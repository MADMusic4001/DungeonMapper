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

import com.madmusic4001.dungeonmapper.data.dao.CellDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.Cell;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Handles {@link Cell} related events.
 */
@Singleton
public class CellRxHandler {
	private CellDao cellDao;

	/**
	 * Creates a new CellRxHandler instance.
	 *
	 * @param cellDao  a {@link CellDao} instance
	 */
	@Inject
	public CellRxHandler(CellDao cellDao) {
		this.cellDao = cellDao;
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a Cell instance or instances. The work will be
	 * performed a separate thread from the poster.
	 *
	 * @param cell  the {@link Cell} instance to be saved to persistent storage
	 * @return an {@link Observable} instance that, when subscribed to, will save the Cell instance to persistent storage.
	 */
	public Observable<Cell> save(final Cell cell) {
		return Observable.create(
				new Observable.OnSubscribe<Cell>() {
					@Override
					public void call(Subscriber<? super Cell> subscriber) {
						try {
							cellDao.save(cell);
							subscriber.onNext(cell);
							subscriber.onCompleted();
						}
						catch (Exception e) {
							subscriber.onError(e);
						}
					}
				}
		).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a Cell instance or instances. The work will be
	 * performed a separate thread from the poster.
	 *
	 * @param filters  a collection of {@link DaoFilter} instances that will be used to filter which Cell instances will be deleted.
	 * @return an {@link Observable} instance that, when subscribed to, will delete Cell instance from persistent storage.
	 */
	public Observable<Collection<Cell>> delete(final Collection<DaoFilter> filters) {
		return Observable.create(
				new Observable.OnSubscribe<Collection<Cell>>() {
					@Override
					public void call(Subscriber<? super Collection<Cell>> subscriber) {
						try {
							Collection<Cell> deletedCells = cellDao.load(filters);
							cellDao.delete(filters);
							subscriber.onNext(deletedCells);
							subscriber.onCompleted();
						}
						catch (Exception e) {
							subscriber.onError(e);
						}
					}
				}
		).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Responds to requests to perform a persistent storage operation for a Cell instance or instances. The work will be
	 * performed a separate thread from the poster.
	 *
	 * @param filters  a collection of {@link DaoFilter} instances that will be used to filter which Cell instances will be loaded.
	 * @return an {@link Observable} instance that, when subscribed to, will load Cell instance from persistent storage.
	 */
	public Observable<Collection<Cell>> load(final Collection<DaoFilter> filters) {
		return Observable.create(
				new Observable.OnSubscribe<Collection<Cell>>() {
					@Override
					public void call(Subscriber<? super Collection<Cell>> subscriber) {
						try {
							Collection<Cell> cells = cellDao.load(filters);
							subscriber.onNext(cells);
							subscriber.onCompleted();
						}
						catch (Exception e) {
							subscriber.onError(e);
						}
					}
				}
		).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}
}
