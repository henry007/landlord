
package com.hurray.landlord.view;

import com.hurray.landlord.R;
import com.hurray.landlord.animation.FrameAnim;
import com.hurray.landlord.game.data.PlayerContext;

import android.view.View;
import android.widget.ImageView;

public class LordCupViewHolder {
    public ImageView mSelfLordCup;
    public FrameAnim mSelfLordCupAnim;

    public View mLeftLordLayout;
    public ImageView mLeftLordArrow;
    public ImageView mLeftLordCup;
    public FrameAnim mLeftLordCupAnim;

    public View mRightLordLayout;
    public ImageView mRightLordArrow;
    public ImageView mRightLordCup;
    public FrameAnim mRightLordCupAnim;

    public void updateLordCupViews(PlayerContext playerCtx) {

        if (playerCtx.isSelfLandLord()) {
            mSelfLordCup.setVisibility(View.VISIBLE);
            mLeftLordLayout.setVisibility(View.INVISIBLE);
            mRightLordLayout.setVisibility(View.INVISIBLE);

            mLeftLordCupAnim.onStop();
            mRightLordCupAnim.onStop();

            mSelfLordCupAnim.updateFrameAnimRes(R.anim.hg_frame);
            mSelfLordCupAnim.onStart();
        } else {
            mSelfLordCup.setVisibility(View.INVISIBLE);
            mSelfLordCupAnim.onStop();

            if (playerCtx.isPrevPlayerLandLord()) { // left
                mLeftLordLayout.setVisibility(View.VISIBLE);
                mRightLordLayout.setVisibility(View.INVISIBLE);

                mRightLordCupAnim.onStop();

                mLeftLordCupAnim.updateFrameAnimRes(R.anim.hg_frame);
                mLeftLordCupAnim.onStart();
                mLeftLordArrow.setImageResource(R.drawable.left_arrow1);
                mLeftLordArrow.setVisibility(View.VISIBLE);
            } else {
                mRightLordLayout.setVisibility(View.VISIBLE);
                mLeftLordLayout.setVisibility(View.INVISIBLE);

                mLeftLordArrow.setImageResource(0);
                mLeftLordArrow.setVisibility(View.GONE);
                mLeftLordCupAnim.onStop();

                mRightLordCupAnim.updateFrameAnimRes(R.anim.hg_frame);
                mRightLordCupAnim.onStart();
                mRightLordArrow.setImageResource(R.drawable.right_arrow1);
                mRightLordArrow.setVisibility(View.VISIBLE);
            }
        }
    }

    public void reset() {
        mSelfLordCup.setVisibility(View.INVISIBLE);
        mLeftLordLayout.setVisibility(View.INVISIBLE);
        mRightLordLayout.setVisibility(View.INVISIBLE);

        mSelfLordCupAnim.onStop();
        mLeftLordArrow.setImageResource(0);
        mLeftLordArrow.setVisibility(View.GONE);
        
        mLeftLordCupAnim.onStop();
        mRightLordArrow.setImageResource(0);
        mRightLordArrow.setVisibility(View.GONE);
        mRightLordCupAnim.onStop();
    }

    public void onDestroy() {
        mSelfLordCupAnim.onStop();
        mLeftLordArrow.setImageResource(0);
        mLeftLordArrow.setVisibility(View.GONE);
        mLeftLordCupAnim.onStop();
        mRightLordArrow.setImageResource(0);
        mRightLordArrow.setVisibility(View.GONE);
        mRightLordCupAnim.onStop();

        mSelfLordCup = null;
        mSelfLordCupAnim = null;

        mLeftLordLayout = null;
        mLeftLordArrow = null;
        mLeftLordCup = null;
        mLeftLordCupAnim = null;

        mRightLordLayout = null;
        mRightLordArrow = null;
        mRightLordCup = null;
        mRightLordCupAnim = null;
    }
}
