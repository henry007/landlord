
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.BeautyDbAdapter;
import com.hurray.landlord.entity.BeautyInfo;
import com.hurray.landlord.entity.BeautyStatus;
import com.hurray.landlord.utils.SoundSwitch;
import com.hurray.landlord.view.SoundPanel;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import java.util.ArrayList;

public class OfflineHomeActivity extends BaseActivity implements OnTouchListener {

    private View mRootLayout;
    private View mRootLayout1;

    private ImageButton mIBtnBeautyOne;
    private ImageButton mIBtnBeautyTwo;
    private ImageButton mIBtnBeautyThree;
    private ImageButton mIBtnBeautyFourth;

    private BeautyDbAdapter mDbHelper;
    private Cursor mBeautyCursor;

    private int[] mAnimationIds = {
            R.anim.beauty_one_wink,
            R.anim.beauty_two_wink,
            R.anim.beauty_three_wink,
            R.anim.beauty_fourth_wink
    };
    private int[] mUnselectedIds = {
            R.drawable.iv_beauty_one_unselected,
            R.drawable.iv_beauty_two_unselected,
            R.drawable.iv_beauty_three_unselected,
            R.drawable.iv_beauty_fourth_unselected
    };
    private int[] mUnavailableIds = {
            R.drawable.iv_beauty_one_not_activated,
            R.drawable.iv_beauty_two_not_activated,
            R.drawable.iv_beauty_three_not_activated,
            R.drawable.iv_beauty_fourth_not_activated
    };
    private int[] mPressedIds = {
            R.drawable.iv_beauty_one_pressed,
            R.drawable.iv_beauty_two_pressed,
            R.drawable.iv_beauty_three_pressed,
            R.drawable.iv_beauty_fourth_pressed
    };
    private int[] mUnavailablePressedIds = {
    	R.drawable.iv_beauty_one_not_activated_pressed,
    	0,0,
    	R.drawable.iv_beauty_fourth_not_activated_pressed
    };

    private ImageButton[] mIBtnBeauty = new ImageButton[4];
    private BeautyInfo[] mBeautyInfos = new BeautyInfo[4];
    private AnimationDrawable[] winkAnimation = new AnimationDrawable[4];
    private ArrayList<Integer> selectedList = null;
    private int preSelected = 0;
    private ImageButton mIBtnStart;
    private ImageButton mIBtnPossession;
    private ImageButton mIBtnTop;
    private ImageButton mIBtnBack;
    
