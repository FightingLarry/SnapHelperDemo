package com.czm.snaphelperdemo;

/**
 * @author larryycliu on 2018/1/1.
 */

public class FeedCellInfo {

    public String text;

    public int picId;

    public int type;

    public boolean canCenter() {
        return type == SnapHelperAdapter.TYPE_IMAGE;
    }

}
