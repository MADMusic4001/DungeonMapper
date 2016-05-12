/**
 * Copyright (C) 2014 MadMusic4001
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.madmusic4001.dungeonmapper.controller.managers;

import android.graphics.Bitmap;

import com.madmusic4001.dungeonmapper.data.dao.CellExitDao;
import com.madmusic4001.dungeonmapper.data.entity.CellExit;
import com.madmusic4001.dungeonmapper.data.exceptions.DataException;
import com.madmusic4001.dungeonmapper.data.util.BitmapPacker;
import static com.madmusic4001.dungeonmapper.data.util.DataConstants.*;
import com.madmusic4001.dungeonmapper.data.util.PackedBitmap;

import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Keeps track of all available {@link CellExit}  instances
 */
public class CellExitManager {
    private static final int          BITMAP_SIZE  = 144;
    private static final int          PADDING_SIZE = 6;
    private              PackedBitmap packedBitmap = null;
    private              BitmapPacker packer       = null;
    private final CellExitDao dao;
    private java.util.Map<String, CellExit> cellExitNameMap = new HashMap<>();

    public Collection<CellExit> getCellExits() {
        Collection<CellExit> cellExits = cellExitNameMap.values();
        if(cellExits.isEmpty()) {
            cellExits = dao.loadAll();
            for(CellExit aCellExit : cellExits) {
                cellExitNameMap.put(aCellExit.getName(), aCellExit);
            }
        }

        return cellExits;
    }

    /**
     * Creates a new {@code CellExitManager}.
     *
     * @param dao a {@code CellExitDao} implementation instance.
     */
    @Inject
    protected CellExitManager(final CellExitDao dao) {
        this.dao = dao;
    }

    /**
     * Gets the {@code CellExit} instance with the specified ID.
     *
     * @param cellExitId the id of the desired {@code CellExit}.
     * @return a {@code CellExit} instance or null if not found.
     */
    public CellExit getCellExitWithId(long cellExitId) throws DataException {
        CellExit cellExit = null;
        for (CellExit aCellExit : cellExitNameMap.values()) {
            if (aCellExit.getId() == cellExitId) {
                cellExit = aCellExit;
                break;
            }
        }
        return cellExit;
    }

    /**
     * Loads all {@link CellExit} instances.
     *
     * @return a {@link Collection} of {@link CellExit} instances.
     */
    public Collection<CellExit> loadCellExits() {
        Collection<CellExit> cellExits = cellExitNameMap.values();
        if (cellExits.isEmpty()) {
            cellExits = dao.loadAll();
            for(CellExit anExit : cellExits) {
                cellExitNameMap.put(anExit.getName(),anExit);
            }
        }
        return cellExits;
    }

    private BitmapPacker createPacker() {
        // Estimate atlas size based on number of cell exits using device dpi bitmap sizes
        int numExits = getCellExits().size()*2;
        int width = (int)Math.ceil(Math.sqrt(numExits));
        // Calculate how many pixels would be needed to put all bitmaps in row
        // then take the square root to get the size to make it a square texture atlas
        int rowLength = width * (BITMAP_SIZE + PADDING_SIZE * 2 + 2);
        // Make the row length as a power of 2
        rowLength--;
        rowLength |= rowLength >> 1;
        rowLength |= rowLength >> 2;
        rowLength |= rowLength >> 4;
        rowLength |= rowLength >> 8;
        rowLength |= rowLength >> 16;
        rowLength++;
        return new BitmapPacker(rowLength, rowLength, PADDING_SIZE, true);
    }

    private void packBitmaps(BitmapPacker packer) {
    Bitmap bitmap;

        for(CellExit exit : cellExitNameMap.values()) {
            bitmap = exit.getBitmapForDirection(UP);
            packer.addImage(exit.getName() + "_up", bitmap);

            bitmap = exit.getBitmapForDirection(NORTH);
            packer.addImage(exit.getName() + "_north", bitmap);

            bitmap = exit.getBitmapForDirection(WEST);
            packer.addImage(exit.getName() + "_west", bitmap);

            bitmap = exit.getBitmapForDirection(EAST);
            packer.addImage(exit.getName() + "_east", bitmap);

            bitmap = exit.getBitmapForDirection(SOUTH);
            packer.addImage(exit.getName() + "_south", bitmap);

            bitmap = exit.getBitmapForDirection(DOWN);
            packer.addImage(exit.getName() + "_down", bitmap);
        }
        packer.pack();
    }

    public BitmapPacker getBitmapPacker() {
        if(packer == null) {
            packer = createPacker();
            packBitmaps(packer);
        }
        return packer;
    }

    public void recyclePackedBitmap() {
        if(packedBitmap != null) {
            packedBitmap.dispose();
            packedBitmap = null;
        }
    }
}
