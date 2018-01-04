package com.iamlarry.snaphelper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;

import static com.iamlarry.snaphelper.SnapHelperAdapter.TYPE_IMAGE;
import static com.iamlarry.snaphelper.SnapHelperAdapter.TYPE_STATUS;
import static com.iamlarry.snaphelper.SnapHelperAdapter.TYPE_TEXT;
import static com.iamlarry.snaphelper.SnapHelperAdapter.TYPE_USER;

public class SnapHelperActivity extends AppCompatActivity implements SnapHelperAdapter.OnItemClickListener {
    private static final String TAG = "SnapHelperActivity";
    RecyclerView mRecyclerView;
    ArrayList<FeedCellInfo> mData;
    LinearLayoutManager mLayoutManager;
    BaseBlackSnapHelper mSnapHelper;
    private SnapHelperAdapter mAdapter;

    private View mEmptyHeaderView;

    private View mSearchUsersTabView;
    private PopupWindow mWelcomePopupWindow;
    private int mMaxNotScroll = 6;
    private final int N = 4;
    private int targetFeedId;

    private int mTargetY = 140;

    private int mTargetPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_helper);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initData();
        mAdapter = new SnapHelperAdapter(this, mData, this, mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //方法start
//        方法1：
        mSnapHelper = new BlackSnapHelper(new BaseBlackSnapHelper.SnapHelpListener() {

            @Override
            public boolean isTargetItem(View childView) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(childView);
                return holder instanceof SnapHelperAdapter.ImageViewHolder;
            }

            @Override
            public int getTargetY() {
                return mTargetY + DpPxUtil.dp2px(SnapHelperActivity.this, 56);
            }

            @Override
            public void onFindSnapView(View childView) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(childView);
                if (holder instanceof SnapHelperAdapter.ImageViewHolder) {
                    Log.i("larry123", ((SnapHelperAdapter.ImageViewHolder) holder).mItemView.getTag().toString());
                } else {
                    Log.e("larry123", "failed");
                }
            }

            @Override
            public int nextTargetPosition(int snapPosition, boolean reverseLayout, boolean forwardDirection) {

                int targetPosition = forwardDirection ? snapPosition + 4 : snapPosition;//这里forwardDirection=false不减是因为snapPosition已经在上面隐藏了一半。
                targetPosition = targetPosition < 0 ? 0 : targetPosition;
                targetPosition = (targetPosition > mData.size() - 1) ? (mData.size() - 1) : targetPosition;

                mTargetPosition = targetPosition;//mTargetPosition得到的是Adapter的位置

                mAdapter.updatePosition(mTargetPosition);
                mAdapter.notifyDataSetChanged();


                Log.e("larry123", "target:" + targetPosition + ",snapPosition:" + snapPosition);
                return targetPosition;
            }
        });
        mSnapHelper.attachToRecyclerView(mRecyclerView);

//        方法2：
//        mSnapHelper = new BaseBlackSnapHelper(new PagerSnapHelpListener() {
//            @Override
//            public int prevPosition(int position) {
//                return 0;
//            }
//
//            @Override
//            public int nextPosition(int position) {
//                return 0;
//            }
//        });
//        mSnapHelper.attachToRecyclerView(mRecyclerView);
//        方法3：事件拦截
        mRecyclerView.addOnItemTouchListener(new OnBlackItemTouchListener(this));
