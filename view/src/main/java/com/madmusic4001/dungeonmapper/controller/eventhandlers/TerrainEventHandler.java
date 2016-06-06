package com.madmusic4001.dungeonmapper.controller.eventhandlers;

import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainEvent;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainEventPosting;
import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.dao.TerrainDao;
import com.madmusic4001.dungeonmapper.data.entity.Terrain;
import com.madmusic4001.dungeonmapper.data.exceptions.DaoException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;

/**
 * Handles events requesting operations on {@link Terrain} instances with persistent storage.
 */
public class TerrainEventHandler {
    private EventBus eventBus;
    private TerrainDao terrainDao;

    /**
     * Creates a TerrainEventHandler instance with the given parameters.
     *
     * @param eventBus  a {@link EventBus} instance
     * @param terrainDao  a {@link TerrainDao} instance
     */
    public TerrainEventHandler(EventBus eventBus, TerrainDao terrainDao) {
        this.eventBus = eventBus;
        this.terrainDao = terrainDao;
    }

    /**
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed a separate thread from the poster.
     *
     * @param event  a {@link TerrainEvent} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSaveTerrainEvent(TerrainEvent.Save event) {
        saveTerrain(event.getTerrain());
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDeleteTerrainsEvent(TerrainEvent.Delete event) {
        deleteTerrains(event.getFilters());
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLoadTerrainsEvent(TerrainEvent.Load event) {
        loadTerrains(event.getFilters());
    }

    /**
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed in the same thread as the poster.
     *
     * @param event  a {@link TerrainEventPosting} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onSaveTerrainEvent(TerrainEventPosting.Save event) {
        saveTerrain(event.getTerrain());
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDeleteTerrainsEvent(TerrainEventPosting.Delete event) {
        deleteTerrains(event.getFilters());
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onLoadTerrainsEvent(TerrainEventPosting.Load event) {
        loadTerrains(event.getFilters());
    }

    private void saveTerrain(Terrain terrain) {
        eventBus.post(new TerrainEvent.Saved(terrainDao.save(terrain), terrain));
    }

    private void deleteTerrains(Collection<DaoFilter> filters) {
        Collection<Terrain> terrainsDeleted = terrainDao.load(filters);
        int deletedCount = terrainDao.delete(filters);
        eventBus.post(new TerrainEvent.Deleted(deletedCount >= 0, deletedCount, terrainsDeleted));
    }

    private void loadTerrains(Collection<DaoFilter> filters) {
        Collection<Terrain> terrains = null;
        boolean success = true;
        try {
            terrains = terrainDao.load(filters);
        }
        catch(DaoException ex) {
            Log.e("TerrainEventHandler", ex.getMessage(), ex);
            success = false;
        }
        eventBus.post(new TerrainEvent.Loaded(success, terrains));
    }
}
