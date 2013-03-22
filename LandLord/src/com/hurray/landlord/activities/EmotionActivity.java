
package com.hurray.landlord.activities;

import com.hurray.landlord.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class EmotionActivity extends BaseActivity implements OnItemClickListener {

    public static final String EMOTION_ID = "emotion_id";

    private GridView mEmotionGrid;

    private static final int[] sEmotionIconResId = {
            R.drawable.ic_emotion1,
            R.drawable.ic_emotion2,
            R.drawable.ic_emotion3,
            R.drawable.ic_emotion4,
            R.drawable.ic_emotion5,
            R.drawable.ic_emotion6
    };

    private int mEmotionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.emotion_activity);

        mEmotionId = -1;

        mEmotionGrid = (GridView) findViewById(R.id.faces);

        ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < sEmotionIconResId.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", sEmotionIconResId[i]);
            menuList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, menuList,
                R.layout.item_face_image,
                new String[] {
                    "itemImage"
                },
                new int[] {
                    R.id.itemImage
                });

        mEmotionGrid.setAdapter(adapter);
        mEmotionGrid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mEmotionId = position + 1;
        Intent i = new Intent();
        i.putExtra(EMOTION_ID, mEmotionId);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

}
