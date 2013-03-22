
package com.hurray.landlord.game.local;

import com.hurray.landlord.game.GameServer;
import com.hurray.landlord.game.OnGameEventListener;
import com.hurray.landlord.net.socket.ClientMessage;

import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;
import java.util.List;

public class LocalServer implements GameServer {

    private LocalRoom mRoom;

    private LandLordPlayer mHuman;

    private Handler mHandler;

    public LocalServer() {
        mHuman = new LandLordPlayer(0, true);

        List<LandLordPlayer> players = new LinkedList<LandLordPlayer>();
        players.add(mHuman);
        players.add(new LandLordPlayer(1, false));
        players.add(new LandLordPlayer(2, false));

        mRoom = new LocalRoom(players);
        mRoom.setFakeOnlineServer(false);

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {
                    ClientMessage cltMsg = (ClientMessage) msg.obj;
                    mHuman.onReceivedClientMessage(cltMsg);
                }
            }

        };
    }

    @Override
    public void connect() {
        mHuman.onGameStart();
        mRoom.start();
    }

    @Override
    public void disconnect() {
        mHandler.removeMessages(0);
        mRoom.close();
        mHuman.onGameOver();
    }

    @Override
    public void send(ClientMessage cltMsg) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0, cltMsg), 150);
    }

    @Override
    public void setOnGameEventListener(OnGameEventListener listener) {
        mHuman.setOnGameEventListener(listener);
    }

}
