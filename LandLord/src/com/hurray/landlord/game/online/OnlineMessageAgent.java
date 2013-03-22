package com.hurray.landlord.game.online;

import com.hurray.lordserver.protocol.message.base.BaseMessage;

public interface OnlineMessageAgent {
    
    public void doSend(BaseMessage msg);
    
    public void setOnlineMessageListener(OnlineMessageListener l);    
}