//        // 方法4:
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                switch (newState) {
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//
//                        break;
//                    case RecyclerView.SCROLL_STATE_DRAGGING:
//                        break;
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        mTargetPosition = getTargetPosition();
//                        mAdapter.updatePosition(mTargetPosition);
//                        mAdapter.notifyDataSetChanged();
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//            }
//        });
        //方法end

        mEmptyHeaderView = new

                View(SnapHelperActivity.this);
        mEmptyHeaderView.setPivotY(0);
        mAdapter.addHeaderView(mEmptyHeaderView);

        testAnim();

        findViewById(R.id.anim_test).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        testAnim();
                    }
                });


        mSearchUsersTabView =

                findViewById(R.id.yh);
        mSearchUsersTabView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                showSearchUsersPopup();
            }
        });

        findViewById(R.id.gc).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            PackageManager manager = getPackageManager();
                            PackageInfo info = manager.getPackageInfo("com.tencent.qqmusic", 0);

                            Log.w(TAG, info.versionName + "," + info.versionCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private int[] topArray = {-660, 0, 200, 630, 800, 1400, 1920};
    private int index = 0;

    private void testAnim() {
        index = (int) (Math.random() * (topArray.length - 1));

        //-660~1920
        int top = (int) (Math.random() * 1920 - 660);

        Rect mFirstViewVideoLocation = new Rect(0, topArray[index], 1080, 660);

        Log.w(TAG, "top=" + topArray[index]);

        //方法开始
        //方法3：requestLayout
        int defaultHeight = mFirstViewVideoLocation.top <= 0 ? 0 : mFirstViewVideoLocation.top;
        mEmptyHeaderView.getLayoutParams().height = defaultHeight;
        mEmptyHeaderView.requestLayout();
        if (defaultHeight != mTargetY) {
            ValueAnimator animator = ValueAnimator.ofInt(defaultHeight, mTargetY);
            animator.setDuration(300);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int curValue = (int) animation.getAnimatedValue();
                    mEmptyHeaderView.getLayoutParams().height = curValue;
                    mEmptyHeaderView.requestLayout();
                }
            });
            animator.start();
        }


        //方法1：ObjectAnimator,失败没有反应
//                int getTargetY = 630;
//                float scaleY;
//                int defaultHeight = mFirstViewVideoLocation.top <= 0 ? 1 : mFirstViewVideoLocation.top;
//                mEmptyHeaderView.getLayoutParams().height = defaultHeight;
//                mEmptyHeaderView.requestLayout();
//                scaleY = getTargetY * 1f / defaultHeight;
//
//                ObjectAnimator scaleYObjectAnim = ObjectAnimator.ofFloat(mEmptyHeaderView, "scaleY", 1f, scaleY);
//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.play(scaleYObjectAnim);
//                animatorSet.setDuration(300);
//                animatorSet.setInterpolator(new AccelerateInterpolator());
//                animatorSet.start();


        //方法2：ScrollBy,失败，mRecyclerView.scrollBy(0, scrollByY);没反应
