
package com.hurray.landlord.game;

import com.hurray.landlord.R;
import com.hurray.landlord.game.data.Sex;
import com.hurray.landlord.utils.SoundUtil;
import com.hurray.landlord.utils.SoundUtil.OnSoundListener;

import android.content.Context;
import android.content.res.Resources;

import java.util.Random;

public class GameSoundUtil {

    private static Random mRandom = new Random();

    public static boolean getRandPassType() {
        return mRandom.nextBoolean();
    }

    public static void playShowSound(Context ctx, int[] showCardIds, int cardType, int sex, boolean isShow) {
        int[] ids = getSoundResIds(ctx, showCardIds, cardType, sex);
        playShowSound(ids, cardType, isShow);
    }

    public static void playAllocCards() {
        play(R.raw.s_alloc_cards);
    }

    public static void playAllocCardsRepeat(final OnRepeatListener repeatListener) {
        OnSoundListener soundListener = new OnSoundListener() {

            @Override
            public void onError(int errCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCompletion() {
                if (repeatListener.goonRepeat()) {
                    play(R.raw.s_alloc_cards, this);
                }
            }
        };

        play(R.raw.s_alloc_cards, soundListener);
    }

    public static void playDeclareLord(int num, int sex) {
        int resId;
        if (sex == Sex.MALE) { // nan
            switch (num) {
                case 1:
                    resId = R.raw.m_yi_fen;
                    break;
                case 2:
                    resId = R.raw.m_er_fen;
                    break;
                case 3:
                    resId = R.raw.m_san_fen;
                    break;
                default:
                    resId = R.raw.m_bu_jiao;
                    break;
            }
        } else {
            switch (num) {
                case 1:
                    resId = R.raw.f_yi_fen;
                    break;
                case 2:
                    resId = R.raw.f_er_fen;
                    break;
                case 3:
                    resId = R.raw.f_san_fen;
                    break;
                default:
                    resId = R.raw.f_bu_jiao;
                    break;
            }
        }

        play(resId);

    }

    public static void playRobLord(boolean rob, int sex) {
        int resId;
        if (sex == Sex.MALE) { // nan
            if (rob) {
                resId = R.raw.m_qiang_di_zhu;
            } else {
                resId = R.raw.m_bu_qiang;
            }
        } else {
            if (rob) {
                resId = R.raw.f_qiang_di_zhu;
            } else {
                resId = R.raw.f_bu_qiang;
            }
        }

        play(resId);
    }

    public static void playShowCards() {
        play(R.raw.s_show_cards);
    }

    public static void playResult(boolean success, OnSoundListener listener) {
        int resId;
        if (success) {
            resId = R.raw.bgm_success;
        } else {
            resId = R.raw.bgm_failed;
        }

        play(resId, listener);
    }

    public static void playPassSound(boolean randPassType, int sex) {
        if (randPassType) {
            if (sex == Sex.MALE) {
                play(R.raw.m_bu_yao);
            } else {
                play(R.raw.f_bu_yao);
            }
        } else {
            if (sex == Sex.MALE) {
                play(R.raw.m_yao_bu_qi);
            } else {
                play(R.raw.f_yao_bu_qi);
            }
        }
    }

    private static void playShowSound(int[] ids, int cardType, boolean isShow) {

        switch (cardType) {
            case CardType.LL_SINGLE: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_DOUBLE: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_TRIPLE: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_TRIPLE_W_SINGLE: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_TRIPLE_W_DOUBLE: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_SINGLE_DRAGON: {
                if (isShow)
                    play(ids[0]);
                else
                    playRandomVol(ids);
            }
                break;

            case CardType.LL_DOUBLE_DRAGON: {
                play(ids[0]);
            }
                break;

            case CardType.LL_TRIPLE_DRAGON:
            case CardType.LL_TRIPLE_DRAGON_W_SINGLE:
            case CardType.LL_TRIPLE_DRAGON_W_DOUBLE: {
                play(ids[0]);
            }
                break;

            case CardType.LL_FOUR_W_TWO_SINGLE: {
                play(ids[0]);
            }
                break;

            case CardType.LL_FOUR_W_TWO_DOUBLE: {
                play(ids[0]);
            }
                break;

            case CardType.LL_FOUR_AS_BOMBER: {
                play(R.raw.s_explosion);
                play(ids[0]);
            }
                break;

            case CardType.LL_JOKER_AS_ROCKET: {
                play(ids[0]);
            }
                break;

            default:
                break;
        }

    }

    private static int[] getSoundResIds(Context ctx, int[] showCardIds, int cardType, int sex) {
        final int[] ids = new int[3];

        Resources res = ctx.getResources();
        String pkg = ctx.getPackageName();

        StringBuffer sb = new StringBuffer();
        if (sex == Sex.MALE) {
            sb.append("m_");
            ids[1] = R.raw.m_guan_shang;
            ids[2] = R.raw.m_da_ni;
        } else {
            sb.append("f_");
            ids[1] = R.raw.f_guan_shang;
            ids[2] = R.raw.f_da_ni;
        }

        switch (cardType) {
            case CardType.LL_SINGLE: {
                int val = CardUtil.getCardValue(showCardIds[0]);
                sb.append(val);
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_DOUBLE: {
                int val = CardUtil.getCardValue(showCardIds[0]);
                sb.append("dui_" + val);
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_TRIPLE: {
                int val = CardUtil.getCardValue(showCardIds[0]);
                sb.append("san_ge_" + val);
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_TRIPLE_W_SINGLE: {
                sb.append("san_dai_yi");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_TRIPLE_W_DOUBLE: {
                sb.append("san_dai_er");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_SINGLE_DRAGON: {
                sb.append("shun_zi");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_DOUBLE_DRAGON: {
                sb.append("lian_dui");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_TRIPLE_DRAGON: {
                sb.append("fei_ji");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;
            case CardType.LL_TRIPLE_DRAGON_W_SINGLE:
            case CardType.LL_TRIPLE_DRAGON_W_DOUBLE: {
                sb.append("fei_ji_dai_chi_bang");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_FOUR_W_TWO_SINGLE:
            case CardType.LL_FOUR_W_TWO_DOUBLE: {
                sb.append("si_dai_er");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_FOUR_AS_BOMBER: {
                sb.append("zha_dan");
                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);
            }
                break;

            case CardType.LL_JOKER_AS_ROCKET: {
                if (mRandom.nextBoolean()) {
                    sb.append("huo_jian");
                } else {
                    sb.append("wang_zha");
                }

                ids[0] = res.getIdentifier(sb.toString(), "raw", pkg);

            }
                break;

            default:
                break;
        }

        return ids;
    }

    private static int getRandSoundId(int[] ids) {
        return ids[mRandom.nextInt(ids.length)];
    }

    private static void playRandomVol(int[] ids) {
        int soundId = getRandSoundId(ids);
        play(soundId);
    }

    private static void play(int resId) {
        SoundUtil.getSingleton().play(resId);
    }

    private static void play(int resId, OnSoundListener listener) {
        SoundUtil.getSingleton().play(resId, listener);
    }

    public interface OnRepeatListener {
        public boolean goonRepeat();
    }

}
