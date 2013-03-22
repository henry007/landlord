package com.hurray.landlord.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hurray.landlord.R;
import com.hurray.landlord.sdk.SdkManagerJuZi;
import com.hurray.landlord.sms.SmsInfo;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.pay.PayListReq;
import com.hurray.lordserver.protocol.message.pay.PayListResp;
import com.hurray.lordserver.protocol.message.pay.PayListResp.FeeInfoBody;


public class TabPaymentActivity extends BaseNetActivity {
    public final static String KEY_FEE_MONEY = "PAY_FEE_TYPE";
    public final static String KEY_FEE_GOLD = "PAY_GOLD_DES";
    private GridView mGridView;
    
    private GridAdapter mAdapter;
    private OnItemClickListener mItemClickListener;
    private TextView goldDes;
    private PayListResp payList;
    
    private ImageButton button;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_layout);
        button = (ImageButton) findViewById(R.id.gv_pay_button);
        doSend(new PayListReq());
        if (SdkManagerJuZi.power) {
			button.setOnClickListener(SdkManagerJuZi.getInstance()
					.getOffersOnClickListener());
		}
        if(SmsInfo.power){
        	button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SmsInfo.getInstance().sendSms(1);
				}
			});
        }
    }
    
    @Override
    public void onReceived(BaseMessage response) {
    	if(response instanceof PayListResp){
    		payList = (PayListResp)response;
    		if(payList.isSucceeded()){
    			initPayRespData(payList);
    		}
    	}
    }
    
    private void initPayRespData(PayListResp payRespDataBody){
    	goldDes = (TextView)findViewById(R.id.tv_gold);
    	goldDes.setText(payRespDataBody.getMoneyGoldDes());
    	goldDes.setTextColor(Color.YELLOW);
    	
    	mGridView = (GridView) findViewById(R.id.gv_payment);
        
        mAdapter = new GridAdapter();
        
        mItemClickListener = new OnItemClickListener(){              
        	@Override
        	public void onItemClick(AdapterView<?> parent, View v, int pos,
        			long id) {
        		Object obj = v.getTag();
				if(obj instanceof TabPaymentActivity.GridAdapter.ViewHolder){
					startPaymentDetail((TabPaymentActivity.GridAdapter.ViewHolder) obj);
				} else {
					System.out.println("无数据体");
				}
        	}
        };
        
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(mItemClickListener);
    }
    
    private void startPaymentDetail(TabPaymentActivity.GridAdapter.ViewHolder holder){
    	Intent i = new Intent(this, TabPaymentDetailActivity.class);
    	i.putExtra(KEY_FEE_MONEY, holder.money);
    	i.putExtra(KEY_FEE_GOLD, holder.goldDes);
        startActivity(i);
    }

    public class GridAdapter extends BaseAdapter {
    	
        @Override
        public int getCount() {
        	if(payList != null && payList.getFeeInfoBodys() != null){
        		return payList.getFeeInfoBodys().length;
        	}
        	return 0;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
        	try {
				convertView = LayoutInflater.from(TabPaymentActivity.this)
						.inflate(R.layout.acitivity_recharge_item, null);
				ViewHolder holder = new ViewHolder(convertView, payList.getFeeInfoBodys()[position]);
				convertView.setTag(holder);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
        }

        final class ViewHolder {
        	
        	public int money = 0;
        	public String goldDes = "";
        	
            ViewHolder(View convertView, FeeInfoBody feeInfoBody){
            	if (feeInfoBody != null) {
					money = feeInfoBody.feeMoney;
					goldDes = feeInfoBody.feeMoneyDes;
				}
				// 金币数量
            	TextView mTVGold = (TextView) convertView
						.findViewById(R.id.tv_gold_content);
				mTVGold
						.setText(goldDes);
//				mTVGold.setTextSize(10);
				// 充值面值
				TextView mTvMoney = (TextView) convertView
						.findViewById(R.id.tv_money);
				mTvMoney
						.setText("¥" + money);
//				mTvMoney.setTextSize(14);
				mTvMoney.setTextColor(Color.YELLOW);
				// 后缀
				TextView mDiscount = (TextView) convertView
						.findViewById(R.id.tv_gold_discount);
				mDiscount.setText("");
            }
        }
    }
}
