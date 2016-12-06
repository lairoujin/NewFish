package com.example.roujin.fish;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by roujin on 2016/12/3.
 */
public class Fish extends FishSprite{
    private int horiSegment = 6;
    private int verSegment = 6;
    private int  level = 0;
    private int explodeFrequency = 15;

    public int life = 20;
    public static final int MY_FISH_DEF_LIFE = 10;

    public Fish(Bitmap bitmap) {
        super(bitmap);
        scaleOrigin = 1.0f;
    }

    @Override
    public float getWidth() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return bitmap.getWidth() / horiSegment;
        }
        return 0;
    }


    @Override
    public float getHeight() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return bitmap.getHeight() / verSegment;
        }
        return 0;
    }

    @Override
    public Rect getBitmapSrcRec() {
        Rect rect = super.getBitmapSrcRec();
        int left = (int)(level * getWidth());
        rect.offsetTo(left, 0);
        return rect;
    }

    @Override
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            if(getFrame() % explodeFrequency == 0){
                //level自加1，用于绘制下个爆炸片段
                level++;
                if(level >= horiSegment){
                    level = 0;
                }
            }
        }
    }

}
