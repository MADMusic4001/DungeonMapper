package com.madmusic4001.dungeonmapper.controller.events;

/**
 * Event signifying a request to import the database from a file.
 */
public class ImportDatabaseEvent {
    private String filename;
    private boolean overwrite;

    /**
     * Creates a new ImportDatabaseEvent instance.
     *
     * @param filename  the name of the file containing the data to import
     * @param overwrite  true if the current database content should be overwritten if there is a conflict, otherwise false.
     */
    public ImportDatabaseEvent(String filename, boolean overwrite) {
        this.filename = filename;
        this.overwrite = overwrite;
    }

    // Getters
    public String getFilename() {
        return filename;
    }
    public boolean isOverwrite() {
        return overwrite;
    }
}
