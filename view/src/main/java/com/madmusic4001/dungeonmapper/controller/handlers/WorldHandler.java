package com.madmusic4001.dungeonmapper.controller.handlers;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.WorldDao;
import com.madmusic4001.dungeonmapper.data.entity.World;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * Created by madanle on 7/21/16.
 */
public class WorldHandler {
    private WorldDao dao;

    /**
     * Creates a new WorldHandler instance
     *
     * @param dao  a {@link WorldDao} instance to be used for data access
     */
    public WorldHandler(WorldDao dao) {
        this.dao = dao;
    }

    /**
     * Creates an Observable that, when subscribed to, will query the database for a World instance with the given id.
     *
     * @param id  the id of the world to retrieve from the database
     * @return an {@link Observable<World>} instance that can be subscribed to in order to retrieve a World instance.
     */
    public Observable<World> getWorld(int id) {
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
        )
        .subscribeOn(Schedulers.io());
    }

    /**
     * Creates an Observable that, when subscribed to, will query the database for a collection of World instances that match the given
     * filters.
     *
     * @param filters  the filters to use when querying the database
     * @return an {@link Observable<Collection<World>>} instance that can be subscribed to in order to retrieve a collection of World
     * instances.
     */
    public Observable<Collection<World>> getWorlds(Collection<DaoFilter> filters) {
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
        )
        .subscribeOn(Schedulers.io());
    }

    /**
     * Creates an Observable that, when subscribed to, will save a World instance to the database.
     *
     * @param world  the world instance to be saved
     * @return an {@link Observable<Collection<World>>} instance that can be subscribed to in order to save the World instance.
     */
    public Observable<World> saveWorld(World world) {
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
        )
        .subscribeOn(Schedulers.io());
    }

    /**
     * Creates an Observable that, when subscribed to, will delete all World instances that match the given filters.
     *
     * @param filters  the filters to use when deleting the World instances from the database
     * @return an {@link Observable<Collection<World>>} instance that can be subscribed to in order to delete the World instances.
     */
    public Observable<Collection<World>> deleteWorlds(Collection<DaoFilter> filters) {
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
        )
        .subscribeOn(Schedulers.io());
    }
}