//                final int getTargetY = 630;
//
//                final int initHeaderHeight = Math.max(mFirstViewVideoLocation.top, getTargetY);
//                mEmptyHeaderView.getLayoutParams().height = initHeaderHeight;
//                mEmptyHeaderView.requestLayout();
//
//                final int itemLocationY = mFirstViewVideoLocation.top <= 0 ? 0 : mFirstViewVideoLocation.top;
//                final int scrollByY = itemLocationY - initHeaderHeight;
//
//                final int smoothScrollByY = getTargetY - itemLocationY;
//
////                mRecyclerView.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
//                        if (scrollByY != 0) {
//                            mRecyclerView.scrollBy(0, scrollByY);
//                        }
//
////                    }
////                }, 1000);
//
//                mRecyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (smoothScrollByY != 0) {
//                            mRecyclerView.smoothScrollBy(0, -smoothScrollByY);
//                        }
//                    }
//                }, 100);
//
//
//                Log.e(TAG, "scrollByY=" + scrollByY + ",smoothScrollByY=" + smoothScrollByY
//                        + ",initHeaderHeight=" + initHeaderHeight + ",itemLocationY=" + itemLocationY);


        //方法结束


    }

    int[] imgs = new int[]{R.drawable.jdzz, R.drawable.ccdzz, R.drawable.dfh,
            R.drawable.dlzs, R.drawable.sgkptt, R.drawable.ttxss, R.drawable.zmq, R.drawable.zzhx};

    private void initData() {
        mData = new ArrayList<>();
        FeedCellInfo header = new FeedCellInfo();
        header.feedId = -1;
        header.id = -1;
        mData.add(header);

        int imgIndex = 0;
        for (int i = 0; i < 120; i++) {
            FeedCellInfo info = new FeedCellInfo();
            info.feedId = i / N;
            info.id = i;
            String com = "id:" + i + ",feedId:" + (i / N);
            if (i % N == 0) {
                info.type = TYPE_USER;
                info.text = "User:" + com;
            } else if (i % N == 1) {
                info.type = TYPE_IMAGE;
                info.text = "pic:" + com;
                info.picId = imgs[imgIndex];
                imgIndex++;
                if (imgIndex > imgs.length - 1) {
                    imgIndex = 0;
                }
            } else if (i % N == 2) {
                String text = com;
                text += "分布式系统中的节点通信存在两种模型：共享内存（Shared memory）和消息传递（Messages passing）。基于消息传递通信模型的分布式系统，不可避免的会发生以下错误：进程可能会慢、被杀死或者重启，消息可能会延迟、丢失、重复，在基础Paxos场景中，先不考虑可能出现消息篡改即拜占庭错误的情况。Paxos算法解决的问题是在一个可能发生上述异常的分布式系统中如何就某个值达成一致，保证不论发生以上任何异常，都不会破坏决议的一致性。一个典型的场景是，在一个分布式数据库系统中，如果各节点的初始状态一致，每个节点都执行相同的操作序列，那么他们最后能得到一个一致的状态。为保证每个节点执行相同的命令序列，需要在每一条指令上执行一个“一致性算法”以保证每个节点看到的指令一致。一个通用的一致性算法可以应用在许多场景中，是分布式计算中的重要问题。因此从20世纪80年代起对于一致性算法的研究就没有停止过。";
                int end = (int) (Math.random() * (text.length() - 1));
                info.type = TYPE_TEXT;
                info.text = text.substring(0, end);
            } else if (i % N == 3) {
                info.type = TYPE_STATUS;
                info.text = "Status:" + com;
            }
            mData.add(info);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

//        mRecyclerView.smoothScrollBy(0, mRecyclerView.computeVerticalScrollExtent() / 2);
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Log.d(TAG, rect.toString());

        view.getLocalVisibleRect(rect);
        Log.v(TAG, rect.toString());

        int local[] = new int[2];
        view.getLocationOnScreen(local);

        Log.w(TAG, "x=" + local[0] + ",y=" + local[1]);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        view.getLocationInWindow(local);
        Log.e(TAG, String.format("x=%d,y=%d,w=%d,h=%d,sw=%d,sh=%d,cx=%d,cy=%d",
                local[0], local[1], view.getWidth(), view.getHeight(), point.x, point.y,
                local[0], (point.y - view.getHeight()) / 2
        ));

    }

    interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private LinearLayout autoMoveLayout;

    private void showSearchUsersPopup() {
        if (mSearchUsersTabView != null) {

            if (mWelcomePopupWindow == null) {
                mWelcomePopupWindow = new PopupWindow(this);
                View layout = LayoutInflater.from(this).inflate(R.layout.search_user_popup, null);
                autoMoveLayout = layout.findViewById(R.id.arrow_auto_move_layout);

                mWelcomePopupWindow.setContentView(layout);
                mWelcomePopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                //PopupWindow对象设置高度
                mWelcomePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                //PopupWindow对象设置可以触发点击事件
                mWelcomePopupWindow.setTouchable(true);
                mWelcomePopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            }
            if (mWelcomePopupWindow.isShowing()) {
                mWelcomePopupWindow.dismiss();
            }

            Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);

            mWelcomePopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = mWelcomePopupWindow.getContentView().getMeasuredWidth();
            int arrowRightW = (int) (2f / 5 * width);
            int targetRightW = (int) (point.x * 1.4f / mMaxNotScroll);
            int xOff;
            if (arrowRightW >= targetRightW) {
                xOff = 0;
            } else {
                xOff = targetRightW - arrowRightW;
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) autoMoveLayout.getLayoutParams();
            params.setMargins(0, 0, xOff, 0);

            PopupWindowCompat.showAsDropDown(mWelcomePopupWindow, mSearchUsersTabView, 0, 0,
                    Gravity.BOTTOM | Gravity.RIGHT);

            mHandler.sendEmptyMessageDelayed(MSG_DISMISS_SEARCH_USERS_POPUP, 6000);
        } else {
            dismissSearchUsersPopup();
        }
    }

    private static int MSG_DISMISS_SEARCH_USERS_POPUP = 4821;
    private static int MSG_SHOW_SEARCH_USERS_POPUP = 4822;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg == null) {
                return;
            }
            if (msg.what == MSG_DISMISS_SEARCH_USERS_POPUP) {
                dismissSearchUsersPopup();
            } else if (msg.what == MSG_SHOW_SEARCH_USERS_POPUP) {
                showSearchUsersPopup();
            }

        }
    };

    private void dismissSearchUsersPopup() {
        if (mWelcomePopupWindow != null && mWelcomePopupWindow.isShowing()) {
            mWelcomePopupWindow.dismiss();
        }
        if (mHandler.hasMessages(MSG_DISMISS_SEARCH_USERS_POPUP)) {
            mHandler.removeMessages(MSG_DISMISS_SEARCH_USERS_POPUP);
        }
    }

    private void removeShowUserPopup() {
        if (mHandler.hasMessages(MSG_SHOW_SEARCH_USERS_POPUP)) {
            mHandler.removeMessages(MSG_SHOW_SEARCH_USERS_POPUP);
        }
    }

    private void isViewShow() {

    }

    protected int getTargetPosition() {

//        SnapHelperAdapter.BaseHolder targetViewHolder = null;
//        int oldTargetViewHolderY = 0;
//        View childView = null;
//        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
//            final RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
//            if (holder instanceof SnapHelperAdapter.ImageViewHolder) {
//                int[] newTargetY = new int[2];
//                ((SnapHelperAdapter.ImageViewHolder) holder).mItemView.getLocationOnScreen(newTargetY);
//                if (targetViewHolder == null) {
//                    targetViewHolder = (SnapHelperAdapter.BaseHolder) holder;
//                    oldTargetViewHolderY = newTargetY[1];
//                    childView = mRecyclerView.getChildAt(i);
//                } else if (Math.abs(mTargetY - getFixDistance(newTargetY[1])) < Math.abs(mTargetY - getFixDistance(oldTargetViewHolderY))) {
//                    oldTargetViewHolderY = newTargetY[1];
//                    targetViewHolder = (SnapHelperAdapter.BaseHolder) holder;
//                    childView = mRecyclerView.getChildAt(i);
//                }
//            }
//        }
//        if (childView == null) {
//            return 0;
//        }
        View childView = mSnapHelper.findSnapView(mLayoutManager);
        if (childView == null) {
            return 0;
        }
        return mRecyclerView.getChildAdapterPosition(childView);
    }

    //减去状用户栏的高度
    private int getFixDistance(int y) {
        return y > 0 ? y - 100 : y;
    }

    private class OnBlackItemTouchListener implements RecyclerView.OnItemTouchListener {

        private int mLastDownY;
        //该值记录了最小滑动距离
        private int touchSlop;
        private int nextTargetPosition = -1;

        public OnBlackItemTouchListener(Context context) {
            touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastDownY = y;
                    nextTargetPosition = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (nextTargetPosition == -1) {
                        if (y - mLastDownY > 0) {
                            //手指往下，RecycleView上滑时，
                            nextTargetPosition = mSnapHelper.findTargetSnapPosition(mLayoutManager, 0, -1);
                        } else {
                            nextTargetPosition = mSnapHelper.findTargetSnapPosition(mLayoutManager, 0, 1);
                        }
                    }

                    View nextTargetView = mLayoutManager.findViewByPosition(nextTargetPosition);
                    if (nextTargetView == null) {
                        return false;
                    }
                    int[] distance = mSnapHelper.calculateDistanceToFinalSnap(mLayoutManager, nextTargetView);

                    Log.d("larry123", (y - mLastDownY) + "," + nextTargetPosition + ",d:" + distance[1]);
                    boolean intercept = distance[1] <= touchSlop;
                    return intercept;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

}
