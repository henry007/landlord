
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;
import com.hurray.landlord.utils.LogUtil;

public class ServerMessageFactory implements ServerMessageType {

    private static final String TAG = "ServerMessageFactory";

    public static ServerMessage create(SocketStream stream) {
        int msgType = stream.getMsgType();

        LogUtil.d(TAG, "msgType=" + msgType);

        switch (stream.getMsgType()) {
            case SRV_WAIT_ROOM:
                return new ServerWaitRoom(stream);
            case SRV_ROOM_INFO:
                return new ServerRoomInfo(stream);
            case SRV_PLS_READY:
                return new ServerPlsReady(stream);
            case SRV_PLAYER_READY:
                return new ServerPlayerReady(stream);
            case SRV_ALLOC_CARDS:
                return new ServerAllocCards(stream);
            case SRV_PLS_DECLARE:
                return new ServerPlsDeclare(stream);
            case SRV_LAST_DECLARE:
                return new ServerLastDeclare(stream);
            case SRV_DECLARE_RESULT:
                return new ServerDeclareResult(stream);
            case SRV_PLS_SHOW:
                return new ServerPlsShow(stream);
            case SRV_PLS_FOLLOW:
                return new ServerPlsFollow(stream);
            case SRV_ROBOT:
                return new ServerRobot(stream);
            case SRV_UPDATE_RATE:
                return new ServerUpdateRate(stream);
            case SRV_SHOW_RESULT:
                return new ServerShowResult(stream);
            case SRV_GAME_RESULT:
                return new ServerGameResult(stream);
            case SRV_HEART_BEAT:
                return new ServerHeartBeat(stream);
            case SRV_TIME_OUT:
                return new ServerTimeOut(stream);
            case SRV_CHAT:
                return new ServerChat(stream);
            default:
                break;
        }

        return null;
    }

}
