package com.example.roujin.fish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by roujin on 2016/12/3.
 */
public class GameView extends View{
    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private float density = getResources().getDisplayMetrics().density;//屏幕密度

    private long frame = 0;//总共绘制的帧数
    private int screenWidth;
    private int screenHeight;

    //触摸事件相关的变量
    private static final int TOUCH_MOVE = 1;//移动
    private static final int TOUCH_SINGLE_CLICK = 2;//单击
    private static final int TOUCH_DOUBLE_CLICK = 3;//双击
    //一次单击事件由DOWN和UP两个事件合成，假设从down到up间隔小于200毫秒，我们就认为发生了一次单击事件
    private static final int singleClickDurationTime = 200;
    //一次双击事件由两个点击事件合成，两个单击事件之间小于300毫秒，我们就认为发生了一次双击事件
    private static final int doubleClickDurationTime = 300;
    private long lastSingleClickTime = -1;//上次发生单击的时刻
    private long touchDownTime = -1;//触点按下的时刻
    private long touchUpTime = -1;//触点弹起的时刻
    private float touchX = -1;//触点的x坐标
    private float touchY = -1;//触点的y坐标

    private Paint scorePaint;
    private int life = 0;

    //自己鱼
    private Fish myFish;
    private List<FishSprite> sprites = new ArrayList<FishSprite>();
    private List<FishSprite> spritesNeedAdded = new ArrayList<FishSprite>();
    private Paint paint;

    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    private void init(){
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        scorePaint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(density * 15);
    }

    public void start(int[] bitmapIds){
        destroy();
        for(int bitmapId : bitmapIds){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
            bitmaps.add(bitmap);
        }
        startWhenBitmapsReady();
    }

    private void startWhenBitmapsReady(){
        myFish = new Fish(bitmaps.get(0));
        life = myFish.life;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGameStarted(canvas);
    }

    private void drawGameStarted(Canvas canvas){
        if(frame == 0){
            myFish.centerTo(screenWidth / 2, screenHeight / 2);
        }

        //将spritesNeedAdded添加到sprites中
        if(spritesNeedAdded.size() > 0){
            sprites.addAll(spritesNeedAdded);
            spritesNeedAdded.clear();
        }
        if (frame % 100 == 0){
            int index = new Random().nextInt(10) + 1;
            int life;
            if (index == 1){
                life = 20;
            }else if (index == 2){
                life = 25;
            }else if (index == 3){
                life = 30;
            }else if (index == 4){
                life = 35;
            }else if (index == 5){
                life = 50;
            }else if (index == 6){
                life = 15;
            }else if (index == 7){
                life = 15;
            }else if (index == 8){
                life = 10;
            }else if (index == 9){
                life = 10;
            }else {
                life = 10;
            }
            Log.d("LIFE","生命" + life);
            createRandomSprite(life);
        }
        frame++;

        Iterator<FishSprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            FishSprite s = iterator.next();

            if(!s.isDestroyed()){
                s.draw(canvas, paint, this);
            }
            if(s.isDestroyed()){
                iterator.remove();
            }
        }

        if(myFish != null) {
            myFish.draw(canvas, paint, this);
            if (myFish.isDestroyed()) {
            }
            postInvalidate();
        }

        drawLife(canvas);
    }

    private void drawLife(Canvas canvas){
        canvas.drawText("生命:" + life, screenWidth - 300, 50, paint);
    }

    private void createRandomSprite(int life){
        FishSprite sprite = new EnermyFish(bitmaps.get(1), life);
        float spriteWidth = sprite.getWidth();
        float spriteHeight = sprite.getHeight();
        float y = (float)((screenHeight - spriteHeight)*Math.random());
        float x = -spriteWidth;
        sprite.setX(x);
        sprite.setY(y);
        addSprite(sprite);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchType = resolveTouchType(event);
        if(touchType == TOUCH_MOVE){
            if(myFish != null){
                myFish.centerTo(touchX, touchY);
            }
        }
        return true;
    }



    //合成我们想要的事件类型
    private int resolveTouchType(MotionEvent event){
        int touchType = -1;
        int action = event.getAction();
        touchX = event.getX();
        touchY = event.getY();
        if(action == MotionEvent.ACTION_MOVE){
            long deltaTime = System.currentTimeMillis() - touchDownTime;
            if(deltaTime > singleClickDurationTime){
                //触点移动
                touchType = TOUCH_MOVE;
            }
        }else if(action == MotionEvent.ACTION_DOWN){
            //触点按下
            touchDownTime = System.currentTimeMillis();
        }else if(action == MotionEvent.ACTION_UP){
            //触点弹起
            touchUpTime = System.currentTimeMillis();
            //计算触点按下到触点弹起之间的时间差
            long downUpDurationTime = touchUpTime - touchDownTime;
            //如果此次触点按下和抬起之间的时间差小于一次单击事件指定的时间差，
            //那么我们就认为发生了一次单击
            if(downUpDurationTime <= singleClickDurationTime){
                //计算这次单击距离上次单击的时间差
                long twoClickDurationTime = touchUpTime - lastSingleClickTime;

                if(twoClickDurationTime <=  doubleClickDurationTime){
                    //如果两次单击的时间差小于一次双击事件执行的时间差，
                    //那么我们就认为发生了一次双击事件
                    touchType = TOUCH_DOUBLE_CLICK;
                    //重置变量
                    lastSingleClickTime = -1;
                    touchDownTime = -1;
                    touchUpTime = -1;
                }else{
                    //如果这次形成了单击事件，但是没有形成双击事件，那么我们暂不触发此次形成的单击事件
                    //我们应该在doubleClickDurationTime毫秒后看一下有没有再次形成第二个单击事件
                    //如果那时形成了第二个单击事件，那么我们就与此次的单击事件合成一次双击事件
                    //否则在doubleClickDurationTime毫秒后触发此次的单击事件
                    lastSingleClickTime = touchUpTime;
                }
            }
        }
        return touchType;
    }
    //---------------------------------------
    //移除掉已经destroyed的Sprite
    private void removeDestroyedSprites(){
        Iterator<FishSprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            FishSprite s = iterator.next();
            if(s.isDestroyed()){
                iterator.remove();
            }
        }
    }

    private void destroyNotRecyleBitmaps(){
        //将游戏设置为销毁状态
        //重置frame
        frame = 0;


        //销毁战斗机
        if(myFish != null){
            myFish.destroy();
        }
        myFish = null;

        for(FishSprite s : sprites){
            s.destroy();
        }
        sprites.clear();
    }

    public void destroy(){
        destroyNotRecyleBitmaps();

        //释放Bitmap资源
        for(Bitmap bitmap : bitmaps){
            bitmap.recycle();
        }
        bitmaps.clear();
    }

    //------pubic--

    //向Sprites中添加Sprite
    public void addSprite(FishSprite sprite){
        spritesNeedAdded.add(sprite);
    }

    public float getDensity(){
        return density;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public Fish getMyFish(){
        return myFish;
    }


}
