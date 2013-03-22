
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

public class OnlineStoreImageAdapter extends BaseAdapter {

    private Context mContext;

    // private List<Integer> storeGoodsIds;

//    private List<BitmapDrawable> storeGoodsIds;
    
    private List<Drawable> storeGoodsIds;

    private String[] mStoreGoodsName = null;

    private String[] mStoreGoodsMoney = null;
    // private ViewHolder holder = null;
    public int mCurrentPos = -1;

    public OnlineStoreImageAdapter(Context context, List<Drawable> storeGoodsIds,
            String[] mStoreGoodsName, String[] mStoreGoodsMoney) {

        mContext = context;

        this.storeGoodsIds = storeGoodsIds;

        this.mStoreGoodsName = new String[mStoreGoodsName.length];

        this.mStoreGoodsMoney = new String[mStoreGoodsMoney.length];
        
        this.mStoreGoodsName = mStoreGoodsName;
        
        this.mStoreGoodsMoney =  mStoreGoodsMoney;
        
    }

    @Override
    public int getCount() {
        return storeGoodsIds.size();
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

            convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_internal_layout,
                    null);

            holder.iv = (ImageView) convertView.findViewById(R.id.img_goods);

            holder.iv.setAdjustViewBounds(false);

            holder.iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.iv.setPadding(2, 2, 2, 2);

            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);

            holder.mTvMoney = (TextView) convertView.findViewById(R.id.tv_money);

            holder.orangeBg = (LinearLayout) convertView.findViewById(R.id.orange_bg);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv.setImageDrawable(storeGoodsIds.get(position));
        
        holder.mTvName.setText(mStoreGoodsName[position]);

        holder.mTvMoney.setText(mStoreGoodsMoney[position]);
        
        if (mCurrentPos == position) {

//            holder.orangeBg.setVisibility(View.VISIBLE);

            holder.iv.setBackgroundResource(R.drawable.white_corner);

        } else {

//            holder.orangeBg.setVisibility(View.INVISIBLE);

            holder.iv.setBackgroundResource(0);

        }

        // holder.orangeBg.setVisibility(View.INVISIBLE);
        return convertView;
    }

   
    //
    // private Integer[] mStoreGoodsIdsMaleClothes = {
    // R.drawable.nan_store_clothes1, R.drawable.nan_store_clothes2,
    // R.drawable.nan_store_clothes3
    // };
    //
    // private Integer[] mStoreGoodsIdsFemaleHair = {
    // R.drawable.nv_store_hair1, R.drawable.nv_store_hair2,
    // R.drawable.nv_store_hair3
    // };
    //
    // private Integer[] mStoreGoodsIdsFemaleClothes = {
    // R.drawable.nv_store_clothes1, R.drawable.nv_store_clothes2,
    // R.drawable.nv_store_clothes3
    // };

    public final class ViewHolder {
        public ImageView iv;
        public TextView mTvName;
        public TextView mTvMoney;
        public LinearLayout orangeBg;
    }

    public void setmCurrentPos(int mCurrentPos) {
        this.mCurrentPos = mCurrentPos;
    }

}
