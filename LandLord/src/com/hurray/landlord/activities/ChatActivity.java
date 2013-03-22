
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.landlord.entity.ChatInfo;
import com.hurray.landlord.game.ui.UiSpeakHistory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    public static final String CHAT = "chat";

    public static final String CHAT_ID = "chat_id";

    private ListView mCommonChats;

    private ListView mChatHistory;

    private EditText mChatContent;

    private View mBtnCommonChat;

    private View mBtnChatHistory;

    private int mChatId;

    private boolean mCommonChatTabOn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        
        mChatId = -1;

        initView();

        ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < ChatInfo.SPEAKS_ARR.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemtext", ChatInfo.SPEAKS_ARR[i]);
            menuList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, menuList,
                R.layout.item_speak_text,
                new String[] {
                    "itemtext"
                },
                new int[] {
                    R.id.itemtext
                });

        mCommonChats.setAdapter(adapter);

        ArrayList<HashMap<String, Object>> historyList = new ArrayList<HashMap<String, Object>>();

        for (int i = UiSpeakHistory.sHistorySpeak.size() - 1; i >= 0; i--) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemtext", UiSpeakHistory.sHistorySpeak.get(i));
            historyList.add(map);
        }

        SimpleAdapter historyadapter = new SimpleAdapter(this, historyList,
                R.layout.item_speak_text,
                new String[] {
                    "itemtext"
                },
                new int[] {
                    R.id.itemtext
                });

        mChatHistory.setAdapter(historyadapter);

    }

    private void initView() {
        mChatContent = (EditText) findViewById(R.id.chat_content);
        findViewById(R.id.btn_send).setOnClickListener(this);

        mBtnCommonChat = findViewById(R.id.btn_common_chat);
        // mBtnCommonChat.setBackgroundResource(R.drawable.tab_common_speak_normal);
        mBtnCommonChat.setOnClickListener(this);

        mBtnChatHistory = findViewById(R.id.btn_chat_history);
        // mBtnChatHistory.setBackgroundResource(R.drawable.tab_common_speak_press);
        mBtnChatHistory.setOnClickListener(this);

        mCommonChatTabOn = true;

        mCommonChats = (ListView) findViewById(R.id.common_chats);
        mCommonChats.setOnItemClickListener(this);
        mChatHistory = (ListView) findViewById(R.id.chat_history);

        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mChatId = position;

        if (mChatId >= 0 && mChatId < ChatInfo.SPEAKS_ARR.length) {
            mChatContent.setText(ChatInfo.SPEAKS_ARR[mChatId]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send: {
                String chat = mChatContent.getText().toString();
                if (!"".equalsIgnoreCase(chat)) {
                    Intent i = new Intent();
                    i.putExtra(CHAT, chat);
                    i.putExtra(CHAT_ID, mChatId);
                    setResult(Activity.RESULT_OK, i);
                }

                finish();
            }
                break;
            case R.id.btn_common_chat:

                if (!mCommonChatTabOn) {
                    mCommonChatTabOn = true;

                    mCommonChats.setVisibility(View.VISIBLE);
                    mChatHistory.setVisibility(View.GONE);

                    mBtnCommonChat.setBackgroundResource(R.drawable.tab_common_speak_normal);
                    mBtnChatHistory.setBackgroundResource(R.drawable.tab_history_speak_press);
                }

                break;
            case R.id.btn_chat_history:

                if (mCommonChatTabOn) {
                    mCommonChatTabOn = false;

                    mCommonChats.setVisibility(View.GONE);
                    mChatHistory.setVisibility(View.VISIBLE);

                    mBtnCommonChat.setBackgroundResource(R.drawable.tab_common_speak_press);
                    mBtnChatHistory.setBackgroundResource(R.drawable.tab_history_speak_normal);
                }
                break;

            case R.id.btn_close: {
                doBack();
            }
                break;

            default:
                break;
        }

    }

    private void doBack() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected boolean onBack() {
        doBack();
        return true;
    }

}
