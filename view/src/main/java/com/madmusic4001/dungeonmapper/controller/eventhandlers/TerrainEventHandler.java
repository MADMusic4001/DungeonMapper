package com.madmusic4001.dungeonmapper.controller.eventhandlers;

import android.util.Log;

import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainPersistenceEvent;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainPersistenceEventPosting;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainSavedEvent;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainsDeletedEvent;
import com.madmusic4001.dungeonmapper.controller.events.terrain.TerrainsLoadedEvent;
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
     * @param event  a {@link TerrainPersistenceEvent} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onWorldPersistenceEvent(TerrainPersistenceEvent event) {
        switch (event.getOperation()) {
            case SAVE:
                saveWorld(event);
                break;
            case DELETE:
                deleteWorld(event);
                break;
            case LOAD:
                loadWorlds(event);
                break;
        }
    }

    /**
     * Responds to requests to perform a persistent storage operation for a Terrain instance or instances. The work will be
     * performed in the same thread as the poster.
     *
     * @param event  a {@link TerrainPersistenceEventPosting} instance containing the information need to complete the request
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onWorldPersistenceEvent(TerrainPersistenceEventPosting event) {
        switch (event.getOperation()) {
            case SAVE:
                saveWorld(event);
                break;
            case DELETE:
                deleteWorld(event);
                break;
            case LOAD:
                loadWorlds(event);
                break;
        }
    }

    private void saveWorld(TerrainPersistenceEvent event) {
        eventBus.post(new TerrainSavedEvent(terrainDao.save(event.getTerrain()), event.getTerrain()));
    }

    private void deleteWorld(TerrainPersistenceEvent event) {
        Collection<Terrain> terrainsDeleted = terrainDao.load(event.getFilters());
        int deletedCount = terrainDao.delete(event.getFilters());
        eventBus.post(new TerrainsDeletedEvent(deletedCount >= 0, deletedCount, terrainsDeleted));
    }

    private void loadWorlds(TerrainPersistenceEvent event) {
        Collection<Terrain> terrains = null;
        boolean success = true;
        try {
            terrains = terrainDao.load(event.getFilters());
        }
        catch(DaoException ex) {
            Log.e("TerrainEventHandler", ex.getMessage(), ex);
            success = false;
        }
        eventBus.post(new TerrainsLoadedEvent(success, terrains));
    }
}
