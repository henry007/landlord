package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;


public class ClientHeartBeat extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 7062498254511344916L;

    public ClientHeartBeat() {
        super(CLT_HEART_BEAT);
    }

    @Override
    protected String packJson() {
        return "";
    }

}
