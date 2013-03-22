
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

public class ClientLeaveRoom extends ClientMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -4049631765111353055L;

    public ClientLeaveRoom() {
        super(CLT_LEAVE_ROOM);
    }

    @Override
    protected String packJson() {
        return "";
    }

}
