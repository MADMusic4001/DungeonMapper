package com.madmusic4001.dungeonmapper.controller.eventhandlers;

import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypePersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypePersistenceEventPosting;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypeSavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypesDeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.cellExitType.CellExitTypesLoadedEvent;
import com.madmusic4001.dungeonmapper.data.dao.CellExitTypeDao;
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
     * @param event  a {@link CellExitTypePersistenceEvent} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCellPersistenceEvent(CellExitTypePersistenceEvent event) {
        switch (event.getOperation()) {
            case SAVE:
                saveCell(event);
                break;
            case DELETE:
                deleteCells(event);
                break;
            case LOAD:
                loadCells(event);
                break;
        }
    }

    /**
     * Responds to requests to perform a persistent storage operation for a CellExitType instance or instances. The work will be
     * performed in the same thread as the poster.
     *
     * @param event  a {@link CellExitTypePersistenceEventPosting} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onCellPersistenceEvent(CellExitTypePersistenceEventPosting event) {
        switch (event.getOperation()) {
            case SAVE:
                saveCell(event);
                break;
            case DELETE:
                deleteCells(event);
                break;
            case LOAD:
                loadCells(event);
                break;
        }
    }

    private void saveCell(CellExitTypePersistenceEvent event) {
        eventBus.post(new CellExitTypeSavedEvent(cellExitTypeDao.save(event.getCellExitType()), event.getCellExitType()));
    }

    private void deleteCells(CellExitTypePersistenceEvent event) {
        Collection<CellExitType> cellExitTypesDeleted = cellExitTypeDao.load(event.getFilters());
        int deletedCount = cellExitTypeDao.delete(event.getFilters());
        eventBus.post(new CellExitTypesDeletedEvent(deletedCount >= 0, deletedCount, cellExitTypesDeleted));
    }

    private void loadCells(CellExitTypePersistenceEvent event) {
        Collection<CellExitType> cellExitTypes = null;
        boolean success = true;
        try {
            cellExitTypes = cellExitTypeDao.load(event.getFilters());
        }
        catch(DaoException ex) {
            Log.e("CellExitTypeEventHandle", ex.getMessage(), ex);
            success = false;
        }
        eventBus.post(new CellExitTypesLoadedEvent(success, cellExitTypes));
    }
}
