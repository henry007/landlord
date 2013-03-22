
package com.hurray.landlord.net.socket.clt;

public interface ClientMessageType {

    public static final int CLT_SIGN_IN = 100;

    public static final int CLT_SIGN_OUT = 105;

    public static final int CLT_LEAVE_ROOM = 110;

    public static final int CLT_READY = 111;
    
    public static final int CLT_GOT_CARDS = 113;

    public static final int CLT_DECLARE_LORD = 115;

    public static final int CLT_ROB_LORD = 116;

    public static final int CLT_SHOW_CARDS = 120;

    public static final int CLT_SHOW_PASS = 125;

    public static final int CLT_ROBOT = 126;

    public static final int CLT_HEART_BEAT = 130;

    public static final int CLT_CHAT = 135;
    
    public static final int CLT_GOON = 140;
    
    public static final int CLT_EMOTION = 145;

}
