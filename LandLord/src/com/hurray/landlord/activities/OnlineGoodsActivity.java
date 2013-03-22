
package com.hurray.landlord.activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OnlineGoodsImageAdapter;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.entity.OnlineHomeInfo;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.SysCommonPush;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.goods.DressupItemReq;
import com.hurray.lordserver.protocol.message.goods.ShowBagReq;
import com.hurray.lordserver.protocol.message.goods.ShowBagResp;
import com.hurray.lordserver.protocol.message.goods.ShowBagResp.BagInfo;

public class OnlineGoodsActivity extends BaseNetActivity {
    private Button mBtnSave;
    private GridView mGridView;
    private OnlineGoodsImageAdapter mAdapter;
    private int mHairId = 0;
    private int mClothesId = 0;

    // ----商场adapter数据------
    private List<String> mGoodsIds = null;

    private List<Drawable> mGoodsImgs;

    private String[] mGoodsIconIds = null;

    private String[] mGoodsNames = null;

    private String[] mGoodsPriceDesc = null;
    // ------换装需要的数据--------
    private List<String> mGoodsIdsHair;
    private List<String> mGoodsIdsClothes;
    private boolean[] mSexs;
    private int[] mGoodsTypes;
    private boolean isMan = false;

    int[] mPositions;

    public static int clickHead = -1;

    public static int clickClothes = -1;
    private int mCurPos = -1;

