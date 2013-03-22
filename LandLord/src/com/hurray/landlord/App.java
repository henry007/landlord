
package com.hurray.landlord;

import com.hurray.landlord.bitmaps.CardFgsBitmap;
import com.hurray.landlord.bitmaps.CardSoftBitmapRef;
import com.hurray.landlord.bitmaps.SoftBitmapRef;
import com.hurray.landlord.entity.MyPreferrences;
import com.hurray.landlord.game.local.AchievesRec;
import com.hurray.landlord.game.local.Effects;
import com.hurray.landlord.game.local.ScoreRec;
import com.hurray.landlord.game.ui.UiConstants;
import com.hurray.landlord.settings.Settings;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.landlord.utils.ManifestUtil;
import com.hurray.landlord.utils.NetworkUtil;
import com.hurray.landlord.utils.SoundSwitch;
import com.hurray.landlord.utils.SoundUtil;
import com.hurray.landlord.utils.ToastUtil;

import android.app.Application;

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();

        initStaticClass();
    }

    private void initStaticClass() {
        MyPreferrences.initContext(this);
        ToastUtil.init(this);
        ManifestUtil.init(this);
        NetworkUtil.initContext(this);
        NetworkUtil.checkNetworkState();
        
        UiConstants.initDrawings(this);
        Settings.init(this);
        ScoreRec.init(this);
        Effects.init(this);
        AchievesRec.init(this);
        SoundSwitch.init(this);  
        SoundUtil.init(this);
        
        CardFgsBitmap.init(this);
        CardSoftBitmapRef.init(this);
        SoftBitmapRef.init(this);
    }

    @Override
    public void onTerminate() {
        LogUtil.d(TAG, "onTerminate");
        super.onTerminate();

        SoundUtil.getSingleton().close();
    }

}
