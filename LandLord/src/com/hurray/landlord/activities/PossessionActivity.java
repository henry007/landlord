
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.BeautyDbAdapter;
import com.hurray.landlord.adapter.OfflineAchImageAdapter;
import com.hurray.landlord.entity.BeautyStatus;
import com.hurray.landlord.game.local.AchievesRec;
import com.hurray.landlord.game.local.Effects;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.SoundSwitch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

public class PossessionActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "PossessionActivity";

    private ListView mListView;
    private ImageButton mIBtnSelect;

    private ImageButton mIBtnBack;
    private ImageView mIvAchMean;
    private OfflineAchImageAdapter mListAdapter;
    private int mScrollState;
    private BeautyDbAdapter mDbHelper;
    private ArrayList<Integer> selectAchIds;

    private static final int MSG_REFRESH_LIST = 47;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST:
                    mListAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    };

    private ArrayList<Integer> achIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new BeautyDbAdapter(getApplicationContext());
        mDbHelper.open();

        setContentView(R.layout.possession_activity);

        mListView = (ListView) findViewById(R.id.lv_possession);
        mIBtnSelect = (ImageButton) findViewById(R.id.ibtn_select);
        mIBtnSelect.setOnClickListener(this);
        mIBtnBack = (ImageButton) findViewById(R.id.ibtn_possession_back);
        mIBtnBack.setOnClickListener(this);
        mIvAchMean = (ImageView) findViewById(R.id.iv_achievement_meaning);

        // test
        // AchievesRec.setAchieved(0, true);
        // AchievesRec.setAchieved(1, true);
        // AchievesRec.setAchieved(2, true);
        // AchievesRec.setAchieved(3, true);
        // AchievesRec.setAchieved(4, true);
        // AchievesRec.setAchieved(5, true);
        // AchievesRec.setAchieved(6, true);
        // AchievesRec.setAchieved(7, true);
        //
        // AchievesRec.setSelected(0, true);
        // AchievesRec.setSelected(1, false);
        // AchievesRec.setSelected(2, false);
        // AchievesRec.setSelected(3, true);
        // AchievesRec.setSelected(4, false);
        // AchievesRec.setSelected(5, false);
        // AchievesRec.setSelected(6, false);
        // AchievesRec.setSelected(7, false);

        achIds = AchievesRec.getAchievedIds();
        selectAchIds = AchievesRec.getSelectedIds();
        mListAdapter = new OfflineAchImageAdapter(getApplicationContext(), achIds, selectAchIds);

        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(mScrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SoundSwitch.isMusicOn()) {
            playBgMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SoundSwitch.isMusicOn()) {
            stopBgMusic();
        }
    }

    private OnScrollListener mScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mScrollState = scrollState;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

            switch (firstVisibleItem) {
                case 0:
                    mIvAchMean
                    .setImageResource(achIds.contains(0) ? R.drawable.unlocked_left_text_2
                            : R.drawable.offline_lv_transparent);
                    break;
                case 1:
                  
                    mIvAchMean
                    .setImageResource(achIds.contains(1) ? R.drawable.unlocked_left_text_1
                            : R.drawable.locked_right_text_1);
                    break;
                case 2:
                    mIvAchMean
                            .setImageResource(achIds.contains(2) ? R.drawable.unlocked_left_text_3
                                    : R.drawable.locked_right_text_2);
                    break;
                case 3:
                    mIvAchMean
                            .setImageResource(achIds.contains(3) ? R.drawable.unlocked_left_text_4
                                    : R.drawable.offline_lv_transparent);
                    break;
                case 4:
                    mIvAchMean
                            .setImageResource(achIds.contains(4) ? R.drawable.unlocked_left_text_5
                                    : R.drawable.locked_right_text_4);
                    break;
                case 5:
                    mIvAchMean
                            .setImageResource(achIds.contains(5) ? R.drawable.unlocked_left_text_6
                                    : R.drawable.locked_right_text_6);
                    break;
                case 6:
                    mIvAchMean
                            .setImageResource(achIds.contains(6) ? R.drawable.unlocked_left_text_7
                                    : R.drawable.locked_right_text_7);
                    break;
                case 7:
                    mIvAchMean
                            .setImageResource(achIds.contains(7) ? R.drawable.unlocked_left_text_8
                                    : R.drawable.locked_right_text_8);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_possession_back:
                finish();
                break;
            case R.id.ibtn_select:
                selectAch();
                break;
            default:
                break;
        }
    }

    private void selectAch() {
        if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            Integer currentVisiblePosition = mListView.getFirstVisiblePosition();

            LogUtil.d(TAG, "currentVisiblePosition=" + currentVisiblePosition);

            if (AchievesRec.isSelected(currentVisiblePosition)) {
                switch (currentVisiblePosition) {
                    case 0:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG0, true);
                        break;
                    case 1:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG1_SLWS, true);
                        break;
                    case 2:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG2_PLW, true);
                        break;
                    case 3:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG0, true);
                        break;
                    case 4:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG1_FXX, true);
                        break;
                    case 5:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG2_FTZZ, true);
                        break;
                    case 6:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_KQS, true);
                        mDbHelper.updateBeauty(getResources()
                                .getString(R.string.beauty_fourth_name),
                                BeautyStatus.UNSELECTED);

                        mDbHelper.updateBeauty(
                                getResources().getString(R.string.beauty_three_name),
                                BeautyStatus.SELECTED);
                        break;
                    case 7:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_DYJ, true);
                        mDbHelper.updateBeauty(getResources().getString(R.string.beauty_one_name),
                                BeautyStatus.UNSELECTED);

                        mDbHelper.updateBeauty(getResources().getString(R.string.beauty_two_name),
                                BeautyStatus.SELECTED);
                        break;
                    default:
                        break;
                }

            } else if (AchievesRec.isAchieved(currentVisiblePosition)) {
                switch (currentVisiblePosition) {
                    case 0:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG0, true);
                        break;
                    case 1:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG1_SLWS, true);
                        break;
                    case 2:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_FG2_PLW, true);
                        break;
                    case 3:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG0, true);
                        break;
                    case 4:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG1_FXX, true);
                        break;
                    case 5:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_BG2_FTZZ, true);
                        break;
                    case 6:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_KQS, true);
                        mDbHelper.updateBeauty(getResources()
                                .getString(R.string.beauty_fourth_name),
                                BeautyStatus.SELECTED);
                        mDbHelper.updateBeauty(
                                getResources().getString(R.string.beauty_three_name),
                                BeautyStatus.UNSELECTED);
                        break;
                    case 7:
                        AchievesRec.setSelected(AchievesRec.ACHIEVE_ID_DYJ, true);
                        mDbHelper.updateBeauty(getResources().getString(R.string.beauty_one_name),
                                BeautyStatus.SELECTED);
                        mDbHelper.updateBeauty(getResources().getString(R.string.beauty_two_name),
                                BeautyStatus.UNSELECTED);
                        break;
                    default:
                        break;
                }

            }

            mListAdapter.changeAchIds(AchievesRec.getAchievedIds(),
                    AchievesRec.getSelectedIds());
            mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

}
