package com.madmusic4001.dungeonmapper.controller.events;

/**
 * Event signifying a request to export the current database contents to a file.
 */
public class ExportDatabaseEvent {
    String filename;

    /**
     * Creates a new ExportDatabaseEvent instance.
     *
     * @param filename  the name of the file to write the database to
     */
    public ExportDatabaseEvent(String filename) {
        this.filename = filename;
    }

    // Getters
    public String getFilename() {
        return filename;
    }
}
