
package com.hurray.landlord.activities;

import com.hurray.landlord.R;
import com.hurray.lordserver.protocol.message.ache.AchInfoReq;
import com.hurray.lordserver.protocol.message.ache.AchInfoResp;
import com.hurray.lordserver.protocol.message.ache.AchInfoResp.AcheInfo;
import com.hurray.lordserver.protocol.message.base.BaseMessage;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabPersonalAchieveActivity extends BaseNetActivity {

    private static final String IMAGE = "img";

    private static final String NAME = "name";

    private static final String DESCRIPT = "descript";

    private ListView mListView;

    private SimpleAdapter mAdapter;

    private ProgressBar mProgressBar;

    private ArrayList<Map<String, String>> mAchieveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_achievement);

        initView();

        mProgressBar.setVisibility(View.VISIBLE);
        doSend(new AchInfoReq());
    }

    @Override
    public void onReceived(BaseMessage response) {

        if (response instanceof AchInfoResp) {
            AchInfoResp r = (AchInfoResp) response;

            mProgressBar.setVisibility(View.GONE);

            if (r.isSucceeded()) {
                updateAdapter(r.getAcheInfo());
                
//                removeListener();
            }
        }

    }

    private void updateAdapter(AcheInfo[] acheInfos) {
        mAchieveList.clear();
        
        for (AcheInfo info : acheInfos) {
            String iconName = info.iconId;
            StringBuffer bufIconName = new StringBuffer(iconName);
            bufIconName.append(info.beSelected ? "_1" : "_0");

            int iconId = getResources().getIdentifier(
                    bufIconName.toString(),
                    "drawable",
                    this.getPackageName());

            Map<String, String> map = new HashMap<String, String>();

            map.put(IMAGE, String.valueOf(iconId));
            map.put(NAME, info.acheName);
            map.put(DESCRIPT, info.acheDesc);

            mAchieveList.add(map);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void initView() {

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mListView = (ListView) findViewById(R.id.achievement_listview);

        initListAdapter();
    }

    private void initListAdapter() {

        mAchieveList = new ArrayList<Map<String, String>>();
        mAdapter = new SimpleAdapter(this, mAchieveList, R.layout.achievement_gridview,
                new String[] {
                        IMAGE,
                        NAME,
                        DESCRIPT
                },
                new int[] {
                        R.id.achievement_img,
                        R.id.tv_achievement_name,
                        R.id.tv_achievement_descript
                });
        mListView.setAdapter(mAdapter);
    }
}
