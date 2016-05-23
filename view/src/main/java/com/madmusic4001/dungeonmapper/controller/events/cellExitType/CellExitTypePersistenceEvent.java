package com.madmusic4001.dungeonmapper.controller.events.cellExitType;

import com.madmusic4001.dungeonmapper.data.dao.DaoFilter;
import com.madmusic4001.dungeonmapper.data.entity.CellExitType;

import java.util.Collection;

/**
 * Event representing a request to take some action on a CellExitType instance or a collection of CellExitType instances.
 */
public class CellExitTypePersistenceEvent {
    public enum Operation {
        SAVE,
        DELETE,
        LOAD
    }
    private Operation             operation;
    private CellExitType          cellExitType;
    private Collection<DaoFilter> filters;

    /**
     * Creates a CellExitTypePersistenceEvent instance.
     *
     * @param operation  the action being requested in the event
     * @param cellExitType  a CellExitType instance to act on
     * @param filters  filters to use to obtain CellExitType instances to act on
     */
    public CellExitTypePersistenceEvent(Operation operation, CellExitType cellExitType, Collection<DaoFilter> filters) {
        this.operation = operation;
        this.cellExitType = cellExitType;
        this.filters = filters;
    }

    // Getters
    public Operation getOperation() {
        return operation;
    }
    public CellExitType getCellExitType() {
        return cellExitType;
    }
    public Collection<DaoFilter> getFilters() {
        return filters;
    }
}
