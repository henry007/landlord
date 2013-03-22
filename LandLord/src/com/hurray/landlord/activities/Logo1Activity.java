
package com.hurray.landlord.activities;

import com.hurray.landlord.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Logo1Activity extends BaseActivity {

    private static final String TAG = "Logo1Activity";
    
    private ImageView mLogo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo1_activity);

        startLogoAnim();
    }

    private void startLogoAnim() {
        mLogo1 = (ImageView) findViewById(R.id.logo1);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_out);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i = new Intent(Logo1Activity.this, StartActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }
        });
        
        mLogo1.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        mLogo1.clearAnimation();
    }

    @Override
    protected boolean onBack() {
        return true;
    }

}
