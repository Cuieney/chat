package com.android.youtube.customview;

/**
 * Created by laoyongzhi on 2017/4/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public class YouTubeVideoView extends LinearLayout  {

    private RecyclerView mRecyclerView;
    private boolean isBottomMax;



    public interface Callback {
        void onVideoViewHide();

        void onVideoClick();
    }

    //单击以及消失时的回调
    private Callback mCallback;

    // 可拖动的videoView 和下方的详情View
    private View mVideoView;
    private View mDetailView;


    //滑动区间,取值为是videoView最小化时距离屏幕顶端的高度
    private float allScrollY;
    private float allMinScrollY;

    //1f为初始状态，0.5f或0.25f(横屏时)为最小状态
    private float nowStateScale;
    //最小的缩放比例
    private float MIN_RATIO = 0.5f;
    //播放器比例
    private static final float VIDEO_RATIO = 16f / 9f;

    //是否是第一次Measure，用于获取播放器初始宽高
    private boolean isFirstMeasure = true;

    //VideoView初始宽高
    private int originalWidth;//视频原始宽高
    private int originalHeight;


    private int originListHeight;//列表原始高度


    //最小时距离屏幕右边以及下边的 DP值 初始化时会转化为PX
    private static final int MARGIN_DP = 10;
    private int marginPx;
    private static final int MARGIN_RL = 10;
    private int marginRLPx;//最小化左右边距
    private float marginBottomPx;//最小化底部编剧
    private float videoHeightPx;//最小中化高度大小
    private float videoMinHeightPx;//最小化高度
    private float displayHeight;
    private float videoWidthPx;//最小化宽度
    private float swipePx2Dismiss = 100;

    //是否可以横滑删除
    private boolean canHide;

    public YouTubeVideoView(Context context) {
        super(context);
        this.context = context;
    }

    public YouTubeVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public YouTubeVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    private Context context;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2)
            throw new RuntimeException("YouTubeVideoView only need 2 child views");

        mVideoView = getChildAt(0);
        mDetailView = getChildAt(1);
        init();
    }

    private LayoutParamsWrapper layoutPVideo;//头部容器layout
    private LayoutParamsWrapper layoutPList;//list容器
    private LayoutParamsWrapper layoutPContainer;//全局容器
    private RelativeLayoutParamsWrapper layoutPVController;//播放器控制器容器
    private RelativeLayoutParamsWrapper layoutPVideoView;//视频视图控制器
    private RelativeLayoutParamsWrapper layoutPCoverView;//蒙层


    private void init() {
        mVideoView.setOnTouchListener(new VideoTouchListener());
        //初始化包装类

        //DP To PX
        marginPx = MARGIN_DP * (getContext().getResources().getDisplayMetrics().densityDpi / 160);
        marginRLPx = MARGIN_RL * (getContext().getResources().getDisplayMetrics().densityDpi / 160);

        //当前缩放比例
        nowStateScale = 1f;

        //如果是横屏则最小化比例为0.25f
        if (mVideoView.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            MIN_RATIO = 0.25f;

        originalWidth = mVideoView.getContext().getResources().getDisplayMetrics().widthPixels;
//        originalHeight = (int) (originalWidth / VIDEO_RATIO);
        videoWidthPx = originalWidth * 0.65f;


        layoutPVideo = new LayoutParamsWrapper(mVideoView);
        layoutPList = new LayoutParamsWrapper(mDetailView);
        layoutPContainer = new LayoutParamsWrapper(this);
        layoutPVideoView = new RelativeLayoutParamsWrapper(((ViewGroup) mVideoView).getChildAt(1));
        layoutPVController = new RelativeLayoutParamsWrapper(((ViewGroup) mVideoView).getChildAt(0));
        layoutPCoverView = new RelativeLayoutParamsWrapper(((ViewGroup) mDetailView).getChildAt(1));

        mRecyclerView = (RecyclerView) ((ViewGroup) mDetailView).getChildAt(0);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mRecyclerView.canScrollVertically(1)) {
                    isList2Top = false;
                } else {
                    isList2Top = false;
                }
                if (mRecyclerView.canScrollVertically(-1)) {
                    isList2Top = false;
                } else {
                    isList2Top = true;
                }
            }
        });


        goMin();

        layoutPVController.setMarginLeft(originalWidth-(int) videoWidthPx+marginRLPx);

    }

    public boolean isList2Top;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isFirstMeasure) {
            //滑动区间,取值为是videoView最小化时距离屏幕顶端的高度 也就是最小化时的marginTop
            int measuredHeight = getMeasuredHeight();
            videoHeightPx = (measuredHeight * 0.2f);
            allScrollY = measuredHeight * 0.70f;
            marginBottomPx = measuredHeight * 0.1f;
            videoMinHeightPx = measuredHeight * 0.1f;
            allMinScrollY = measuredHeight * 0.10f;
            originalHeight = (int) (getMeasuredHeight() * 0.4f);
            originListHeight = (int) (measuredHeight - originalHeight + allMinScrollY);
            displayHeight = measuredHeight;
            Log.i(TAG, "init: " + measuredHeight + " " + videoHeightPx + " " + allScrollY + " " + marginBottomPx + " " + originListHeight + " " + originalHeight);
            isFirstMeasure = false;
            layoutPVController.setHeight((int) videoMinHeightPx);
        }
    }

    float y = 0;

    int mLastY = 0;
    //刚触摸时手指的坐标
    float mDownY = 0;
    private VelocityTracker mVelocityTracker = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        y = ev.getY();
        int pointerId = ev.getPointerId(0);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                Log.i(TAG, "dispatchTouchEvent: ACTION_DOWN " + mDownY);
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:

                float dDownY = y - mDownY;

                Log.i(TAG, "dispatchTouchEvent: " + mDownY + " " + y);
                mVelocityTracker.addMovement(ev);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.


                if ((mDownY >= (layoutPVideo.getHeight() + layoutPVideo.getMarginTop())) && dDownY > 0 && layoutPVideo.getHeight() < originalHeight + 600 && (isList2Top)) {
                    //判断点击的范围，及当前视频尺寸大小。listview是否已经滑到顶部

                    layoutPVideo.setHeight((int) (layoutPVideo.getHeight() + dDownY));
                    Log.i(TAG, "dispatchTouchEvent: xia " + dDownY);
                    mDownY = y;
                    return true;
                } else if ((mDownY >= (layoutPVideo.getHeight() + layoutPVideo.getMarginTop())) && dDownY <= 0 && layoutPVideo.getHeight() >= originalHeight) {

                    layoutPVideo.setHeight((int) (layoutPVideo.getHeight() + dDownY));
                    //可以加个弹性动画
                    Log.i(TAG, "dispatchTouchEvent: shang " + dDownY);
                    mDownY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                float yVelocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                        pointerId);
                Log.i("VelocityTrackerCompat", "Y velocity: " +
                        yVelocity);

                if (yVelocity >= 5685 && layoutPVideo.getHeight() < originalHeight + 600) {//判断惯性加速度根据惯性加速度进行引导视频大小到底部
                    headMoveToMax();
                } else {
                    Log.i(TAG, "headMoveToMax: not come in" + " " + (layoutPVideo.getHeight() < originalHeight + 600));
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    private void headMoveToMax() {
        Log.i(TAG, "headMoveToMax: ");
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofInt(layoutPVideo, "height", layoutPVideo.getHeight(), originalHeight + 600)

        );
        set.setDuration(100).start();
    }


    private String TAG = "oye";

    private class VideoTouchListener implements OnTouchListener {
        //保存上一个滑动事件手指的坐标
        private int mLastY;
        private int mLastX;
        //刚触摸时手指的坐标
        private int mDownY;
        private int mDownX;

        private int dy;//和上一次滑动的差值 设置为全局变量是因为 UP里也要使用

        private boolean isClick;
        private int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        private VelocityTracker tracker;

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            int y = (int) ev.getRawY();
            int x = (int) ev.getRawX();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    tracker = VelocityTracker.obtain();
                    mDownY = (int) ev.getRawY();
                    mDownX = (int) ev.getRawX();

                    if (mDownY >= allScrollY + allMinScrollY && mDownY <= allScrollY + allMinScrollY + videoMinHeightPx) {
                        isBottomMax = true;
                    } else {
                        isBottomMax = false;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    tracker.addMovement(ev);
                    dy = y - mLastY; //和上一次滑动的差值
                    int dx = x - mLastX;

                    int newMarY = layoutPVideo.getMarginTop() + dy; //新的marginTop值
                    int newMarX = layoutPVideo.getMarginRight() - dx;//新的marginRight值
                    int dDownY = y - mDownY;
                    int dDownX = x - mDownX; // 从点击点开始产生的的差值

                    //如果滑动达到一定距离
                    if (Math.abs(dDownX) > touchSlop || Math.abs(dDownY) > touchSlop) {
                        isClick = false;
                        //如果X>Y 且能滑动关闭, 则动态设置水平偏移量。
                        if (Math.abs(dDownX) > Math.abs(dDownY) && canHide) {
//                            mVideoWrapper.setMarginRight(newMarX);
                        } else
                            updateVideoView(newMarY, dy); //否则通过新的marginTop的值更新大小
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:

                    if (isClick) {
                        if (nowStateScale == 1f && mCallback != null) {
                            //单击事件回调
                            mCallback.onVideoClick();
                        } else {
                            goMax();
                        }
                        break;
                    }

                    tracker.computeCurrentVelocity(100);
                    float yVelocity = Math.abs(tracker.getYVelocity());
                    tracker.clear();
                    tracker.recycle();

                    if (canHide) {
                        //速度大于一定值或者滑动的距离超过了最小化时的宽度，则进行隐藏，否则保持最小状态。
                        if ((yVelocity > touchSlop || Math.abs(layoutPVideo.getMarginTop()) > (allScrollY + allMinScrollY)) && 0 < dy) {

                            dismissView();

                        } else {
                            goMin();
                        }
                    } else {
                        confirmState(yVelocity, dy);//确定状态。
                    }
                    break;
            }

            mLastY = y;
            mLastX = x;
            return true;
        }
    }

    private void updateVideoView(int m, int originY) {

        canHide = false;
        if (mDetailView.getHeight() == 0) {
            if (layoutPVideoView.getMarginRight() <= videoWidthPx && 0 < originY) {


                int value = layoutPVideoView.getMarginRight() + originY * 9;
                if (value > videoWidthPx) {
                    value = (int) videoWidthPx;
                }
                float percent = (videoWidthPx - value) / videoWidthPx;
                if (0 > percent) {
                    percent = 0.f;
                }
                int videoHeight = (int) (videoMinHeightPx * (1 - percent));
                int videoMTop = (int) (allMinScrollY * (1 - percent));
                layoutPVideo.setMarginTop((int) (allScrollY + videoMTop));
                layoutPVideo.setHeight((int) (videoHeightPx - videoHeight));
                layoutPVideoView.setMarginRight(value);



                canHide = true;
                Log.i(TAG, "updateVideoView: "+isBottomMax);
                if (layoutPVideoView.getMarginRight() >= videoWidthPx) {
                    if (isBottomMax) {

                        layoutPVideo.setMarginTop(m);
                        float v = m - (allScrollY + allMinScrollY);
                        mVideoView.setAlpha(1.0f - v / swipePx2Dismiss);

                        if (v >= swipePx2Dismiss) {
                            setVisibility(INVISIBLE);
                            mVideoView.setAlpha(1f);
                        }
                    }
                }

                return;
            }//缩小视频右边距


            if (layoutPVideoView.getMarginRight() >= 0 && 0 > originY) {
                int value = layoutPVideoView.getMarginRight() + originY * 9;
//                if (0 > value) {
//                    value = 0;
//                }
                float percent = (videoWidthPx - value) / videoWidthPx;
                if (0 > percent) {
                    percent = 0.f;
                }
                int videoHeight = (int) (videoMinHeightPx * (1 - percent));
                int videoMTop = (int) (allMinScrollY * (1 - percent));
                layoutPVideo.setMarginTop((int) (allScrollY + videoMTop));
                layoutPVideo.setHeight((int) (videoHeightPx - videoHeight));
                layoutPVideoView.setMarginRight(value);
                return;
            }//放大视频右边距

            if (layoutPVideoView.getMarginRight() >= 0 && originY > 0) {

                return;
            }//最小化阶段


        }


        if (layoutPVideo.getMarginTop() <= 0 && originY < 0) {
            m = 0;
        }//最大化阶段

        float percent = (allScrollY - m) / allScrollY;
        if (0 > percent) {
            percent = 0;
            return;

        }


//        Log.i(TAG, "updateVideoView: " + mDetailView.getHeight() + " " + layoutPVideo.getMarginTop() + " " + percent + " " + m);

        int videoHeight = (int) (originalHeight - (originalHeight - videoHeightPx) * (1 - percent));
        int listHeight = (int) ((originListHeight) * (percent));
        layoutPVideo.setMarginTop(m);
        layoutPVideo.setHeight(videoHeight);
        layoutPList.setMarginBottom((int) (marginBottomPx * (1 - percent)));
        layoutPList.setHeight(listHeight);
        layoutPCoverView.getView().setAlpha((1 - percent));
        layoutPContainer.setMarginRight((int) (marginRLPx * (1 - percent)));
        layoutPContainer.setMarginLeft((int) (marginRLPx * (1 - percent)));
        int mr = (int) ((1f - percent) * marginPx); //VideoView右边和详情View 上方的margin
        layoutPVideo.setZ(mr / 2);//这个是Z轴的值，悬浮效果

    }

    private void dismissView() {


        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(layoutPVideo.getView(), "alpha", 1f, 0),
                ObjectAnimator.ofInt(layoutPVideo, "MarginTop", layoutPVideo.getMarginTop(), (int) (allScrollY + allMinScrollY + 100))
        );

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
                mVideoView.setAlpha(1f);

            }
        });
        set.setDuration(200).start();

        if (mCallback != null)
            mCallback.onVideoViewHide();
    }

    private void confirmState(float v, int dy) { //dy用于判断是否反方向滑动了
        //如果手指抬起时宽度达到一定值 或者 速度达到一定值 则改变状态
        if (nowStateScale == 1f) {
            if ((v > 15 && dy > 0)) {
                goMin();
            } else
                goMax();
        } else {
            if ((v > 15 && dy < 0)) {
                goMax();
            } else
                goMin();
        }
    }

    public void goMax() {
        if (nowStateScale == MIN_RATIO) {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = -1;
            params.height = -1;
            setLayoutParams(params);
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(

                ObjectAnimator.ofInt(layoutPVideo, "width", layoutPVideo.getWidth(), originalWidth),
                ObjectAnimator.ofInt(layoutPVideo, "height", layoutPVideo.getHeight(), originalHeight),
                ObjectAnimator.ofInt(layoutPVideo, "marginTop", layoutPVideo.getMarginTop(), 0),
                ObjectAnimator.ofInt(layoutPVideoView, "marginRight", layoutPVideoView.getMarginRight(), 0),

                ObjectAnimator.ofInt(layoutPContainer, "marginRight", layoutPContainer.getMarginRight(), 0),
                ObjectAnimator.ofInt(layoutPContainer, "marginLeft", layoutPContainer.getMarginLeft(), 0),

                ObjectAnimator.ofInt(layoutPList, "marginBottom", layoutPList.getMarginBottom(), 0),
                ObjectAnimator.ofInt(layoutPList, "height", layoutPList.getHeight(), originListHeight),

                ObjectAnimator.ofFloat(layoutPCoverView.getView(), "alpha", 1, 0)

        );
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setVisibility(VISIBLE);
            }
        });
        set.setDuration(200).start();
        nowStateScale = 1.0f;
        canHide = false;
    }

    public void goMaxFirst() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(

                ObjectAnimator.ofInt(layoutPVideo, "width", originalWidth, originalWidth),
                ObjectAnimator.ofInt(layoutPVideo, "height", ((int) videoMinHeightPx), originalHeight),
                ObjectAnimator.ofInt(layoutPVideo, "marginTop", (int)(allScrollY+allMinScrollY), 0),
                ObjectAnimator.ofInt(layoutPVideoView, "marginRight", 0, 0),

                ObjectAnimator.ofInt(layoutPContainer, "marginRight", marginRLPx, 0),
                ObjectAnimator.ofInt(layoutPContainer, "marginLeft", marginRLPx, 0),

                ObjectAnimator.ofInt(layoutPList, "marginBottom", ((int) marginBottomPx), 0),
                ObjectAnimator.ofInt(layoutPList, "height", 0, originListHeight),

                ObjectAnimator.ofFloat(layoutPCoverView.getView(), "alpha", 1, 0)

        );

        set.setDuration(200).start();
        nowStateScale = 1.0f;
        canHide = false;
    }

    public void goMin() {
        AnimatorSet set = new AnimatorSet();

        if (layoutPVideo.getHeight() < videoHeightPx) {
            goDetail();
            return;
        }
        set.playTogether(

//
                ObjectAnimator.ofInt(layoutPVideo, "height", layoutPVideo.getHeight(), (int) videoHeightPx),
                ObjectAnimator.ofInt(layoutPVideo, "marginTop", layoutPVideo.getMarginTop(), (int) allScrollY),
//

                ObjectAnimator.ofInt(layoutPContainer, "marginRight", layoutPContainer.getMarginRight(), marginRLPx),
                ObjectAnimator.ofInt(layoutPContainer, "marginLeft", layoutPContainer.getMarginLeft(), marginRLPx),

                ObjectAnimator.ofInt(layoutPList, "marginBottom", layoutPList.getMarginBottom(), (int) marginBottomPx),
                ObjectAnimator.ofInt(layoutPList, "height", layoutPList.getHeight(), 0),

                ObjectAnimator.ofFloat(layoutPCoverView.getView(), "alpha", layoutPCoverView.getView().getAlpha(), 1)
        );
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                goDetail();
            }
        });

        set.setDuration(200).start();
    }


    public void goDetail() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofInt(layoutPVideoView, "marginRight", layoutPVideoView.getMarginRight(), (int) (videoWidthPx)),

                ObjectAnimator.ofInt(layoutPVideo, "height", layoutPVideo.getHeight(), (int) videoMinHeightPx),
                ObjectAnimator.ofInt(layoutPVideo, "marginTop", layoutPVideo.getMarginTop(), (int) (allScrollY + allMinScrollY))
        );

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canHide = true;
                ViewGroup.LayoutParams p = getLayoutParams();
                p.width = -2;
                p.height = -2;
                setLayoutParams(p);
                nowStateScale = MIN_RATIO;
                nowStateScale = 0.0f;
            }
        });
        set.setInterpolator(new LinearInterpolator());
        set.setDuration(250).start();
    }


    //获取当前状态
    public float getNowStateScale() {
        return nowStateScale;
    }

    public void show() {
        setVisibility(VISIBLE);
        goMaxFirst();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

}