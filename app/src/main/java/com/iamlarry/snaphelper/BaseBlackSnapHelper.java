package com.iamlarry.snaphelper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.SmoothScroller;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * @author larryycliu on 2018/1/1.
 */

public abstract class BaseBlackSnapHelper extends RecyclerView.OnFlingListener {

    static final float MILLISECONDS_PER_INCH = 100f;

    RecyclerView mRecyclerView;

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }

    /**
     * Attaches the {@link android.support.v7.widget.SnapHelper} to the provided RecyclerView, by calling
     * {@link RecyclerView#setOnFlingListener(RecyclerView.OnFlingListener)}.
     * You can call this method with {@code null} to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     *                     {@code null} if you want to remove android.support.v7.widget.SnapHelper from the current
     *                     RecyclerView.
     * @throws IllegalArgumentException if there is already a {@link RecyclerView.OnFlingListener}
     *                                  attached to the provided {@link RecyclerView}.
     */
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
        }
    }

    /**
     * Called when an instance of a {@link RecyclerView} is attached.
     */
    private void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.setOnFlingListener(this);
    }

    /**
     * Called when the instance of a {@link RecyclerView} is detached.
     */
    private void destroyCallbacks() {
        mRecyclerView.setOnFlingListener(null);
    }

    /**
     * Helper method to facilitate for snapping triggered by a fling.
     *
     * @param layoutManager The {@link LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param velocityX     Fling velocity on the horizontal axis.
     * @param velocityY     Fling velocity on the vertical axis.
     * @return true if it is handled, false otherwise.
     */
    private boolean snapFromFling(@NonNull LayoutManager layoutManager, int velocityX,
                                  int velocityY) {
        if (!(layoutManager instanceof SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        SmoothScroller smoothScroller = createScroller(layoutManager);
        if (smoothScroller == null) {
            return false;
        }

        int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        smoothScroller.setTargetPosition(targetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
        return true;
    }

    /**
     * Creates a scroller to be used in the snapping implementation.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @return a {@link SmoothScroller} which will handle the scrolling.
     */
    @Nullable
    protected SmoothScroller createScroller(LayoutManager layoutManager) {
        return createSnapScroller(layoutManager);
    }

    /**
     * Creates a scroller to be used in the snapping implementation.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @return a {@link LinearSmoothScroller} which will handle the scrolling.
     * @deprecated use {@link #createScroller(LayoutManager)} instead.
     */
    @Nullable
    @Deprecated
    protected LinearSmoothScroller createSnapScroller(LayoutManager layoutManager) {
        if (!(layoutManager instanceof SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(),
                        targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    /**
     * Override this method to snap to a particular point within the target view or the container
     * view on any axis.
     * <p>
     * This method is called when the {@link android.support.v7.widget.SnapHelper} has intercepted a fling and it needs
     * to know the exact distance required to scroll by in order to snap to the target view.
     *
     * @param layoutManager the {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}
     * @param targetView    the target view that is chosen as the view to snap
     * @return the output coordinates the put the result into. out[0] is the distance
     * on horizontal axis and out[1] is the distance on vertical axis.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public abstract int[] calculateDistanceToFinalSnap(@NonNull LayoutManager layoutManager,
                                                       @NonNull View targetView);

    /**
     * Override this method to provide a particular target view for snapping.
     * <p>
     * This method is called when the {@link android.support.v7.widget.SnapHelper} is ready to start snapping and requires
     * a target view to snap to. It will be explicitly called when the scroll state becomes idle
     * after a scroll. It will also be called when the {@link android.support.v7.widget.SnapHelper} is preparing to snap
     * after a fling and requires a reference view from the current set of child views.
     * <p>
     * If this method returns {@code null}, android.support.v7.widget.SnapHelper will not snap to any view.
     *
     * @param layoutManager the {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}
     * @return the target view to which to snap on fling or end of scroll
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public abstract View findSnapView(LayoutManager layoutManager);

    /**
     * Override to provide a particular adapter target position for snapping.
     *
     * @param layoutManager the {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}
     * @param velocityX     fling velocity on the horizontal axis
     * @param velocityY     fling velocity on the vertical axis
     * @return the target adapter position to you want to snap or {@link RecyclerView#NO_POSITION}
     * if no snapping should happen
     */
    public abstract int findTargetSnapPosition(LayoutManager layoutManager, int velocityX,
                                               int velocityY);


    public interface SnapHelpListener {
        /**
         * 是目标item，可以snap的item
         *
         * @param childView
         * @return
         */
        boolean isTargetItem(View childView);

        /**
         * 目标y左边，居中居上等y坐标
         *
         * @return
         */
        int getTargetY();

        void onFindSnapView(View childView);

        /**
         * @param snapPosition     Adapter的position，包含了Header等ViewType
         * @param reverseLayout
         * @param forwardDirection 方向，竖直时，true为向下
         * @return
         */
        int nextTargetPosition(int snapPosition, boolean reverseLayout, boolean forwardDirection);

    }

}
