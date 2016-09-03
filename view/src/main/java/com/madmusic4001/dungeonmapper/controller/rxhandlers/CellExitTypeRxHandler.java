package com.madmusic4001.dungeonmapper.controller.rxhandlers;

import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Handles {@link CellExitType} related events.
 */
@Singleton
public class CellExitTypeRxHandler {
    private CellExitTypeDao cellExitTypeDao;

    /**
     * Creates a new CellExitTypeRxHandler instance.
     *
     * @param cellExitTypeDao  a {@link CellExitTypeDao} instance
     */
    @Inject
    public CellExitTypeRxHandler(CellExitTypeDao cellExitTypeDao) {
        this.cellExitTypeDao = cellExitTypeDao;
    }

    /**
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param cellExitType  the CellExitType instance to be saved.
     */
    public Observable<CellExitType> save(final CellExitType cellExitType) {
        return Observable.create(
                new Observable.OnSubscribe<CellExitType>() {
                    @Override
                    public void call(Subscriber<? super CellExitType> subscriber) {
                        try {
                            cellExitTypeDao.save(cellExitType);
                            subscriber.onNext(cellExitType);
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
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed in a separate thread from the poster.
     *
     * @return returns an Observable that, when subscribed to, will delete CellExitType instances from persistent storage.
     */
    public Observable<Collection<CellExitType>> deleteCellExitTypes(final Collection<DaoFilter> filters) {
        return Observable.create(
                new Observable.OnSubscribe<Collection<CellExitType>>() {
                    @Override
                    public void call(Subscriber<? super Collection<CellExitType>> subscriber) {
                        try {
                            Collection<CellExitType> cellExitTypes = cellExitTypeDao.load(filters);
                            cellExitTypeDao.delete(filters);
                            subscriber.onNext(cellExitTypes);
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
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed in a separate thread from the poster.
     *
     * @return returns an Observable that, when subscribed to, will load all CellExitType instances from persistent storage.
     */
    public Observable<Collection<CellExitType>> get(final Collection<DaoFilter> filters) {
        return Observable.create(
                new Observable.OnSubscribe<Collection<CellExitType>>() {
                    @Override
                    public void call(Subscriber<? super Collection<CellExitType>> subscriber) {
                        try {
                            subscriber.onNext(cellExitTypeDao.load(filters));
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
