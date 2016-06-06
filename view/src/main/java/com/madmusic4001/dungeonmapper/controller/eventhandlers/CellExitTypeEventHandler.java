package com.madmusic4001.dungeonmapper.controller.eventhandlers;

import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypeEvent;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypeEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles {@link CellExitType} related events.
 */
@Singleton
public class CellExitTypeEventHandler {
    private EventBus eventBus;
    private CellExitTypeDao cellExitTypeDao;

    /**
     * Creates a new CellExitTypeEventHandler instance.
     *
     * @param eventBus  a {@link EventBus} instance
     * @param cellExitTypeDao  a {@link CellExitTypeDao} instance
     */
    @Inject
    public CellExitTypeEventHandler(EventBus eventBus, CellExitTypeDao cellExitTypeDao) {
        this.eventBus = eventBus;
        this.cellExitTypeDao = cellExitTypeDao;
    }

    /**
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param event  a {@link CellExitTypeEvent} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSaveCellExitTypeEvent(CellExitTypeEvent.Save event) {
        saveCellExitType(event.getCellExitType());
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeleteCellExitTypesEvent(CellExitTypeEvent.Delete event) {
        deleteCellExitTypes(event.getFilters());
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLoadCellExitTypesEvent(CellExitTypeEvent.Load event) {
        loadCellExitTypes(event.getFilters());
    }

    /**
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed in the same thread as the poster.
     *
     * @param event  a {@link CellExitTypeEventPosting} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSaveCellExitTypeEvent(CellExitTypeEventPosting.Save event) {
        saveCellExitType(event.getCellExitType());
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeleteCellExitTypesEvent(CellExitTypeEventPosting.Delete event) {
        deleteCellExitTypes(event.getFilters());
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLoadCellExitTypesEvent(CellExitTypeEventPosting.Load event) {
        loadCellExitTypes(event.getFilters());
    }

    private void saveCellExitType(CellExitType cellExitType) {
        eventBus.post(new CellExitTypeEvent.Saved(cellExitTypeDao.save(cellExitType), cellExitType));
    }

    private void deleteCellExitTypes(Collection<DaoFilter> filters) {
        Collection<CellExitType> cellExitTypesDeleted = cellExitTypeDao.load(filters);
        int deletedCount = cellExitTypeDao.delete(filters);
        eventBus.post(new CellExitTypeEvent.Deleted(deletedCount >= 0, deletedCount, cellExitTypesDeleted));
    }

    private void loadCellExitTypes(Collection<DaoFilter> filters) {
        Collection<CellExitType> cellExitTypes = null;
        boolean success = true;
        try {
            cellExitTypes = cellExitTypeDao.load(filters);
        }
        catch(DaoException ex) {
            Log.e("CellExitTypeEventHandle", ex.getMessage(), ex);
            success = false;
        }
        eventBus.post(new CellExitTypeEvent.Loaded(success, cellExitTypes));
    }
}
