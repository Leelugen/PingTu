package com.llg.pingtu.utils;

/**
 * Created by Administrator on 17-2-21.
 */

import android.graphics.Bitmap;

/**
 * 图片块类
 */
public class ImagePiece {
    private int index;
    private Bitmap bitmap;

    public ImagePiece(int index, Bitmap bitmap) {
        this.index = index;
        this.bitmap = bitmap;
    }

    public ImagePiece() {
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "ImagePiece{" +
                "index=" + index +
                ", bitmap=" + bitmap +
                '}';
    }
}
