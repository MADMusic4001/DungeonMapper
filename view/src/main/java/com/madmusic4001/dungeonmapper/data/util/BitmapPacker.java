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
package com.madmusic4001.dungeonmapper.data.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * ${CLASS_DESCRIPTION}
 *
 * @author Mark
 *         Created 9/9/2014.
 */
public class BitmapPacker {
    private boolean packed = false;
    private Bitmap bitmap;
    private int width;
    private int height;
    private int padding;
    private boolean duplicateBorder;
    private ArrayList<Level> levels = new ArrayList<Level>(10);
    private ArrayList<Region> allRegions = new ArrayList<Region>(100);
    private TreeSet<ImageRegion> regionsSet;

    static final class Level {
        Level previousLevel = null;
        int remainingWidth;
        int ceiling;
        private ArrayList<ImageRegion> imageRegions = new ArrayList<ImageRegion>(20);
    }

    static final class Region {
        private String name;
        private Rect rect;
    }

    static final class ImageRegion {
        private Bitmap image;
        private Region region = new Region();
    }

    public BitmapPacker(int width, int height, int padding, boolean duplicateBorder) {
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.duplicateBorder = duplicateBorder;

        regionsSet = new TreeSet<ImageRegion>(new Comparator<ImageRegion>() {
            @Override
            public int compare(ImageRegion lhs, ImageRegion rhs) {
                int result = rhs.region.rect.height() - lhs.region.rect.height();
                if(result == 0) {
                    result = 1;
                }
                return result;
            }
        });
    }

    public void addImage(String name, Bitmap image) {
        if(packed) throw new IllegalStateException("Images cannot be added to an already packed " +
                "instance");

        if(image != null) {
            ImageRegion imageRegion = new ImageRegion();
            imageRegion.region.name = name;
            imageRegion.image = image;
            imageRegion.region.rect = new Rect(0, 0, image.getWidth(), image.getHeight());
            regionsSet.add(imageRegion);
        }
        else {
            Log.d(this.getClass().getName(), "Null image for " + name);
        }
    }

    public Bitmap pack() {
        int borderPixels = padding + (duplicateBorder ? 1 : 0);
        int bothBorderPixels = borderPixels << 1;
        Level lastLevel = new Level();
        lastLevel.remainingWidth = width;
        lastLevel.ceiling = 0;
        levels.add(lastLevel);

        for(ImageRegion imageRegion : regionsSet) {
            Level level = lastLevel;

            Rect rect = new Rect(0, 0, imageRegion.image.getWidth() + bothBorderPixels,
                    imageRegion.image.getHeight() + bothBorderPixels);
            while (level != null && rect.width() > level.remainingWidth) {
                level = level.previousLevel;
            }
            if(level == null) {
                level = new Level();
                level.remainingWidth = width;
                level.previousLevel = lastLevel;
                level.ceiling = lastLevel.ceiling +
                        lastLevel.imageRegions.get(0).region.rect.height() +
                        bothBorderPixels;
                lastLevel = level;
                levels.add(level);
            }
            level.imageRegions.add(imageRegion);
            imageRegion.region.rect.left = width - level.remainingWidth + borderPixels;
            imageRegion.region.rect.top = level.ceiling + borderPixels;
            imageRegion.region.rect.right =
                    imageRegion.region.rect.left + imageRegion.image.getWidth();
            imageRegion.region.rect.bottom =
                    imageRegion.region.rect.top + imageRegion.image.getHeight();
            level.remainingWidth -= rect.width();
        }
        return drawBitmaps();
    }

    private Bitmap drawBitmaps() {
        Rect srcRect = new Rect();
        Rect dstRect = new Rect();
        bitmap = Bitmap.createBitmap(width, height,  Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for(Level level : levels) {
            for(ImageRegion imageRegion : level.imageRegions) {
                canvas.drawBitmap(imageRegion.image, imageRegion.region.rect.left,
                        imageRegion.region.rect.top, null);

                if (duplicateBorder) {
                    int imageWidth = imageRegion.image.getWidth();
                    int imageHeight = imageRegion.image.getHeight();
                    // Copy corner pixels to fill corners of the padding.
                    srcRect.set(0, 0, 1, 1);
                    dstRect.set(imageRegion.region.rect.left - 1, imageRegion.region.rect.top - 1,
                            imageRegion.region.rect.left, imageRegion.region.rect.top);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(imageWidth - 1, 0, imageWidth, 1);
                    dstRect.set(imageRegion.region.rect.right, imageRegion.region.rect.top - 1,
                            imageRegion.region.rect.right + 1, imageRegion.region.rect.top);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(0, imageHeight - 1, 1, imageHeight);
                    dstRect.set(imageRegion.region.rect.left - 1, imageRegion.region.rect.bottom,
                            imageRegion.region.rect.left, imageRegion.region.rect.bottom + 1);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(imageWidth - 1, imageHeight - 1, imageWidth, imageHeight);
                    dstRect.set(imageRegion.region.rect.right, imageRegion.region.rect.bottom,
                            imageRegion.region.rect.right + 1, imageRegion.region.rect.bottom + 1);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    // Copy edge pixels into padding.
                    srcRect.set(0, 0, imageWidth, 1);
                    dstRect.set(imageRegion.region.rect.left, imageRegion.region.rect.top - 1,
                            imageRegion.region.rect.right, imageRegion.region.rect.top);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(0, imageHeight - 1, imageWidth, imageHeight);
                    dstRect.set(imageRegion.region.rect.left, imageRegion.region.rect.bottom,
                            imageRegion.region.rect.right, imageRegion.region.rect.bottom + 1);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(0, 0, 1, imageHeight);
                    dstRect.set(imageRegion.region.rect.left - 1, imageRegion.region.rect.top,
                            imageRegion.region.rect.left, imageRegion.region.rect.bottom);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                    srcRect.set(imageWidth - 1, 0, imageWidth, imageHeight);
                    dstRect.set(imageRegion.region.rect.right, imageRegion.region.rect.top,
                            imageRegion.region.rect.right + 1, imageRegion.region.rect.bottom);
                    canvas.drawBitmap(imageRegion.image, srcRect, dstRect, null);
                }
                allRegions.add(imageRegion.region);
            }
            level.previousLevel = null;
        }
        packed = true;
        levels.clear();
        regionsSet.clear();
        return bitmap;
    }

    public Rect getRect(String name) {
        for(Region region : allRegions) {
            if(region.name.equals(name)) {
                return region.rect;
            }
        }
        return null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
