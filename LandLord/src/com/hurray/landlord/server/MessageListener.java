package com.hurray.landlord.server;

import com.hurray.landlord.server.stub.IMessageListener;
import com.hurray.landlord.server.stub.MessageHolder;
import com.hurray.lordserver.protocol.message.MessageFormatException;
import com.hurray.lordserver.protocol.message.MessageList;
import com.hurray.lordserver.protocol.message.base.BaseMessage;

import android.os.RemoteException;

/**
 * @Author: Yizhou He
 * 5/4/12 21:08
 */
public abstract class MessageListener extends IMessageListener.Stub {
    @Override
    public final void onReceive(MessageHolder holder) throws RemoteException {
        try {
            MessageList msgs = holder.getMessages();
            for (BaseMessage msg : msgs) {
                msg.setReceivedAt(System.currentTimeMillis());
            }
            onReceive(msgs);
        } catch (MessageFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void onSentResult(boolean status, MessageHolder holder) throws RemoteException {
        try {
            MessageList msgs = holder.getMessages();
            int[] nums = new int[msgs.size()];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = msgs.get(i).getTrace();
            }
            onSentResult(status, nums);
        } catch (MessageFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void onReceive(MessageList messages);

    public abstract void onSentResult(boolean status, int[] serialNum);
}
