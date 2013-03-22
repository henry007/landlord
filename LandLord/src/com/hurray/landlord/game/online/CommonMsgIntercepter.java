package com.hurray.landlord.game.online;

import com.hurray.lordserver.protocol.message.base.BaseMessage;

public interface CommonMsgIntercepter {
        public boolean msgIntercepted(BaseMessage msg);
}
