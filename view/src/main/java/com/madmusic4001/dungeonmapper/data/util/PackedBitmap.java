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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for manipulating a packed bitmap
 */
@SuppressWarnings("UnusedDeclaration")
public class PackedBitmap {

    static final class Node {
        public Node leftChild;
        public Node rightChild;
        public Rect rect;
        public String leaveName;

        public Node (int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName) {
            this.rect = new Rect(x, y, x + width, y + height);
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            this.leaveName = leaveName;
        }

        public Node () {
            rect = new Rect();
        }
    }

    public class Page {
        Node root;
        HashMap<String, Rect> rects;
        Bitmap image;
        Canvas canvas;
        final ArrayList<String> addedRects = new ArrayList<>();

        public Bitmap getBitmap() {
            return image;
        }

        public HashMap<String, Rect> getRects () {
            return rects;
        }
    }

    final int pageWidth;
    final int pageHeight;
    final int padding;
    final boolean duplicateBorder;
    final ArrayList<Page> pages = new ArrayList<>();
    Page currPage;
    boolean disposed;

    /** <p>
     * Creates a new ImagePacker which will insert all supplied images into a <code>width</code> by <code>height</code> image.
     * <code>padding</code> specifies the minimum number of pixels to insert between images. <code>border</code> will duplicate the
     * border pixels of the inserted images to avoid seams when rendering with bi-linear filtering on.
     * </p>
     *
     * @param width the width of the output image
     * @param height the height of the output image
     * @param padding the number of padding pixels
     * @param duplicateBorder whether to duplicate the border */
    public PackedBitmap(int width, int height, int padding, boolean duplicateBorder) {
        this.pageWidth = width;
        this.pageHeight = height;
        this.padding = padding;
        this.duplicateBorder = duplicateBorder;
        newPage();
    }

    /** <p>
     * Inserts the given {@link android.graphics.Bitmap}. You can later on retrieve the images position in the output
     * image via the supplied name
     * and the method {@link #getRect(String)}.
     * </p>
     *
     * @param name the name of the image
     * @param image the image
     * @return Rect describing the area the pixmap was rendered to or null.
     * @throws RuntimeException in case the image did not fit due to the page size being to small or providing a duplicate name */
    public synchronized Rect pack (String name, Bitmap image) {
        if (disposed) return null;
        if (getRect(name) != null) throw new RuntimeException("Key with name '" + name + "' is already in map");
        int borderPixels = padding + (duplicateBorder ? 1 : 0);
        borderPixels <<= 1;

        Rect rect = new Rect(0, 0, image.getWidth() + borderPixels, image.getHeight() + borderPixels);
        Rect srcRect = new Rect();
        Rect dstRect = new Rect();

        if (rect.width() > pageWidth || rect.height() > pageHeight)
            throw new RuntimeException("page size for '" + name + "' to small");

        Node node = insert(currPage.root, rect);

        if (node == null) {
            newPage();
            return pack(name, image);
        }

        node.leaveName = name;
        rect = new Rect(node.rect);
        rect.right -= borderPixels;
        rect.bottom -= borderPixels;
        borderPixels >>= 1;
        rect.left += borderPixels;
        rect.top += borderPixels;
        currPage.rects.put(name, rect);

        this.currPage.canvas.drawBitmap(image, rect.left, rect.top, null);

        if (duplicateBorder) {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            // Copy corner pixels to fill corners of the padding.
            srcRect.set(0, 0, 1, 1);
            dstRect.set(rect.left - 1, rect.top - 1, rect.left, rect.top);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(imageWidth - 1, 0, imageWidth, 1);
            dstRect.set(rect.right, rect.top - 1, rect.right + 1, rect.top);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(0, imageHeight - 1, 1, imageHeight);
            dstRect.set(rect.left - 1, rect.bottom, rect.left, rect.bottom + 1);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(imageWidth - 1, imageHeight - 1, imageWidth, imageHeight);
            dstRect.set(rect.right, rect.bottom, rect.right + 1, rect.bottom + 1);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            // Copy edge pixels into padding.
            srcRect.set(0, 0, imageWidth, 1);
            dstRect.set(rect.left, rect.top - 1, rect.right, rect.top);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(0, imageHeight - 1, imageWidth, imageHeight);
            dstRect.set(rect.left, rect.bottom, rect.right, rect.bottom + 1);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(0, 0, 1, imageHeight);
            dstRect.set(rect.left - 1, rect.top, rect.left, rect.bottom);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
            srcRect.set(imageWidth - 1, 0, imageWidth, imageHeight);
            dstRect.set(rect.right, rect.top, rect.right + 1, rect.bottom);
            this.currPage.canvas.drawBitmap(image, srcRect, dstRect, null);
        }

        currPage.addedRects.add(name);
        return rect;
    }

