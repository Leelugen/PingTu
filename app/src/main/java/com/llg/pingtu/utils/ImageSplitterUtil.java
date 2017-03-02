package com.llg.pingtu.utils;

/**
 * Created by Administrator on 17-2-21.
 */

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片切割器
 */
public class ImageSplitterUtil {

    /**
     * @param bitmap 传入的图片资源
     * @param piece  将图片切成piece*piece块
     * @return List<ImagePiece>
     */
    public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {
        List<ImagePiece> imagePieces = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int pieceWidth = Math.min(width,height)/piece;

        for (int i = 0; i <piece; i++){
            for (int j = 0 ; j<piece; j++){
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.setIndex(j+piece*i);

                int x = j*pieceWidth;
                int y = i*pieceWidth;

                imagePiece.setBitmap(Bitmap.createBitmap(bitmap,x,y,pieceWidth,pieceWidth));
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }
}