    private SoundPanel mSoundPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new BeautyDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.offline_home_activity);
        
        mSoundPanel = (SoundPanel) findViewById(R.id.sound_panel);

        mRootLayout = findViewById(R.id.root_layout);
        mRootLayout1 = findViewById(R.id.root_layout1);
        
        mIBtnBeautyOne = (ImageButton) findViewById(R.id.ibtn_beauty_one);
        mIBtnBeautyOne.setOnTouchListener(this);
        mIBtnBeauty[0] = mIBtnBeautyOne;

        mIBtnBeautyTwo = (ImageButton) findViewById(R.id.ibtn_beauty_two);
        mIBtnBeautyTwo.setOnTouchListener(this);
        mIBtnBeauty[1] = mIBtnBeautyTwo;

        mIBtnBeautyThree = (ImageButton) findViewById(R.id.ibtn_beauty_three);
        mIBtnBeautyThree.setOnTouchListener(this);
        mIBtnBeauty[2] = mIBtnBeautyThree;

        mIBtnBeautyFourth = (ImageButton) findViewById(R.id.ibtn_beauty_fourth);
        mIBtnBeautyFourth.setOnTouchListener(this);
        mIBtnBeauty[3] = mIBtnBeautyFourth;

        mIBtnStart = (ImageButton) findViewById(R.id.ibtn_start);
        mIBtnStart.setOnClickListener(mStartListener);
        mIBtnPossession = (ImageButton) findViewById(R.id.ibtn_possession);
        mIBtnPossession.setOnClickListener(mPossessionListener);
        mIBtnTop = (ImageButton) findViewById(R.id.ibtn_top);
        mIBtnTop.setOnClickListener(mTopListener);
        mIBtnBack = (ImageButton) findViewById(R.id.ibtn_back);
        mIBtnBack.setOnClickListener(mBackListener);

        selectedList = (savedInstanceState == null) ? new ArrayList<Integer>() :
                savedInstanceState.getIntegerArrayList(OfflineGameActivity.SELECTED_LIST);

        populateBackground();

    }

    private void populateBackground() {
        if (mBeautyCursor != null && !mBeautyCursor.isClosed()) {
            mBeautyCursor.close();
        }
        mBeautyCursor = mDbHelper.fetchAllBeauty();
        int count = mBeautyCursor.getCount();
        if (count == 0) { // initialize tables
            mDbHelper.createBeauty(
                    getResources().getString(R.string.beauty_one_name),
                    BeautyStatus.UNAVAILABLE);
            mDbHelper.createBeauty(
                    getResources().getString(R.string.beauty_two_name),
                    BeautyStatus.SELECTED);
            mDbHelper.createBeauty(
                    getResources().getString(R.string.beauty_three_name),
                    BeautyStatus.SELECTED);
            mDbHelper.createBeauty(
                    getResources().getString(R.string.beauty_fourth_name),
                    BeautyStatus.UNAVAILABLE);
        } else { // initialize image resource
            mBeautyCursor.moveToFirst();
            selectedList.clear();
            int columnStatusIndex = mBeautyCursor
                    .getColumnIndex(BeautyDbAdapter.KEY_STATUS);
            int columnNameIndex = mBeautyCursor
                    .getColumnIndex(BeautyDbAdapter.KEY_NAME);
            String status = "";
            String name = "";
            for (int i = 0; i < count; i++) {
                status = mBeautyCursor.getString(columnStatusIndex);
                name = mBeautyCursor.getString(columnNameIndex);
                if (status.equals(BeautyStatus.SELECTED.toString())) {
                    mIBtnBeauty[i].setBackgroundResource(mAnimationIds[i]);
                    winkAnimation[i] = (AnimationDrawable) mIBtnBeauty[i].getBackground();
                    mBeautyInfos[i] = new BeautyInfo(BeautyStatus.SELECTED,
                            name);
                    selectedList.add(i);
                } else if (status.equals(BeautyStatus.UNSELECTED.toString())) {
                    mIBtnBeauty[i].setBackgroundResource(mUnselectedIds[i]);
                    mBeautyInfos[i] = new BeautyInfo(BeautyStatus.UNSELECTED,
                            name);
                } else if (status.equals(BeautyStatus.UNAVAILABLE.toString())) {
                    mIBtnBeauty[i].setBackgroundResource(mUnavailableIds[i]);
                    mBeautyInfos[i] = new BeautyInfo(BeautyStatus.UNAVAILABLE,
                            name);
                }
                Log.i("start", "name:" + mBeautyInfos[i].getName()
                        + "; status:" + mBeautyInfos[i].getStatus() + "");
                mBeautyCursor.moveToNext();
            }
        }
    }

    private void selectRole(int current) {
        preSelected = selectedList.get(0);
        mBeautyInfos[preSelected].setStatus(BeautyStatus.UNSELECTED);
        mIBtnBeauty[preSelected].setBackgroundResource(mUnselectedIds[preSelected]);
        winkAnimation[preSelected].stop();

        selectedList.remove(0);
        mBeautyInfos[current].setStatus(BeautyStatus.SELECTED);
        mIBtnBeauty[current].setBackgroundResource(mAnimationIds[current]);
        winkAnimation[current] = (AnimationDrawable) mIBtnBeauty[current].getBackground();
        winkAnimation[current].start();
        selectedList.add(current);
    }

    private void saveState() {
        for (int i = 0; i < mBeautyCursor.getCount(); i++) {
            mDbHelper.updateBeauty(mBeautyInfos[i].getName(),
                    mBeautyInfos[i].getStatus());
            Log.i("end", "name: " + mBeautyInfos[i].getName() + "; status: "
                    + mBeautyInfos[i].getStatus() + "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPanel.updateView();
        populateBackground();
//        if(SoundSwitch.isMusicOn()){
//            playBgMusic();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRootLayout.setBackgroundResource(R.drawable.bg_dance);
        mRootLayout1.setBackgroundResource(R.drawable.bg_offline_home);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRootLayout.setBackgroundResource(0);
        mRootLayout1.setBackgroundResource(0);
        mIBtnBeautyOne.setBackgroundResource(0);
        mIBtnBeautyTwo.setBackgroundResource(0);
        mIBtnBeautyThree.setBackgroundResource(0);
        mIBtnBeautyFourth.setBackgroundResource(0);
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
//        if(SoundSwitch.isMusicOn()){
//            stopBgMusic();
//         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBeautyCursor != null && !mBeautyCursor.isClosed()) {
            mBeautyCursor.close();
        }
        mDbHelper.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putIntegerArrayList(OfflineGameActivity.SELECTED_LIST, selectedList);
    }

    private OnClickListener mStartListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), OfflineGameActivity.class);
            intent.putIntegerArrayListExtra(OfflineGameActivity.SELECTED_LIST, selectedList);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    private OnClickListener mPossessionListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), PossessionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    private OnClickListener mTopListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	Intent intent = new Intent(getApplicationContext(), OfflineTopActivity.class);
        	startActivity(intent);
        	overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    private OnClickListener mBackListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            doBack();
        }
    };
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int current = 0;
        switch (v.getId()) {
            case R.id.ibtn_beauty_one:
                current = 0;
                break;
            case R.id.ibtn_beauty_two:
                current = 1;
                break;
            case R.id.ibtn_beauty_three:
                current = 2;
                break;
            case R.id.ibtn_beauty_fourth:
                current = 3;
                break;
            default:
                current = 0;
                break;
        }
        if (mBeautyInfos[current].getStatus().equals(BeautyStatus.UNSELECTED)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mIBtnBeauty[current].setBackgroundResource(mPressedIds[current]);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                selectRole(current);
            }
            return true;
        } else if (mBeautyInfos[current].getStatus().equals(BeautyStatus.SELECTED)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (winkAnimation[current].isRunning()) {
                    winkAnimation[current].stop();
                }
                mIBtnBeauty[current].setBackgroundResource(mPressedIds[current]);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIBtnBeauty[current].setBackgroundResource(mAnimationIds[current]);
                winkAnimation[current] = (AnimationDrawable) mIBtnBeauty[current].getBackground();
                winkAnimation[current].start();
            }
            return true;
        } else if (mBeautyInfos[current].getStatus().equals(BeautyStatus.UNAVAILABLE)) {
        	if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mIBtnBeauty[current].setBackgroundResource(mUnavailablePressedIds[current]);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mIBtnBeauty[current].setBackgroundResource(mUnavailableIds[current]);
			}
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            for (int i = 0; i < winkAnimation.length; i++) {
                if (winkAnimation[i] != null) {
                    winkAnimation[i].start();
                }
            }
        }
    }
    
    private void doBack() {
        finish();
    }
    
    @Override
    protected boolean onBack() {
        doBack();
        return true;
    }

}
