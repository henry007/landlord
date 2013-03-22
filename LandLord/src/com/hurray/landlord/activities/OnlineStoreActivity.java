
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OnlineStoreImageAdapter;
import com.hurray.landlord.entity.MyPreferrences.AccountPreferrence;
import com.hurray.landlord.entity.UserInfo;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ToastUtil;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.buy.BuyGoodsReq;
import com.hurray.lordserver.protocol.message.shop.OpenShopReq;
import com.hurray.lordserver.protocol.message.shop.OpenShopResp;
import com.hurray.lordserver.protocol.message.shop.OpenShopResp.ShopInfo;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OnlineStoreActivity extends BaseNetActivity {

    private GridView mGridView;
    private Button mBtnBuy;

    private TextView mTvGold;
//    , mTvMoney, mTvScore;

    private OnlineStoreImageAdapter mAdapter;
    private int mHairId = 0;
    private int mClothesId = 0;
    private Dialog mSignInDialog;
    // ----商场adapter数据------
    private List<String> mStoreGoodsIds = null;

    private List<Drawable> mStoreGoodsImgs;

    private String[] mStoreGoodsIconIds = null;

    private String[] mStoreGoodsNames = null;

    private String[] mStoreGoodsPriceDesc = null;
    // ------换装需要的数据--------
    private List<String> mStoreGoodsIdsHair;
    private List<String> mStoreGoodsIdsClothes;
    private boolean[] mSexs;
    private int[] mGoodsTypes;
    private boolean isMan = false;

    private TextView tv;

    private int mSelPos = -1; // 选中商品的标记位

    private String mDialogContent = null;
    private int moneyGOld;
    private int moneyHeart;
    private int score;
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("BaseNetActivity", "OnlineStoreActivity---->removeListener!!");
        super.removeListener();
        // doSend(new PersonCenterReq());
        // doSend(new OpenShopReq());
        // mBar = new ProgressBar(this);
        // mBar.setLayoutParams(new LayoutParams(45, 45));
        // mBar.setPadding(100, 100, 100, 100);
        setContentView(R.layout.common_progressbar);
    }

    @Override
    public void onReceived(BaseMessage response) {
        super.onReceived(response);

        if (response instanceof OpenShopResp) {

            // mStoreGoodsNames = new ArrayList<String>();
            //
            // mStoreGoodsPriceDesc = new ArrayList<String>();

            OpenShopResp resp = (OpenShopResp) response;

            ShopInfo[] infos = resp.getShopInfo();

            mStoreGoodsIconIds = new String[infos.length];

            mStoreGoodsNames = new String[infos.length];

            mStoreGoodsPriceDesc = new String[infos.length];

            mSexs = new boolean[infos.length];

            mGoodsTypes = new int[infos.length];

            mStoreGoodsIds = new ArrayList<String>();

            mStoreGoodsImgs = new ArrayList<Drawable>();

            mStoreGoodsIdsHair = new ArrayList<String>();

            mStoreGoodsIdsClothes = new ArrayList<String>();

            for (int i = 0; i < infos.length; i++) {

                mStoreGoodsNames[i] = infos[i].goodsName;

                mStoreGoodsPriceDesc[i] = infos[i].priceDesc;

                mStoreGoodsIconIds[i] = infos[i].iconId;

                mGoodsTypes[i] = infos[i].goodsType;

                mStoreGoodsIds.add(infos[i].goodsId);

                mSexs[i] = (infos[i].sex == 0);
                // 0:头发 1：衣服
                // BitmapDrawable bitDrawable = null;

                Drawable d = null;
                StringBuffer sb = null;

                if (infos[i].goodsType == 0) {

                    if (infos[i].pos == 0) {
                        // bitDrawable = ResAvatarUtil.getResAvatarHairDrawable(
                        // getResources(), mSexs[i],
                        // Integer.valueOf(mStoreGoodsIconIds[i]));
                        if (mSexs[i]) {
                            sb = new StringBuffer("nan_store_hair");
                        } else {
                            sb = new StringBuffer("nv_store_hair");
                        }

                        mStoreGoodsIdsHair.add(infos[i].iconId);
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
                        mStoreGoodsIdsClothes.add(infos[i].iconId);
                    }
                    if (!(infos[i].iconId.equals("-1"))) {

                        sb.append(mStoreGoodsIconIds[i]);
                    }

                } else if (infos[i].goodsType == 1) {

                    sb = new StringBuffer(mStoreGoodsIconIds[i]);

                }
                d = this.getResources().getDrawable(
                        getResources().getIdentifier(sb.toString(), "drawable",
                                this.getPackageName()));
                mStoreGoodsImgs.add(d);

            }
            moneyGOld = resp.getMoneyGold();

            moneyHeart = resp.getMoneyHeart();

            score = resp.getPoint();
//          if(mAdapter==null)
            startView();
        }
    }

    private void startView() {
        setContentView(R.layout.online_store_activity);
        mBtnBuy = (Button) findViewById(R.id.btn_online_buy);
        mBtnBuy.setOnClickListener(mBuyListener);

        mGridView = (GridView) findViewById(R.id.gv_online_store);
        fillData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // addListener();
        fillData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void initView() {
        // getStoreGoodsIdsHair();
        // getStoreGoodsIdsClothes();
        mAdapter = new OnlineStoreImageAdapter(getApplicationContext(), mStoreGoodsImgs,
                mStoreGoodsNames, mStoreGoodsPriceDesc);

        mTvGold = (TextView) findViewById(R.id.gold);

//        mTvMoney = (TextView) findViewById(R.id.money);
//
//        mTvScore = (TextView) findViewById(R.id.score);

        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(mItemListener);

        isMan = AccountPreferrence.getSingleton().isMale(false);

        mTvGold.setText(getResources().getString(R.string.user_gold) + moneyGOld);

//        mTvMoney.setText(getResources().getString(R.string.user_hong_tao)
//                + moneyHeart);
//
//        mTvScore.setText(getResources().getString(R.string.user_integral) + score);
    }

    private OnItemClickListener mItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {

            mSelPos = position;

            mDialogContent = mStoreGoodsPriceDesc[position];

            if (mGoodsTypes[mSelPos] == 0) {

                if (mSexs[mSelPos] == isMan) {

                    setAvartar(mSelPos);

                    // showDialog(DIALOG_PURCHASE_CONFIRM);
                    // TextView tv = (TextView) mSignInDialog
                    // .findViewById(R.id.tips_incompelete_payment2);
                    // tv.setText(getResources().getString(R.string.tips_confirm_purchase)
                    // + "\n"
                    // + mDialogContent);
                }
                // else {
                // ToastUtil.show("性别不符不能换装");
                // }
            }

            // if (mGoodsTypes[position] == 0) {
            //
            // if (mSexs[position] == isMan) {
            //
            // setAvartar(position);
            //
            // showDialog(DIALOG_PURCHASE_CONFIRM);
            // TextView tv = (TextView) mSignInDialog
            // .findViewById(R.id.tips_incompelete_payment2);
            // tv.setText(getResources().getString(R.string.tips_confirm_purchase)
            // + "\n"
            // + mDialogContent);
            // } else {
            // ToastUtil.show("性别不符不能换装");
            // }
            // } else {
            // showDialog(DIALOG_PURCHASE_CONFIRM);
            // }

            mAdapter.setmCurrentPos(position);
            mAdapter.notifyDataSetChanged();
        }

    };

    private void setAvartar(int position) {
        if (position < mStoreGoodsIdsHair.size()) {
            mHairId = Integer.valueOf(mStoreGoodsIdsHair.get(position));
            ((OnlineHomeActivity) getParent()).setAvartarHair(mHairId);
        } else {
            mClothesId = Integer.valueOf(mStoreGoodsIdsClothes.get(position
                    - mStoreGoodsIdsHair.size()));
            ((OnlineHomeActivity) getParent()).setAvartarClothes(mClothesId);
        }
        // ((OnlineHomeActivity) getParent()).changeAvatar(mHairId,
        // mClothesId);
    }

    private void fillData() {

        AccountPreferrence apf = AccountPreferrence.getSingleton();

        apf.setHairId(apf.getHairId(0));

        apf.setClothId(apf.getClothId(0));

        apf.setMale(apf.isMale(false));
    }

    private void dismissSignInDialog() {
        if (mSignInDialog != null) {
            mSignInDialog.dismiss();
        }
    }

    private static final int DIALOG_PURCHASE_SUCCESS = 2;
    private static final int DIALOG_PURCHASE_CONFIRM = 1;

    protected android.app.Dialog onCreateDialog(int id) {
        View view = null;
        switch (id) {
            case DIALOG_PURCHASE_CONFIRM: {
                final Dialog dialog = new Dialog(OnlineStoreActivity.this, R.style.dialog);
                dismissSignInDialog();
                mSignInDialog = dialog;
                System.out.println("mDialogContent-->" + mDialogContent);

                view = LayoutInflater.from(OnlineStoreActivity.this).inflate(
                        R.layout.dlg_purchase_confirm, null);

                tv = (TextView) view.findViewById(R.id.tips_incompelete_payment2);

                tv.setText(getResources().getString(R.string.tips_confirm_purchase) + "\n"
                        + mDialogContent);
                dialog.setContentView(view);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);
                // WindowManager m = getWindowManager();
                // Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                lp.width = (int) getResources().getDimension(R.dimen.dialog_confirm_purchase_width); // 设置宽度
                lp.height = (int) getResources().getDimension(R.dimen.dialog_confirm_purchase_height); // 设置高度
                dialogWindow.setAttributes(lp);
                TextView btnOk = (TextView) view.findViewById(R.id.btn_ok);
                btnOk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        BuyGoodsReq req = new BuyGoodsReq();

                        req.setGoodsId(mStoreGoodsIds.get(mSelPos));

                        doSend(req);

//                        doSend(new OpenShopReq());

                        dialog.dismiss();

                    }
                });
                TextView btnCancel = (TextView) view.findViewById(R.id.btn_cancel);
                btnCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                return mSignInDialog;
            }

            case DIALOG_PURCHASE_SUCCESS: {
                final Dialog dialog = new Dialog(OnlineStoreActivity.this, R.style.dialog);
                dismissSignInDialog();

                view = LayoutInflater.from(OnlineStoreActivity.this).inflate(
                        R.layout.dlg_purchase_success, null);
                dialog.setContentView(view);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);
                // WindowManager m = getWindowManager();
                // Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                lp.width = 400; // 设置宽度
                lp.height = 250; // 设置高度
                dialogWindow.setAttributes(lp);

                TextView btnOk = (TextView) view.findViewById(R.id.btn_ok);
                btnOk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                return dialog;
            }

            default:
                break;
        }
        return null;
    }

    private OnClickListener mBuyListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if(mSelPos == -1){
                return;
            }
            
            if (mGoodsTypes[mSelPos] == 0) {

                if (mSexs[mSelPos] == isMan) {

                    showDialog(DIALOG_PURCHASE_CONFIRM);
                    TextView tv = (TextView) mSignInDialog
                            .findViewById(R.id.tips_incompelete_payment2);
                    tv.setText(mStoreGoodsNames[mSelPos] + "\n" + getResources().getString(
                            R.string.tips_confirm_purchase)
                            + "\n"
                            + mDialogContent);
                } else {
                    ToastUtil.show("性别不符不能换装");
                }
            } else {
                showDialog(DIALOG_PURCHASE_CONFIRM);
                TextView tv = (TextView) mSignInDialog
                        .findViewById(R.id.tips_incompelete_payment2);
                tv.setText(getResources().getString(R.string.tips_confirm_purchase) + "\n"
                        + mDialogContent);
            }

        }
    };

}
