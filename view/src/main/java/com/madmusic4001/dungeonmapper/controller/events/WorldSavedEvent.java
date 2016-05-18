package com.madmusic4001.dungeonmapper.controller.events;

import com.madmusic4001.dungeonmapper.data.entity.World;

/**
 * Event to notify when a World has been saved to persistent storage.
 */
public class WorldSavedEvent {
    private boolean successful;
    private World world;

    /**
     * Creates a new WorldSavedEvent instance.
     *
     * @param successful  true is the world was sucessfully saved, otherwise false
     * @param world  the World instance that was saved
     */
    public WorldSavedEvent(boolean successful, World world) {
        this.successful = successful;
        this.world = world;
    }

    // Getters
    public boolean isSuccessful() {
        return successful;
    }
    public World getWorld() {
        return world;
    }
}
