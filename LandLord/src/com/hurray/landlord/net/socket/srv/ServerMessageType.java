package com.hurray.landlord.net.socket.srv;

public interface ServerMessageType {
    
    public static final int SRV_WAIT_ROOM = 1000;

    public static final int SRV_ROOM_INFO = 1005;
    
    public static final int SRV_PLS_READY = 1006;
    
    public static final int SRV_PLAYER_READY = 1007;    

    public static final int SRV_ALLOC_CARDS = 1010;

    public static final int SRV_PLS_DECLARE = 1015;
    
    public static final int SRV_LAST_DECLARE = 1017;

    public static final int SRV_DECLARE_RESULT = 1020;
    
    public static final int SRV_PLS_ROB = 1021;
    
    public static final int SRV_LAST_ROB = 1022;

    public static final int SRV_PLS_SHOW = 1025;
    
    public static final int SRV_PLS_FOLLOW = 1027;
    
    public static final int SRV_UPDATE_RATE = 1028;
    
    public static final int SRV_ROBOT = 1029;

    public static final int SRV_SHOW_RESULT = 1030;

    public static final int SRV_GAME_RESULT = 1035;

    public static final int SRV_HEART_BEAT = 1040;
    
    public static final int SRV_TIME_OUT = 1045;
    
    public static final int SRV_CHAT = 1050;
    
    public static final int SRV_UPDATE_PLUS = 1051;
    
    public static final int SRV_EMOTION = 1055;
    
    public static final int SRV_SYNC_CARDS = 1060;
    
    public static final int SRV_PLAYER_UPGRADE = 1065;
    
    public static final int SRV_MATCH_RESULT = 1070;
    
    public static final int SRV_RISED_WAIT = 1075;
    
    public static final int SRV_ROOM_INFOS = 1080;
    
}
