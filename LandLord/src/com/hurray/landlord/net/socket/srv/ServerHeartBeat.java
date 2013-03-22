
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

public class ServerHeartBeat extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -5837378103419799147L;

    public ServerHeartBeat(SocketStream stream) {
        super(stream);
    }

    @Override
    protected void unPackJson(String jsonString) {
    }

}
