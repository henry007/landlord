package com.hurray.landlord.server;

import com.hurray.landlord.server.stub.IMessageServer;
import com.hurray.landlord.server.stub.MessageHolder;
import com.hurray.lordserver.protocol.message.MessageFormatException;
import com.hurray.lordserver.protocol.message.MessageList;
import com.hurray.lordserver.protocol.message.base.BaseMessage;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * @Author: Yizhou He
 * 4/27/12 16:40
 */
public class MessageServerWrapper {
    private IMessageServer iMessageServer;

    public MessageServerWrapper(IMessageServer iMessageServer) {
        this.iMessageServer = iMessageServer;
    }

    public MessageServerWrapper(IBinder iBinder) {
        this(IMessageServer.Stub.asInterface(iBinder));
    }

    public void addListener(MessageListener cb) throws RemoteException {
        iMessageServer.addListener(cb);
    }

    public void removeListener(MessageListener cb) throws RemoteException {
        iMessageServer.removeListener(cb);
    }

    public void sendMessage(BaseMessage msg) throws RemoteException, MessageFormatException {
        MessageHolder holder = new MessageHolder(new MessageList(msg));
        iMessageServer.sendMessage(holder);
    }

    public void setFrequency(long time) throws RemoteException {
        iMessageServer.setFrequency(time);
    }

    public void stop() throws RemoteException {
        iMessageServer.stop();
    }

}
