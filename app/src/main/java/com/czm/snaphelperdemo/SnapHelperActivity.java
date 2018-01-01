package com.czm.snaphelperdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import static com.czm.snaphelperdemo.SnapHelperAdapter.TYPE_IMAGE;
import static com.czm.snaphelperdemo.SnapHelperAdapter.TYPE_STATUS;
import static com.czm.snaphelperdemo.SnapHelperAdapter.TYPE_TEXT;
import static com.czm.snaphelperdemo.SnapHelperAdapter.TYPE_USER;

public class SnapHelperActivity extends AppCompatActivity implements SnapHelperAdapter.OnItemClickListener {
    private static final String TAG = "SnapHelperActivity";
    RecyclerView mRecyclerView;
    ArrayList<FeedCellInfo> mData;
    LinearLayoutManager mLayoutManager;
    SnapHelper mSnapHelper;
    private SnapHelperAdapter mAdapter;

    private View mEmptyHeaderView;

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
        mAdapter = new SnapHelperAdapter(this, mData, this);
        mRecyclerView.setAdapter(mAdapter);
//        方法1：
//        mSnapHelper = new BlackLinearSnapHelper(new SnapHelpListener() {
//            @Override
//            public boolean canCenter(int position) {
//                return mData.get(position).canCenter();
//            }
//        });
//        方法2：
//        mSnapHelper = new BlackPagerSnapHelper(new PagerSnapHelpListener() {
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
        mEmptyHeaderView = new View(SnapHelperActivity.this);
        mEmptyHeaderView.setPivotY(0);
        mAdapter.addHeaderView(mEmptyHeaderView);

        testAnim();
        findViewById(R.id.anim_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAnim();
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

        int targetY = 630;

        //方法开始
        //方法3：requestLayout
        int defaultHeight = mFirstViewVideoLocation.top <= 0 ? 0 : mFirstViewVideoLocation.top;
        mEmptyHeaderView.getLayoutParams().height = defaultHeight;
        mEmptyHeaderView.requestLayout();
        if (defaultHeight != targetY) {
            ValueAnimator animator = ValueAnimator.ofInt(defaultHeight, targetY);
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
//                int targetY = 630;
//                float scaleY;
//                int defaultHeight = mFirstViewVideoLocation.top <= 0 ? 1 : mFirstViewVideoLocation.top;
//                mEmptyHeaderView.getLayoutParams().height = defaultHeight;
//                mEmptyHeaderView.requestLayout();
//                scaleY = targetY * 1f / defaultHeight;
//
//                ObjectAnimator scaleYObjectAnim = ObjectAnimator.ofFloat(mEmptyHeaderView, "scaleY", 1f, scaleY);
//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.play(scaleYObjectAnim);
//                animatorSet.setDuration(300);
//                animatorSet.setInterpolator(new AccelerateInterpolator());
//                animatorSet.start();


        //方法2：ScrollBy,失败，mRecyclerView.scrollBy(0, scrollByY);没反应
//                final int targetY = 630;
//
//                final int initHeaderHeight = Math.max(mFirstViewVideoLocation.top, targetY);
//                mEmptyHeaderView.getLayoutParams().height = initHeaderHeight;
//                mEmptyHeaderView.requestLayout();
//
//                final int itemLocationY = mFirstViewVideoLocation.top <= 0 ? 0 : mFirstViewVideoLocation.top;
//                final int scrollByY = itemLocationY - initHeaderHeight;
//
//                final int smoothScrollByY = targetY - itemLocationY;
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
        mData.add(header);

        int N = 4;
        int imgIndex = 0;
        for (int i = 0; i < 120; i++) {
            FeedCellInfo info = new FeedCellInfo();
            if (i % N == 0) {
                info.type = TYPE_USER;
                info.text = "User:" + (i / N);
            } else if (i % N == 1) {
                info.type = TYPE_IMAGE;
                info.text = "pic:" + i / N;
                info.picId = imgs[imgIndex];
                imgIndex++;
                if (imgIndex > imgs.length - 1) {
                    imgIndex = 0;
                }
            } else if (i % N == 2) {
                String text = "分布式系统中的节点通信存在两种模型：共享内存（Shared memory）和消息传递（Messages passing）。基于消息传递通信模型的分布式系统，不可避免的会发生以下错误：进程可能会慢、被杀死或者重启，消息可能会延迟、丢失、重复，在基础Paxos场景中，先不考虑可能出现消息篡改即拜占庭错误的情况。Paxos算法解决的问题是在一个可能发生上述异常的分布式系统中如何就某个值达成一致，保证不论发生以上任何异常，都不会破坏决议的一致性。一个典型的场景是，在一个分布式数据库系统中，如果各节点的初始状态一致，每个节点都执行相同的操作序列，那么他们最后能得到一个一致的状态。为保证每个节点执行相同的命令序列，需要在每一条指令上执行一个“一致性算法”以保证每个节点看到的指令一致。一个通用的一致性算法可以应用在许多场景中，是分布式计算中的重要问题。因此从20世纪80年代起对于一致性算法的研究就没有停止过。";
                int end = (int) (Math.random() * (text.length() - 1));
                info.type = TYPE_TEXT;
                info.text = text.substring(0, end);
            } else if (i % N == 3) {
                info.type = TYPE_STATUS;
                info.text = "Status:" + (i / N);
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

    private class OnBlackItemTouchListener implements RecyclerView.OnItemTouchListener {

        private int mLastDownX, mLastDownY;
        //该值记录了最小滑动距离
        private int touchSlop;
        private boolean isMove = false;

        public OnBlackItemTouchListener(Context context) {
            touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int x = (int) e.getX();
            int y = (int) e.getY();
            Log.w(TAG, "x=" + x + ",y=" + y + ",action=" + e.getAction());
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastDownX = x;
                    mLastDownY = y;
                    isMove = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(x - mLastDownX) > touchSlop || Math.abs(y - mLastDownY) > touchSlop) {
                        isMove = true;
                    }

                    int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (y - mLastDownY > 0) {
                        if (position + 1 < mAdapter.getItemCount()) {
                            mRecyclerView.smoothScrollBy(0, 200);
//                            mLayoutManager.scrollToPositionWithOffset(position + 1, 0);
                        }
                    } else if (y - mLastDownY < 0) {
                        if (position - 1 >= 0) {
                            mRecyclerView.smoothScrollBy(0, -200);
//                            mLayoutManager.scrollToPositionWithOffset(position - 1, 0);
                        }
                    }
                    Log.i(TAG, "position=" + position);
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return isMove;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


}
