
package com.hurray.landlord.game.online;

import com.hurray.lordserver.protocol.message.base.BaseMessage;

public interface OnlineMessageListener {
  
    public void onReceived(BaseMessage msg);

    public void onSent(boolean status, BaseMessage msg);

    public void onConnected();

    public void onConnectError();
    
    public void onDisconnected();
}
