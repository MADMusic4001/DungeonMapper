package com.madmusic4001.dungeonmapper.controller.events;

/**
 * Event signifying a request to import the database from a file.
 */
public class ImportExportEvent {
    public static class ImportDatabase {
        private String filename;
        private boolean overwrite;

        /**
         * Creates a new ImportExportEvent.ImportDatabase instance.
         *
         * @param filename  the name of the file containing the data to import
         * @param overwrite  true if the current database content should be overwritten if there is a conflict, otherwise false.
         */
        public ImportDatabase(String filename, boolean overwrite) {
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

	/**
	 * Event signifying a request to export the current database contents to a file.
	 */
	public static class ExportDatabase {
		String filename;

		/**
		 * Creates a new ImportExportEvent.ExportDatabase instance.
		 *
		 * @param filename  the name of the file to write the database to
		 */
		public ExportDatabase(String filename) {
			this.filename = filename;
		}

		// Getters
		public String getFilename() {
			return filename;
		}
	}

	/**
	 * Event to notify subscribers of the results of a database export operation.
	 */
	public static class DatabaseExported {
		private boolean successful;
		private String fileName;
		private int worldCount;
		private int regionCount;
		private int cellCount;
		private int cellExitTypeCount;
		private int terrainCount;

		/**
		 * Create a new ImportExportEvent.DatabaseExported instance with the given parameters.
		 *
		 * @param successful  true if the export succeeded, otherwise false
		 * @param fileName  the name of the file the database was written to
		 * @param worldCount  the number of worlds that were exported
		 * @param regionCount  the number of regions that were exported
		 * @param cellCount  the number of cells that were exported
		 * @param cellExitTypeCount  the number of cell exit types that were exported
		 * @param terrainCount  the number of terrains that were exported
		 */
		public DatabaseExported(boolean successful, String fileName, int worldCount, int regionCount, int cellCount, int cellExitTypeCount,
									 int terrainCount) {
			this.successful = successful;
			this.fileName = fileName;
			this.worldCount = worldCount;
			this.regionCount = regionCount;
			this.cellCount = cellCount;
			this.cellExitTypeCount = cellExitTypeCount;
			this.terrainCount = terrainCount;
		}

		// Getters
		public boolean isSuccessful() {
			return successful;
		}
		public String getFileName() {
			return fileName;
		}
		public int getWorldCount() {
			return worldCount;
		}
		public int getRegionCount() {
			return regionCount;
		}
		public int getCellCount() {
			return cellCount;
		}
		public int getCellExitTypeCount() {
			return cellExitTypeCount;
		}
		public int getTerrainCount() {
			return terrainCount;
		}
	}

	/**
	 * Event to notify subscribers of the results of a database import operation.
	 */
	public static class DatabaseImported {
	}
}
