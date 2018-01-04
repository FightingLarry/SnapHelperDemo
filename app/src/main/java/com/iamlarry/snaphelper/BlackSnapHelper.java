package com.iamlarry.snaphelper;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author larryycliu on 2018/1/3.
 */

public class BlackSnapHelper extends BaseBlackSnapHelper {
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    private SnapHelpListener mSnapHelpListener;

    public BlackSnapHelper(SnapHelpListener snapHelpListener) {
        this.mSnapHelpListener = snapHelpListener;
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToTarget(layoutManager, targetView);
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToTarget(layoutManager, targetView);
        } else {
            out[1] = 0;
        }
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findSnapViewByListener(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findSnapViewByListener(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }


    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (mSnapHelpListener == null) {
            return RecyclerView.NO_POSITION;
        }
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View mSnapView = findSnapView(layoutManager);
        if (mSnapView == null) {
            return RecyclerView.NO_POSITION;
        }
        final int snapPosition = layoutManager.getPosition(mSnapView);
        if (snapPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }
        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0;
        } else {
            forwardDirection = velocityY > 0;
        }
        //反向布局
        boolean reverseLayout = false;
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                    (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }
        return mSnapHelpListener.nextTargetPosition(snapPosition, reverseLayout, forwardDirection);
    }


    private int distanceToTarget(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        if (mSnapHelpListener == null) {
            return 0;
        }
        int childCenter;
        if (layoutManager.canScrollHorizontally()) {
            childCenter = getHorizontalHelper(layoutManager).getDecoratedStart(targetView);
        } else {
            childCenter = getVerticalHelper(layoutManager).getDecoratedStart(targetView);
        }
        int containerCenter = mSnapHelpListener.getTargetY();
        return childCenter - containerCenter;
    }


    private View findSnapViewByListener(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
        if (mSnapHelpListener == null) {
            return null;
        }
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }
        View closestChild = null;
        final int target = mSnapHelpListener.getTargetY();
        int absClosest = Integer.MAX_VALUE;
        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            if (mSnapHelpListener.isTargetItem(child)) {
                int childCenter = helper.getDecoratedStart(child) + (helper.getDecoratedMeasurement(child) / 2);
                int absDistance = Math.abs(childCenter - target);
                /** if child center is closer than previous closest, set it as closest  **/
                if (absDistance < absClosest) {
                    absClosest = absDistance;
                    closestChild = child;
                }
            }
        }
        mSnapHelpListener.onFindSnapView(closestChild);
        return closestChild;
    }


    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }


}
