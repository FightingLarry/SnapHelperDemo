package com.czm.snaphelperdemo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author larryycliu on 2018/1/1.
 */

public class BlackPagerSnapHelper extends PagerSnapHelper {

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mVerticalHelper;
    @Nullable
    private OrientationHelper mHorizontalHelper;

    private PagerSnapHelpListener mSnapHelpListener;

    public BlackPagerSnapHelper(PagerSnapHelpListener listener) {
        mSnapHelpListener = listener;
    }


    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager));
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    private View findCenterView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {

        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int center;
        if (layoutManager.getClipToPadding()) {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            center = helper.getEnd() / 2;
        }
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);

            if (mSnapHelpListener != null) {
                int position = layoutManager.getPosition(child);
//                if (mSnapHelpListener.nextPosition(position)) {
//                    int childCenter = helper.getDecoratedStart(child)
//                            + (helper.getDecoratedMeasurement(child) / 2);
//                    int absDistance = Math.abs(childCenter - center);
//
//                    /** if child center is closer than previous closest, set it as closest  **/
//                    if (absDistance < absClosest) {
//                        absClosest = absDistance;
//                        closestChild = child;
//                    }
//                }
            } else {
                int childCenter = helper.getDecoratedStart(child)
                        + (helper.getDecoratedMeasurement(child) / 2);
                int absDistance = Math.abs(childCenter - center);

                /** if child center is closer than previous closest, set it as closest  **/
                if (absDistance < absClosest) {
                    absClosest = absDistance;
                    closestChild = child;
                }
            }
        }
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
