package com.hurray.landlord.server.stub;

import com.hurray.landlord.server.stub.IMessageListener;
import com.hurray.landlord.server.stub.MessageHolder;

interface IMessageServer{

    void sendMessage(in MessageHolder msg);

    void addListener(in IMessageListener cb);

    void removeListener(in IMessageListener cb);

    void setFrequency(long time);

    void stop();
}