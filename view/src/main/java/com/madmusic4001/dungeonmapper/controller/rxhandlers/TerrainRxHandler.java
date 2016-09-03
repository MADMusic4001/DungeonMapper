package com.madmusic4001.dungeonmapper.controller.rxhandlers;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Handles events requesting operations on {@link Terrain} instances with persistent storage.
 */
public class TerrainRxHandler {
    private TerrainDao terrainDao;

    /**
     * Creates a TerrainRxHandler instance with the given parameters.
     *
     * @param terrainDao  a {@link TerrainDao} instance
     */
    @Inject
    public TerrainRxHandler(TerrainDao terrainDao) {
        this.terrainDao = terrainDao;
    }

    /**
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param terrain  the {@link Terrain} instance to save to persistent storage
     * @return an Observable that, when subscribed to, will save the Terrain instance to persistent storage.
     */
    public Observable<Terrain> save(final Terrain terrain) {
        return Observable.create(
                new Observable.OnSubscribe<Terrain>() {
                    @Override
                    public void call(Subscriber<? super Terrain> subscriber) {
                        try {
                            terrainDao.save(terrain);
                            subscriber.onNext(terrain);
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
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param filters  a collection of {@link DaoFilter} instances that will be used to filter which Terrain instances will be deleted
     * @return an Observable that, when subscribed to, will delete Terrain instances from persistent storage.
     */
    public Observable<Collection<Terrain>> delete(final Collection<DaoFilter> filters) {
        return Observable.create(
                new Observable.OnSubscribe<Collection<Terrain>>() {
                    @Override
                    public void call(Subscriber<? super Collection<Terrain>> subscriber) {
                        try {
                            Collection<Terrain> deletedTerrains = terrainDao.load(filters);
                            terrainDao.delete(filters);
                            subscriber.onNext(deletedTerrains);
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
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param filters  a collection of {@link DaoFilter} instances that will be used to filter which Terrain instances will be loaded
     * @return an Observable that, when subscribed to, will load Terrain instances from persistent storage.
     */
    public Observable<Collection<Terrain>> load(final Collection<DaoFilter> filters) {
        return Observable.create(
                new Observable.OnSubscribe<Collection<Terrain>>() {
                    @Override
                    public void call(Subscriber<? super Collection<Terrain>> subscriber) {
                        try {
                            Collection<Terrain> terrains = terrainDao.load(filters);
                            subscriber.onNext(terrains);
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
