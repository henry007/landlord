
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.adapter.OfflineTopDbadapter;
import com.hurray.landlord.adapter.OfflineTopListAdapter;
import com.hurray.landlord.entity.UserInfo;
import com.hurray.landlord.utils.SoundSwitch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class OfflineTopActivity extends BaseActivity {

    private static final int DIALOG_TEXT_ENTRY = 47;

    private static final String TOP_LIST_PLAYER_FILE = "top_list_player_file";

    private static final String TOP_LIST_PLAYER_NAME = "top_list_player_name";

    private Button mBtnBack;

    private ListView mListView;

    private RelativeLayout mRootLayout;

    private RelativeLayout mRlListBg;

    private OfflineTopListAdapter mAdapter;

    private String mPlayerName;

    private int mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_top_activity);

        mRootLayout = (RelativeLayout) findViewById(R.id.root_offline_layout);

        mRlListBg = (RelativeLayout) findViewById(R.id.rl_offline_top_list_bg);

        mBtnBack = (Button) findViewById(R.id.btn_offline_back);
        mBtnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new OfflineTopListAdapter(getApplicationContext());
        mListView = (ListView) findViewById(R.id.lv_offline_top);
        mListView.setAdapter(mAdapter);

        loadPlayerName();

    }
    

    private void showDialogFromIntent() {
        Intent intent = getIntent();
        mScore = intent.getIntExtra(OfflineTopDbadapter.KEY_SCORE, 0);
        if (mScore > 0) {
            try {
                dismissDialog(DIALOG_TEXT_ENTRY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            safeShowDialog(DIALOG_TEXT_ENTRY);
        }
    }
    
    private void clearIntent() {
        Intent intent = getIntent();
        intent.removeExtra(OfflineTopDbadapter.KEY_SCORE);
        mScore = 0;
    }

    private void loadPlayerName() {
        SharedPreferences topList = getSharedPreferences(TOP_LIST_PLAYER_FILE, MODE_PRIVATE);
        synchronized (topList) {
            mPlayerName = topList.getString(TOP_LIST_PLAYER_NAME, getString(R.string.player));
        }
    }

    private void savePlayerName() {
        SharedPreferences topList = getSharedPreferences(TOP_LIST_PLAYER_FILE, MODE_PRIVATE);
        synchronized (topList) {
            Editor ed = topList.edit();
            ed.clear();
            ed.putString(TOP_LIST_PLAYER_NAME, mPlayerName);
            ed.commit();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_TEXT_ENTRY:
                if (mScore > 0 && mScore > OfflineTopDbadapter.getMinScore(this)) {
                    LayoutInflater factory = LayoutInflater.from(this);
                    final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
                    final EditText et = (EditText) textEntryView.findViewById(R.id.username_edit);
                    Editable editable = et.getText();
                    editable.clear();
                    editable.append(mPlayerName);

                    return new AlertDialog.Builder(OfflineTopActivity.this)
                            .setTitle(R.string.alert_dialog_text_entry)
                            .setView(textEntryView)
                            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String playerName = et.getText().toString();
                                    if (!TextUtils.isEmpty(playerName)) {
                                        mPlayerName = playerName;
                                        savePlayerName();
                                    }

                                    if (OfflineTopDbadapter.addRecord(OfflineTopActivity.this, mPlayerName, mScore)) {
                                        updateTopList();
                                    }
                                    
                                    clearIntent();
                                }
                            })
                            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (OfflineTopDbadapter.addRecord(OfflineTopActivity.this, mPlayerName, mScore)) {
                                        updateTopList();
                                    }
                                    
                                    clearIntent();
                                }
                            })
                            .create();
                }
        }
        return null;
    }

    private void updateTopList() {
        ArrayList<UserInfo> userInfoList = OfflineTopDbadapter.getUserInfoList(this);
        mAdapter.setUserInfoList(userInfoList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if(SoundSwitch.isMusicOn()){
            playBgMusic();
        }

        updateTopList();

        showDialogFromIntent();

        mRootLayout.setBackgroundResource(R.drawable.bg_dance);
        mRlListBg.setBackgroundResource(R.drawable.bg_offline_top_view);
        mBtnBack.setBackgroundResource(R.drawable.bg_offline_btn_back);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if(SoundSwitch.isMusicOn()){
            stopBgMusic();
         }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mRootLayout.setBackgroundResource(0);
        mRlListBg.setBackgroundResource(0);
        mBtnBack.setBackgroundResource(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
