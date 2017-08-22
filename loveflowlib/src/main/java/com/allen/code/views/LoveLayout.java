package com.allen.code.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.allen.code.R;
import com.allen.code.evaluators.BezierEvaluator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by allen on 2017/6/22.<br>
 * mail：1059709131@qq.com
 */
public class LoveLayout extends RelativeLayout {
    private Drawable[] drawables = new Drawable[5];
    private Random random = new Random();
    private LayoutParams params;
    private int dWidth;//爱心图片的宽度
    private int dHeight;//爱心图片的高度
    private int mWidth;//此控件的宽度
    private int mHeight;//此控件的高度
    Timer timer ;

    private static final int TIME_INTERVAL = 500;//时间间隔
    private static final int SEND_MSG = 1;//发送消息

    private Interpolator[] interpolators;
    private AccelerateDecelerateInterpolator occ = new AccelerateDecelerateInterpolator();
    private LinearInterpolator bcc = new LinearInterpolator();
    private DecelerateInterpolator dcc = new DecelerateInterpolator();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_MSG:
                    addLoveView();
                    break;
            }
        }
    };


    public LoveLayout(Context context) {
        this(context, null);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                Message message = new Message();
                message.what = SEND_MSG;
                mHandler.sendMessage(message);
            }
        }, 100, TIME_INTERVAL);
    }

    /**
     * 初始化
     */
    private void init() {
        drawables[0] = getResources().getDrawable(R.drawable.love_1);
        drawables[4] = getResources().getDrawable(R.drawable.love_2);
        drawables[2] = getResources().getDrawable(R.drawable.love_3);
        drawables[3] = getResources().getDrawable(R.drawable.love_4);
        drawables[1] = getResources().getDrawable(R.drawable.love_5);

        interpolators = new Interpolator[3];
        interpolators[0] = bcc;
        interpolators[1] = dcc;
        interpolators[2] = occ;
//        interpolators[3] = dcc;


        dWidth = drawables[0].getIntrinsicWidth();
        dHeight = drawables[0].getIntrinsicHeight();

        params = new LayoutParams(dWidth, dHeight);

        timer = new Timer();
    }

    public void addLoveView() {
        //添加点赞的图片
        final ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(drawables[random.nextInt(5)]);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_BOTTOM);
//        params.bottomMargin = getContext().getResources().getDimensionPixelSize(R.dimen.love_bottom);
        addView(iv, params);
        //开启动画
        final AnimatorSet set = getAnimation(iv);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(iv);
            }
        });
        set.start();
    }

    private AnimatorSet getAnimation(ImageView iv) {
        //设置入动画
        ObjectAnimator scaleX = new ObjectAnimator().ofFloat(iv, "scaleX", 0, 1f);
        ObjectAnimator scaleY = new ObjectAnimator().ofFloat(iv, "scaleY", 0, 1f);
        ObjectAnimator alpha = new ObjectAnimator().ofFloat(iv, "alpha", 0f, 1f);
        ObjectAnimator setTranslationX = new ObjectAnimator().ofFloat(iv, "translationX", mWidth + dWidth, mWidth);
        ObjectAnimator setTranslationY = new ObjectAnimator().ofFloat(iv, "translationY", mHeight + dHeight, mHeight);

        AnimatorSet enterSet = new AnimatorSet();
        enterSet.playTogether(scaleX, scaleY, alpha, setTranslationX, setTranslationY);
        enterSet.setDuration(100);

        //设置贝塞尔动画
        ValueAnimator bezierSet = getBezierAnimator(iv);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(enterSet, bezierSet);
        set.setTarget(iv);
        return set;
    }

    /**
     * 贝塞尔动画
     *
     * @param iv
     * @return
     */
    private ValueAnimator getBezierAnimator(final ImageView iv) {

        //贝塞尔曲线的四个点（起始点pointF0,拐点pointF1,ponitF2,终点pointF3）
//        PointF pointF0 = new PointF((mWidth-dWidth)/2,mHeight-dHeight-getContext().getResources().getDimensionPixelSize(R.dimen.love_bottom));
        PointF pointF0 = new PointF((mWidth - dWidth) / 2, mHeight - dHeight);
//        PointF pointF0 = new PointF((mWidth-dWidth)/2,mHeight-dh);
        PointF pointF1 = getTogglePoint(1);
        PointF pointF2 = getTogglePoint(2);
        PointF pointF3 = new PointF(random.nextInt(mWidth), 0);


        final ValueAnimator animator = new ValueAnimator().ofObject(new BezierEvaluator(pointF1, pointF2), pointF0, pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF = (PointF) valueAnimator.getAnimatedValue();
                iv.setX(pointF.x);
                iv.setY(pointF.y);
                iv.setAlpha(1 - valueAnimator.getAnimatedFraction() - 0.01f);
                iv.setRotation(1 - valueAnimator.getAnimatedFraction() * 360);
            }
        });
        animator.setDuration(3000);
//        animator.setInterpolator(interpolators[random.nextInt(3)]);
        return animator;
    }

    private PointF getTogglePoint(int i) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt(mWidth);
        if (i == 1) {
            pointF.y = random.nextInt(mHeight / 2) + mHeight / 2;
        } else {
            pointF.y = random.nextInt(mHeight / 2);
        }
        return pointF;
    }
}
