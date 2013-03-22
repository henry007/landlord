
package com.hurray.landlord.game.ui;

import com.hurray.landlord.Constants;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UiConstants {

    private static final String TAG = "UiConstants";

    /***
     * 显示相关
     */
    public static Paint sPaint = new Paint();

    public static final float SMALL_SCALE = 0.7f;

    private static final int SELF_CARD_WIDTH_DP = 52;

    private static final int SELF_CARD_HEIGHT_DP = 75;

    private static final int SELF_CARDS_GAP_DP = SELF_CARD_WIDTH_DP / 2 + 1;

    public static final int MAX_COL = 10; // 每行最多显示张数

    public static final int MAX_ROW = 2; // 最大列数

    private static final int SHOW_CARDS_GAP_DP = Math.round(SELF_CARDS_GAP_DP * SMALL_SCALE);

    private static final int SHOW_CARD_WIDTH_DP = Math
            .round(SELF_CARD_WIDTH_DP * SMALL_SCALE);

    private static final int SHOW_CARD_HEIGHT_DP = Math.round(SELF_CARD_HEIGHT_DP
            * SMALL_SCALE);

    public static int sShowCardsGap;

    public static int sShowCardWidth;

    public static int sShowCardHeight;

    public static int sSelfCardsGap;

    public static int sSelfCardWidth;

    public static int sSelfCardHeight;

    public static int sSelfCardHead;

    public static int sScreenwidth;

    public static void initDrawings(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        sScreenwidth = dm.widthPixels;
        if (dm.heightPixels > sScreenwidth) {
            sScreenwidth = dm.heightPixels;
        }

        // showCards

        sShowCardsGap = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SHOW_CARDS_GAP_DP, dm));

        sShowCardWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SHOW_CARD_WIDTH_DP, dm));

        sShowCardHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SHOW_CARD_HEIGHT_DP, dm));

        // selfCards

        sSelfCardWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SELF_CARD_WIDTH_DP, dm));

        sSelfCardHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SELF_CARD_HEIGHT_DP, dm));

        sSelfCardsGap = (sScreenwidth - sSelfCardWidth) / (Constants.PLAYER_CARDS_NUM - 1);

        sSelfCardHead = sSelfCardHeight / 7;

        sPaint.setColor(0x851e014a);
    }

    public static void updateSelfCardsGap(int[] selfCardIds) {
        if (selfCardIds != null && selfCardIds.length > 1) {
            sSelfCardsGap = (sScreenwidth - sSelfCardWidth) / (selfCardIds.length - 1);
            if (sSelfCardsGap > sSelfCardWidth * 3 / 5) {
                sSelfCardsGap = sSelfCardWidth * 3 / 5;
            }
        }
    }

}
