
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

public class ClientSignOut extends ClientMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 7721308474532294812L;

    public ClientSignOut() {
        super(CLT_SIGN_OUT);
    }

    @Override
    protected String packJson() {
        return "";
    }

}
