package com.iamlarry.snaphelper;

/**
 * @author larryycliu on 2018/1/1.
 */

public class FeedCellInfo {

    public int feedId;

    public int id;

    public String text;

    public int picId;

    public int type;

    public boolean canCenter() {
        return type == SnapHelperAdapter.TYPE_IMAGE;
    }

    @Override
    public String toString() {
        return "id:" + id + ",feedId:" + feedId + ",text:" + text;

    }
}
