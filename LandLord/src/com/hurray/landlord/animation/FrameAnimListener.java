
package com.hurray.landlord.animation;

import android.widget.ImageView;

public interface FrameAnimListener {
    public void onStart();

    public void onStop();
    
    public void onFrameAnimResUpdated(ImageView iv);
}
