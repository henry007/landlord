
package com.hurray.landlord.adapter;

import com.hurray.landlord.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class OnlineGoodsImageAdapter extends BaseAdapter {
    private Context mContext;
    // private List<Integer> mGoodsIds;

    private List<Drawable> GoodsIds;

    private String[] mGoodsName = null;

    private String[] mStoreGoodsMoney = null;

    public int mCurrentPos = -1;

    public OnlineGoodsImageAdapter(Context context, List<Drawable> storeGoodsIds,
            String[] mStoreGoodsName, String[] mStoreGoodsMoney) {
        mContext = context;

        this.GoodsIds = storeGoodsIds;

        this.mGoodsName = new String[mStoreGoodsName.length];

        this.mStoreGoodsMoney = new String[mStoreGoodsMoney.length];

        this.mGoodsName = mStoreGoodsName;

        this.mStoreGoodsMoney = mStoreGoodsMoney;
    }

    @Override
    public int getCount() {
        return GoodsIds.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (holder == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.goods_internal_layout, null);

            holder.iv = (ImageView) convertView.findViewById(R.id.img_goods);

            holder.iv.setAdjustViewBounds(false);

            holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.iv.setPadding(2, 2, 2, 2);

            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);

            holder.mTvMoney = (TextView) convertView
                    .findViewById(R.id.tv_money);

            holder.orangeBg = (LinearLayout) convertView
                    .findViewById(R.id.orange_bg);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv.setImageDrawable(GoodsIds.get(position));

        holder.mTvName.setText(mGoodsName[position]);
        
        holder.mTvMoney.setVisibility(View.GONE);

       // holder.mTvMoney.setText(mStoreGoodsMoney[position]);

        if (mCurrentPos == position) {

//            holder.orangeBg.setVisibility(View.VISIBLE);

            holder.iv.setBackgroundResource(R.drawable.white_corner);

        } else {

//            holder.orangeBg.setVisibility(View.INVISIBLE);

            holder.iv.setBackgroundResource(0);

        }

        return convertView;
    }

    public void setmCurrentPos(int mCurrentPos) {
        this.mCurrentPos = mCurrentPos;
    }

    public final class ViewHolder {
        public ImageView iv;
        public TextView mTvName;
        public TextView mTvMoney;
        public LinearLayout orangeBg;
    }

}