    // private ProgressBar mBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_progressbar);
        LogUtil.d("BaseNetActivity", "OnlineStoreActivity---->removeListener!!");
        super.removeListener();
    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);

        if (response instanceof ShowBagResp) {

            ShowBagResp resp = (ShowBagResp) response;

            BagInfo[] infos = resp.getBagInfo();

            mPositions = new int[infos.length];

            mGoodsIconIds = new String[infos.length];

            mGoodsNames = new String[infos.length];

            mGoodsPriceDesc = new String[infos.length];

            mSexs = new boolean[infos.length];

            mGoodsTypes = new int[infos.length];

            mGoodsIds = new ArrayList<String>();

            mGoodsImgs = new ArrayList<Drawable>();

            mGoodsIdsHair = new ArrayList<String>();

            mGoodsIdsClothes = new ArrayList<String>();

            for (int i = 0; i < infos.length; i++) {

                mPositions[i] = infos[i].pos;

                mGoodsNames[i] = infos[i].goodsName;

                // mGoodsPriceDesc[i] = infos[i].priceDesc;

                mGoodsIconIds[i] = infos[i].iconId;

                mGoodsTypes[i] = infos[i].type;

                mGoodsIds.add(infos[i].goodsId);

                mSexs[i] = (infos[i].sex == 0);
                // 0:头发 1：衣服
                // BitmapDrawable bitDrawable = null;

                Drawable d = null;
                StringBuffer sb = null;

                if (infos[i].type == 0) {

                    if (infos[i].pos == 0) {
                        // bitDrawable = ResAvatarUtil.getResAvatarHairDrawable(
                        // getResources(), mSexs[i],
                        // Integer.valueOf(mStoreGoodsIconIds[i]));
                        if (mSexs[i]) {
                            sb = new StringBuffer("nan_store_hair");
                        } else {
                            sb = new StringBuffer("nv_store_hair");
                        }

                        mGoodsIdsHair.add(infos[i].iconId);
                    } else if (infos[i].pos == 1) {
                        // bitDrawable =
                        // ResAvatarUtil.getResAvatarClothesDrawable(
                        // getResources(), mSexs[i],
                        // Integer.valueOf(mStoreGoodsIconIds[i]));
                        if (mSexs[i]) {
                            sb = new StringBuffer("nan_store_clothes");
                        } else {
                            sb = new StringBuffer("nv_store_clothes");
                        }
                        mGoodsIdsClothes.add(infos[i].iconId);
                    }

                } else if (infos[i].type == 1) {

                    sb = new StringBuffer("two");

                }
                if (!(infos[i].iconId.equals("-1"))) {

                    sb.append(mGoodsIconIds[i]);
                }
                d = this.getResources().getDrawable(
                        getResources().getIdentifier(sb.toString(), "drawable",
                                this.getPackageName()));
                mGoodsImgs.add(d);
            }

            startView();
            mAdapter.notifyDataSetChanged();
            // removeListener();
        }
        if (response instanceof SysCommonPush) {

            SysCommonPush resp = (SysCommonPush) response;

            if (resp.getCode() == SysCommonPush.DRESSUP_ITEM__CODE) {

                // lhx 7-20
                // saveState();

                Activity act = getParent();

                if (act instanceof OnlineHomeActivity) {

                    OnlineHomeActivity home = (OnlineHomeActivity) act;

                    OnlineHomeInfo info = home.getHomeInfo();

                    if (mPositions[mCurPos] == 0) {
                        mHairId = Integer.valueOf(mGoodsIconIds[mCurPos]);
                        info.setHairId(mHairId);
                        home.setAvartarHair(mHairId);

                    } else {
                        mClothesId = Integer.valueOf(mGoodsIconIds[mCurPos]);
                        info.setClothesId(mClothesId);
                        home.setAvartarClothes(mClothesId);
                    }

                }
                String info = resp.getInfo();

                if (info != null) {
                    int len = info.length();
                    if (len > 0 && len < 10) {
                        ToastUtil.show(info);
                    } else if (len >= 10) {
                        ToastUtil.longShow(info);
                    }
                }

            }

        }
    }

    private void startView() {
        setContentView(R.layout.online_goods_activity);
        mBtnSave = (Button) findViewById(R.id.btn_online_save);
        mBtnSave.setOnClickListener(mSaveListener);

        initView();
        fillData();
    }

    private void initView() {

        mAdapter = new OnlineGoodsImageAdapter(getApplicationContext(), mGoodsImgs,
                mGoodsNames, mGoodsPriceDesc);

        mGridView = (GridView) findViewById(R.id.gv_online_goods);

        isMan = AccountPreferrence.getSingleton().isMale(false);

        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(mItemListener);
    }

    private OnItemClickListener mItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            mCurPos = position;
            if (mSexs[position] == isMan) {

                if (mGoodsTypes[position] == 0) {

                    if (mPositions[position] == 0) {
                        clickHead = position;
                        mHairId = Integer.valueOf(mGoodsIconIds[position]);
                        ((OnlineHomeActivity) getParent()).setAvartarHair(mHairId);

                    } else {
                        clickClothes = position;
                        mClothesId = Integer.valueOf(mGoodsIconIds[position]);
                        ((OnlineHomeActivity) getParent()).setAvartarClothes(mClothesId);
                    }
                }
            } else {

                ToastUtil.show("性别不符不能换装");
            }

            mAdapter.setmCurrentPos(position);
            mAdapter.notifyDataSetChanged();
        }
    };

    private OnClickListener mSaveListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (clickHead != -1 || clickClothes != -1) {
                
                if(mSexs[mCurPos]==isMan){
                    DressupItemReq req = new DressupItemReq();
                    req.setGoodsId(mGoodsIds.get(mCurPos));
                    // addListener();
                    doSend(req);
                    
                }

            } else {

                ToastUtil.show(getResources().getString(R.string.tips_goods_should_select));

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
//        super.addListener();
    }

    protected void setAvartar(int position) {
        if (position < mGoodsIdsHair.size()) {
            clickHead = position;
            mHairId = Integer.valueOf(mGoodsIdsHair.get(position));
            ((OnlineHomeActivity) getParent()).setAvartarHair(mHairId);
        } else {
            clickClothes = position;
            mClothesId = Integer.valueOf(mGoodsIdsClothes.get(position
                    - mGoodsIdsHair.size()));
            ((OnlineHomeActivity) getParent()).setAvartarClothes(mClothesId);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
         saveState();
        super.removeListener();
    }

    private void saveState() {

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        if (clickHead != -1) {

            apf.setHairId(mHairId);

        }
        if (clickClothes != -1) {

            apf.setClothId(mClothesId);

        }
    }

    private void fillData() {

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        apf.getHairId(apf.getHairId(0));

        apf.getClothId(apf.getClothId(0));

        apf.isMale(apf.isMale(false));
    }

}