    private void newPage () {
        Page page = new Page();
        page.image = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
        page.canvas = new Canvas(page.image);
        page.root = new Node(0, 0, pageWidth, pageHeight, null, null, null);
        page.rects = new HashMap<>();
        pages.add(page);
        currPage = page;
    }

    private Node insert (Node node, Rect rect) {
        if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
            Node newNode;

            newNode = insert(node.leftChild, rect);
            if (newNode == null) newNode = insert(node.rightChild, rect);

            return newNode;
        } else {
            if (node.leaveName != null) return null;

            if (node.rect.width() == rect.width() && node.rect.height() == rect.height()) {
                return node;
            }
            if (node.rect.width() < rect.width() || node.rect.height() < rect.height()) {
                return null;
            }

            node.leftChild = new Node();
            node.rightChild = new Node();

            int deltaWidth = node.rect.width() - rect.width();
            int deltaHeight = node.rect.height() - rect.height();

            if (deltaWidth > deltaHeight) {
                node.leftChild.rect.left = node.rect.left;
                node.leftChild.rect.top = node.rect.top;
                node.leftChild.rect.right = node.leftChild.rect.left + rect.width();
                node.leftChild.rect.bottom = node.leftChild.rect.top + node.rect.height();

                node.rightChild.rect.left = node.rect.left + rect.width();
                node.rightChild.rect.top = node.rect.top;
                node.rightChild.rect.right = node.rightChild.rect.left + (
                        node.rect.width() - rect.width());
                node.rightChild.rect.bottom = node.rightChild.rect.top + node.rect.height();
            } else {
                node.leftChild.rect.left = node.rect.left;
                node.leftChild.rect.top = node.rect.top;
                node.leftChild.rect.right = node.rect.right;
                node.leftChild.rect.bottom = node.leftChild.rect.top + rect.height();

                node.rightChild.rect.left = node.rect.left;
                node.rightChild.rect.top = node.rect.top + rect.height();
                node.rightChild.rect.right = node.rightChild.rect.left + node.rect.width();
                node.rightChild.rect.bottom = node.rightChild.rect.top +
                        (node.rect.height()) - rect.height();
            }

            return insert(node.leftChild, rect);
        }
    }

    /** @return the {@link Page} instances created so far. This method is not thread safe! */
    public ArrayList<Page> getPages () {
        return pages;
    }

    /** @param name the name of the image
     * @return the rectangle for the image in the page it's stored in or null */
    public synchronized Rect getRect (String name) {
        for (Page page : pages) {
            Rect rect = page.rects.get(name);
            if (rect != null) return rect;
        }
        return null;
    }

    /** @param name the name of the image
     * @return the page the image is stored in or null */
    public synchronized Page getPage (String name) {
        for (Page page : pages) {
            Rect rect = page.rects.get(name);
            if (rect != null) return page;
        }
        return null;
    }

    /** Returns the index of the page containing the given packed rectangle.
     * @param name the name of the image
     * @return the index of the page the image is stored in or -1 */
    public synchronized int getPageIndex (String name) {
        for (int i = 0; i < pages.size(); i++) {
            Rect rect = pages.get(i).rects.get(name);
            if (rect != null) return i;
        }
        return -1;
    }

    /**
     * Disposes all Bitmap instances for the pages created so far.
     */
    public synchronized void dispose () {
        for (Page page : pages) {
            page.image.recycle();
        }
        disposed = true;
    }

    // Getters and setters
    public int getPageWidth () {
        return pageWidth;
    }
    public int getPageHeight () {
        return pageHeight;
    }
    public int getPadding () {
        return padding;
    }
    public boolean duplicateBorder () {
        return duplicateBorder;
    }
}
