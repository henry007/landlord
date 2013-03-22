
package com.hurray.landlord;


public interface Constants {

    // debug
    public static final boolean DEBUG = true;

    public static final boolean IS_61_MATCH_VER = false;

    public static final boolean DEBUG_EXTRA_INFO = false;

    public static final boolean DEBUG_ONLINE_UI_SKIP_NETWORK = false;

    public static final boolean DEBUG_OFFLINE_TURNTABLE_SKIP_NETWORK = false;// false：联网获取图片和文字
                                                                             // true：单机初始化抽奖图片和文字
    
    public static final boolean DEBUG_POSSESSION_ACH = false;//测试美女收藏的成就，false：正常； true：全部成就开启

    public static final boolean DEBUG_OFFLINE_GAME_USE_ROBOT = false;

    public static final boolean APP_UPDATE_ON = true;

    public static final int OFFLINE_GAME_ROUND_NUM = 10;

    public static final String CONTENT_TYPE_PARAMETERS = "text/html";

    public static final int PLAYER_NUM = 3;

    public static final int TOTAL_CARDS_NUM = 54; // 总牌数

    public static final int BOTTOM_CARDS_NUM = 3;

    public static final int PLAYER_CARDS_NUM = (TOTAL_CARDS_NUM - BOTTOM_CARDS_NUM) / PLAYER_NUM;

    public static final int LANDLORD_CARDS_NUM = PLAYER_CARDS_NUM + BOTTOM_CARDS_NUM;

    public static final int SHUFFLE_TIMES = 300; // 洗牌次数

    public static final String VOL_CHANGE_ACTION = "com.vol.change";

    public static final int BASE_NUM = 100;

    public static final int CARDS_RANDOM = 0;
    public static final int CARDS_ROCKET = 1;
    public static final int CARDS_SINGLE_DRAGON = 2;
    public static final int CARDS_DOUBLE_DRAGON = 3;
    public static final int CARDS_TRIPLE_W_SINGLE = 4;

    public static int CONTROL_CARDS = CARDS_RANDOM;

    // ---------Preferrence 帐号信息----------------
    public static final String PREFS_ACCOUNT = "account";
    public static final String USER_ID = "userId";
    public static final String SESSION_ID = "sessionId";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String IS_GUEST = "isGuest";
    public static final String HAIR_ID = "hairId";
    public static final String CLOTHES_ID = "clothesId";
    public static final String IS_MALE = "isMale";
    public static final String NICKNAME = "nickname";
    public static final String LEVEL = "level";
    public static final String CURRENT_EXP = "current_exp";
    public static final String NEXT_EXP = "next_exp";
    public static final String SILENCE = "sp_silence";
    // ---------Preferrence 帐号信息----------------
    public final static String SAVE_PWD_KEY = "save_pwd";
    public final static String USER_KEY = "username";
    public final static String NICK_KEY = "nickname";
    public final static String PASS_KEY = "password_key";
    public final static String USER_ID_KEY = "userid";

    // -------------连续登录奖励信息--------------
    public final static String MONEY_HEART_VALUE = "money_heart_value";
    public final static String MONEY_GOLD_VALUE = "money_gold_value";
    public final static String AWARD_DESC = "award_desc";
    public final static String AWARD_FLAG = "pop_view";
    // --------------保存在线时长奖励info---------
    public final static String ONLINE_PRIZE = "online_prize";
    // --------------StartActivity------------
    public final static String DIRECT_EXIT = "doexit";
    public final static String DO_EXIT = "dologin";
    public static final String SEND_HEART = "sendheart";
    // ---------------resultActivityCode---------
    public final static int RESULT_FOR_ONLINE_PRIZE_ACTIVITY = 1000;
    public final static int RESULT_FOR_MYTURNTABLE_SURFACEVIEW_ACTIVITY = 1001;
    public final static int RESULT_FOR_ONLINE_REGISTER_ACTIVITY = 1002;
    public final static int RESULT_FOR_ONLINE_INDIVIDUAL_ACTIVITY = 1003;

}
