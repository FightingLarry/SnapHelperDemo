package com.iamlarry.snaphelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by chenzhimao on 17-7-6.
 */

public class SnapHelperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    LayoutInflater mInflater;
    ArrayList<FeedCellInfo> mData;

    public static final int TYPE_HEADER = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_USER = 3;
    public static final int TYPE_STATUS = 4;
    public static final int TYPE_TEXT = 5;

    private LinearLayout mHeaderViewGroup;

    private OnItemClickListener onItemClickListener;


    public SnapHelperAdapter(Context context, ArrayList<FeedCellInfo> data, OnItemClickListener onItemClickListener) {
        mInflater = LayoutInflater.from(context);
        mData = data;
        this.onItemClickListener = onItemClickListener;

        mHeaderViewGroup = new LinearLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderViewGroup.setLayoutParams(params);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (mData.get(position).type == TYPE_USER) {
            return TYPE_USER;
        } else if (mData.get(position).type == TYPE_STATUS) {
            return TYPE_STATUS;
        } else if (mData.get(position).type == TYPE_TEXT) {
            return TYPE_TEXT;
        } else {
            return TYPE_IMAGE;
        }
    }

    public void addHeaderView(View headerView) {
        mHeaderViewGroup.removeAllViews();
        mHeaderViewGroup.addView(headerView);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderViewGroup);
        } else if (viewType == TYPE_USER) {
            View view = mInflater.inflate(R.layout.item_user_layout, parent, false);
            return new ImageViewHolder(view);
        } else if (viewType == TYPE_TEXT) {
            View view = mInflater.inflate(R.layout.item_text_layout, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == TYPE_STATUS) {
            View view = mInflater.inflate(R.layout.item_status_layout, parent, false);
            return new StatusViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.gallery_item_layout, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) != TYPE_HEADER) {
            FeedCellInfo info = mData.get(position);
            if (getItemViewType(position) == TYPE_USER) {
                ImageViewHolder viewHoler = (ImageViewHolder) holder;
                viewHoler.mTextView.setText(info.text);
            } else if (getItemViewType(position) == TYPE_STATUS) {
                StatusViewHolder viewHolder = (StatusViewHolder) holder;
                viewHolder.mText.setText(info.text);
            } else if (getItemViewType(position) == TYPE_IMAGE) {
                ImageViewHolder viewHoler = (ImageViewHolder) holder;
                viewHoler.mImageView.setImageResource(info.picId);
                viewHoler.mTextView.setText(info.text);
                viewHoler.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(v, position);
                        }
                    }
                });
            } else if (getItemViewType(position) == TYPE_TEXT) {
                final TextViewHolder viewHolder = (TextViewHolder) holder;
                viewHolder.mText.setText(info.text);
                viewHolder.mText.setMaxLines(2);
                viewHolder.mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.mText.setMaxLines(Integer.MAX_VALUE);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public View mItemView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mTextView = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public View mItemView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
        }
    }

    class StatusViewHolder extends RecyclerView.ViewHolder {
        public View mItemView;
        public TextView mText;

        public StatusViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mText = (TextView) itemView.findViewById(R.id.text);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        public View mItemView;
        public TextView mText;
        public Button mMore;

        public TextViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mText = (TextView) itemView.findViewById(R.id.text);
            this.mMore = (Button) itemView.findViewById(R.id.more);
        }
    }
}
