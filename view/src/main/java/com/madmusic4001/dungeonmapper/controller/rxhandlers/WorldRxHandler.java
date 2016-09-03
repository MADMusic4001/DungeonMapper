package com.madmusic4001.dungeonmapper.controller.rxhandlers;

import android.util.Log;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Creates reactive observable for requesting operations on {@link World} instances with persistent storage.
 */
public class WorldRxHandler {
    private WorldDao dao;

    /**
     * Creates a new WorldHandler instance
     *
     * @param dao  a {@link WorldDao} instance to use to access persistent storage
     */
    public WorldRxHandler(WorldDao dao) {
        this.dao = dao;
    }

    /**
     * Creates an Observable that, when subscribed to, will query persistent storage for a World instance with the given id.
     *
     * @param id  the id of the world to retrieve from persistent storage
     * @return an {@link Observable} instance that can be subscribed to in order to retrieve a World instance.
     */
    public Observable<World> getWorld(final int id) {
        return Observable.create(
                new Observable.OnSubscribe<World>() {
                    @Override
                    public void call(Subscriber<? super World> subscriber) {
                        try {
                            subscriber.onNext(dao.load(id));
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
     * Creates an Observable that, when subscribed to, will query persistent storage for a collection of World instances that match the given
     * filters.
     *
     * @param filters  the filters to use when querying persistent storage
     * @return an {@link Observable} instance that can be subscribed to in order to retrieve a collection of World
     * instances.
     */
    public Observable<Collection<World>> getWorlds(final Collection<DaoFilter> filters) {
		Log.e("WorldHandler", "getting worlds");
        return Observable.create(
                new Observable.OnSubscribe<Collection<World>>() {
                    @Override
                    public void call(Subscriber<? super Collection<World>> subscriber) {
                        try {
                            subscriber.onNext(dao.load(filters));
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
     * Creates an Observable that, when subscribed to, will save a World instance to persistent storage.
     *
     * @param world  the world instance to be saved
     * @return an {@link Observable} instance that can be subscribed to in order to save the World instance.
     */
    public Observable<World> saveWorld(final World world) {
        return Observable.create(
                new Observable.OnSubscribe<World>() {
                    @Override
                    public void call(Subscriber<? super World> subscriber) {
                        try {
                            dao.save(world);
                            subscriber.onNext(world);
                            subscriber.onCompleted();
                        }
                        catch(Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Creates an Observable that, when subscribed to, will delete all World instances that match the given filters.
     *
     * @param filters  the filters to use when deleting the World instances from persistent storage
     * @return an {@link Observable} instance that can be subscribed to in order to delete the World instances.
     */
    public Observable<Collection<World>> deleteWorlds(final Collection<DaoFilter> filters) {
        return Observable.create(
                new Observable.OnSubscribe<Collection<World>>() {
                    @Override
                    public void call(Subscriber<? super Collection<World>> subscriber) {
                        try {
                            Collection<World> worldsDeleted = dao.load(filters);
                            dao.delete(filters);
                            subscriber.onNext(worldsDeleted);
                            subscriber.onCompleted();
                        }
                        catch(Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
