
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.utils.SoundSwitch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

public class SoundPanel extends LinearLayout implements OnClickListener {

    private WeakReference<Button> mBtnMusic;

    private WeakReference<Button> mBtnSound;

    public SoundPanel(Context context) {
        super(context);
        initViews(context);
    }

    public SoundPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sound_panel, this);

        mBtnSound = new WeakReference<Button>((Button) view.findViewById(R.id.btn_sound));
        mBtnSound.get().setOnClickListener(this);
        mBtnMusic = new WeakReference<Button>((Button) view.findViewById(R.id.btn_music));
        mBtnMusic.get().setOnClickListener(this);
        updateSound();
        updateMusic();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sound: {
                SoundSwitch.setSound(!SoundSwitch.isSoundOn());
                updateSound();
            }
                break;
            case R.id.btn_music: {
                SoundSwitch.setMusic(!SoundSwitch.isMusicOn());
                updateMusic();
            }
                break;
        }

    }
    
    public void updateView(){
        
        updateSound();
        
        updateMusic();
        
    }

    private void updateSound() {
        if (SoundSwitch.isSoundOn()) {
            mBtnSound.get().setBackgroundResource(R.drawable.sound_on);
        } else {
            mBtnSound.get().setBackgroundResource(R.drawable.sound_off);
        }
    }

    private void updateMusic() {
        if (SoundSwitch.isMusicOn()) {
            mBtnMusic.get().setBackgroundResource(R.drawable.music_on);
        } else {
            mBtnMusic.get().setBackgroundResource(R.drawable.music_off);
        }
    }

}
