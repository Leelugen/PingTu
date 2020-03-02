package com.llg.pingtu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;

import com.llg.pingtu.R;
import com.llg.pingtu.utils.ImagePiece;
import com.llg.pingtu.utils.ImageSplitterUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 17-2-21.
 */

public class GameLayout extends ViewGroup implements View.OnClickListener {
    private static final String TAG = GameLayout.class.getSimpleName();

    private int mWidth; // 游戏界面的边长
    private int mMargin = 2; // 图片块间的边距 dp
    private int mGameItemWidth;  //图片块的边长

    private ImageView[] mGameItems; //图片块
    private Bitmap mBitmap; //当前加载的图片资源
    private List<ImagePiece> mBitmaps; //被切割后的图片集

    private int mColumn = 2;//宫格乘数
    private int level = 1;//关卡数
    private Boolean isSuccess = false; //是否过关成功
    private Boolean isGameOver = false; //是否游戏失败
    private int playTime;//设置游戏时间

    public GameLayout(Context context) {
        this(context, null);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        //将边距的单位转成dp
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GameLayout);
        int imageResId = a.getResourceId(R.styleable.GameLayout_game_image, R.drawable.image);
        a.recycle();
        //进行切图，以及排序
        initBitmap(imageResId);
        //设置ImageView item 的边长等属性
        addItem();
        //计算游戏关卡的时间
        countPlayTimeByLevel();
        //发送显示时间的信息
        mHandler.sendEmptyMessage(TIME_CHANGE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure: ");
        //取宽 高中的小值
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mGameItemWidth = (mWidth - mMargin * (mColumn - 1)) / mColumn;
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(mGameItemWidth, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(childMeasureSpec, childMeasureSpec);
        }
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: ");
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        Log.e(TAG, String.format("onLayout: l %d  t %d r %d b %d changed %b childCount %d", l, t, r, b,changed,count));
        View viewGroup = getChildAt(getChildCount()-1);
        if (viewGroup instanceof ViewGroup){
            viewGroup.layout(0,0,r-1,b-t);
            return;
        }
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int x = i % mColumn;
            int y = i / mColumn;
            int left = (mMargin + mGameItemWidth) * x;
            int top = (mMargin + mGameItemWidth) * y;
            child.layout(left, top, left + mGameItemWidth, top + mGameItemWidth);
        }
    }

    /**
     * 进行切图，以及排序
     */
    private void initBitmap(@DrawableRes int imgId) {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), imgId);
        }
        mBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
        //乱序
        Collections.sort(mBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece o1, ImagePiece o2) {
                //返回值即表示两者间的关系 正大 负小 零相等
                //返回随机数 即可乱序
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    private void resetBitmap() {
        if (mBitmap == null) {
            return;
        }
        mBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
        //乱序
        Collections.sort(mBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece o1, ImagePiece o2) {
                //返回值即表示两者间的关系 正大 负小 零相等
                //返回随机数 即可乱序
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView item 的边长 外边距等属性
     */
    private void addItem() {
        mGameItems = new ImageView[mColumn * mColumn];
        //生成Item ,设置Rule
        for (int i = 0; i < mGameItems.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mBitmaps.get(i).getBitmap());

            mGameItems[i] = item;
            //用于rule 通过id的值设置位置
            item.setId(i + 1);
            //在Item的tag中存储了index值
            item.setTag(i + "_" + mBitmaps.get(i).getIndex());
            addView(item);
        }
    }


    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (param < min) {
                min = param;
            }
        }
        return min;
    }


    public PlayGameListener mPlayGameListener;


    /**
     * 设置接口回调
     *
     * @param mListener
     */
    public void setPlayGameListener(PlayGameListener mListener) {
        this.mPlayGameListener = mListener;
    }

    public interface PlayGameListener {
        void nextLevel();

        void UIChange(int currentTime, int level);

        void gameOver();
    }

    private static final int TIME_CHANGE = 0x1;
    private static final int NEXT_LEVEL = 0x2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGE:
                    if (isSuccess || isGameOver || isPause) {
                        return;
                    }
                    if (playTime == 0) {
                        isGameOver = true;
                        mPlayGameListener.gameOver();
                        return;
                    }
                    if (mPlayGameListener != null) {
                        mPlayGameListener.UIChange(playTime, level);
                    }
                    playTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGE, 1000);
                    break;
                case NEXT_LEVEL:
                    level += 1;
                    if (mPlayGameListener != null) {
                        mPlayGameListener.nextLevel();
                    } else {
                        nextLevel();
                    }
                    break;
            }
        }
    };


    private ImageView mFirst;
    private ImageView mSecond;
    private Boolean isAnima = false; //是否正在执行动画效果

    @Override
    public void onClick(View v) {
        if (isAnima) {
            return;
        }
        //重复点击了一块区域
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;
            exchangeView();
        }
    }

    //动画层
    public FrameLayout mAnimaLayout;

    /**
     * 交换游戏的Item
     */
    private void exchangeView() {
        isAnima = true;
        mFirst.setColorFilter(null);

        //构造交换动画层
        setUpAnimaLayout();

        //动画层复制原层的操作
        ImageView first = new ImageView(getContext());
        ImageView second = new ImageView(getContext());
        //设置参数
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mGameItemWidth, mGameItemWidth);
        lp.leftMargin = mFirst.getLeft() - mMargin;
        lp.topMargin = mFirst.getTop() - mMargin;
        first.setLayoutParams(lp);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mGameItemWidth, mGameItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mMargin;
        lp2.topMargin = mSecond.getTop() - mMargin;
        second.setLayoutParams(lp2);

        //设置图片
        final Bitmap firstBitmap = mBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        final Bitmap secondBitmap = mBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();

        first.setImageBitmap(firstBitmap);
        second.setImageBitmap(secondBitmap);

        mAnimaLayout.addView(first);
        mAnimaLayout.addView(second);
        //设置动画
        TranslateAnimation anima = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anima.setDuration(200);
        anima.setFillAfter(true);
        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);
                mFirst = mSecond = null;
                isAnima = false;
                mAnimaLayout.removeAllViews();
                checkSuccess();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        first.startAnimation(anima);

        TranslateAnimation anima2 = new TranslateAnimation(0, mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop() - mSecond.getTop());
        anima2.setDuration(200);
        anima2.setFillAfter(true);
        second.startAnimation(anima2);
    }


    /**
     * 根据Tag获取ImageId
     *
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    /**
     * 根据Tag获取IndexId
     *
     * @param tag
     * @return
     */

    public int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }


    /**
     * 设置动画层
     */
    private void setUpAnimaLayout() {
        if (mAnimaLayout == null) {
            mAnimaLayout = new FrameLayout(getContext());
            LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            addView(mAnimaLayout,lp);
        } else {
            mAnimaLayout.removeAllViews();
        }
    }

    /**
     * 判断游戏是否过关
     */
    private void checkSuccess() {
        for (int i = 0; i < mGameItems.length - 1; i++) {
            if (getImageIndexByTag((String) mGameItems[i].getTag()) == i) {
                isSuccess = true;
            } else {
                isSuccess = false;
                break;
            }
        }
        if (isSuccess) {
            mHandler.removeMessages(TIME_CHANGE);
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }


    /**
     * 游戏成功后跳转下一关
     */
    public void nextLevel() {
        GameLayout.this.removeAllViews();
        mBitmaps.clear();
        mGameItems = null;
        mAnimaLayout = null;
        mColumn++;
        resetBitmap();
        //设置ImageView item 的边长等属性
        addItem();
        //计算游戏关卡的时间
        countPlayTimeByLevel();
        //发送显示时间的信息
        mHandler.sendEmptyMessage(TIME_CHANGE);
        requestLayout();
        isSuccess = false;
    }

    /**
     * 重新进入本关卡
     */
    public void restartGame() {
        isGameOver = false;
        mColumn--;
        nextLevel();
    }


    private boolean isPause = false;

    /**
     * 暂停游戏
     */
    public void pauseGame() {
        isPause = true;
        mHandler.removeMessages(TIME_CHANGE);
    }

    /**
     * 恢复游戏
     */
    public void resumeGame() {
        if (isPause) {
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGE);
        }
    }

    /**
     * 计算出游戏所需关卡的时间
     */
    private void countPlayTimeByLevel() {
        playTime = (int) Math.pow(2, level) * 60;
    }

}
