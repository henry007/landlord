
package com.hurray.landlord.game.online;

import com.hurray.landlord.game.data.RoomInfo;
import com.hurray.landlord.net.socket.ClientMessage;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.clt.ClientChat;
import com.hurray.landlord.net.socket.clt.ClientDeclareLord;
import com.hurray.landlord.net.socket.clt.ClientEmotion;
import com.hurray.landlord.net.socket.clt.ClientMessageType;
import com.hurray.landlord.net.socket.clt.ClientRobot;
import com.hurray.landlord.net.socket.clt.ClientShowCards;
import com.hurray.landlord.net.socket.srv.ServerAllocCards;
import com.hurray.landlord.net.socket.srv.ServerChat;
import com.hurray.landlord.net.socket.srv.ServerDeclareResult;
import com.hurray.landlord.net.socket.srv.ServerEmotion;
import com.hurray.landlord.net.socket.srv.ServerGameResult;
import com.hurray.landlord.net.socket.srv.ServerLastDeclare;
import com.hurray.landlord.net.socket.srv.ServerMatchResult;
import com.hurray.landlord.net.socket.srv.ServerPlayerUpgrade;
import com.hurray.landlord.net.socket.srv.ServerPlsDeclare;
import com.hurray.landlord.net.socket.srv.ServerPlsFollow;
import com.hurray.landlord.net.socket.srv.ServerPlsShow;
import com.hurray.landlord.net.socket.srv.ServerRisedWait;
import com.hurray.landlord.net.socket.srv.ServerRobot;
import com.hurray.landlord.net.socket.srv.ServerRoomInfos;
import com.hurray.landlord.net.socket.srv.ServerShowResult;
import com.hurray.landlord.net.socket.srv.ServerSyncCards;
import com.hurray.landlord.net.socket.srv.ServerUpdateRate;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.lordserver.protocol.message.Constants;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.card.CardMultPush;
import com.hurray.lordserver.protocol.message.card.DealCardReq;
import com.hurray.lordserver.protocol.message.card.DealCardResp;
import com.hurray.lordserver.protocol.message.card.DeclareLordPush;
import com.hurray.lordserver.protocol.message.card.DeclareLordReq;
import com.hurray.lordserver.protocol.message.card.DeclareLordResultPush;
import com.hurray.lordserver.protocol.message.card.GameEndPush;
import com.hurray.lordserver.protocol.message.card.GameEndPush.PlayerResult;
import com.hurray.lordserver.protocol.message.card.MatchEndPush;
import com.hurray.lordserver.protocol.message.card.MatchEndPush.PlayersRank;
import com.hurray.lordserver.protocol.message.card.PlsReadyPush;
import com.hurray.lordserver.protocol.message.card.RiseInRankWaitPush;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush;
import com.hurray.lordserver.protocol.message.card.SettingRobotPush;
import com.hurray.lordserver.protocol.message.card.SettingRobotReq;
import com.hurray.lordserver.protocol.message.card.ShowCardPush;
import com.hurray.lordserver.protocol.message.card.ShowCardPush.OtherPlayserCard;
import com.hurray.lordserver.protocol.message.card.ShowCardReq;
import com.hurray.lordserver.protocol.message.chat.PushChat;
import com.hurray.lordserver.protocol.message.chat.SendChatReq;
import com.hurray.lordserver.protocol.message.role.RoleUpLevelPush;

import android.util.SparseArray;

import java.util.ArrayList;

public class MessageAdapter implements ClientMessageType {

    private static final String TAG = "MessageAdapter";

    private static final int ONE_SEC = 1000;

    private RoomInfo mRoomInfo;

    public MessageAdapter(RoomInfo roomInfo) {
        mRoomInfo = roomInfo;
    }

    public ArrayList<BaseMessage> fromClientMessageToBaseMessage(ClientMessage cltMsg) {
        ArrayList<BaseMessage> lst = new ArrayList<BaseMessage>();
        // TODO

        int msgType = cltMsg.getMsgType();
        switch (msgType) {
            case CLT_READY: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_READY");

                // DealCardReq
                DealCardReq dealCardReq = new DealCardReq();
                dealCardReq.setTeamid(mRoomInfo.getRoomId());
                lst.add(dealCardReq);
            }
                break;
            case CLT_DECLARE_LORD: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_DECLARE_LORD");

                ClientDeclareLord clientDeclareLord = (ClientDeclareLord) cltMsg;
                int declareLordNum = clientDeclareLord.getDeclareLordNum();

                // DeclareLordReq
                DeclareLordReq declareLordReq = new DeclareLordReq();
                declareLordReq.setTeamid(mRoomInfo.getRoomId());
                declareLordReq.setScore(declareLordNum);

                lst.add(declareLordReq);
            }
                break;
            case CLT_ROB_LORD: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_ROB_LORD");
            }
                break;
            case CLT_SHOW_CARDS: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_SHOW_CARDS");
                ClientShowCards clientShowCards = (ClientShowCards) cltMsg;
                int[] showCardsId = clientShowCards.getShowCardIds();
                showCardsId = CardIdAdapter.toServerCardIds(showCardsId);

                // ShowCardReq
                int playType = getPlayType(clientShowCards.isFollowCards());
                ShowCardReq showCardReq = new ShowCardReq();
                showCardReq.setTeamId(mRoomInfo.getRoomId());
                showCardReq.setPlayType(playType);
                showCardReq.setCards(showCardsId);

                lst.add(showCardReq);
            }
                break;
            case CLT_SHOW_PASS: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_SHOW_PASS");

                // ShowCardReq
                ShowCardReq showCardReq = new ShowCardReq();
                showCardReq.setTeamId(mRoomInfo.getRoomId());
                showCardReq.setPlayType(getPlayType(true));

                lst.add(showCardReq);
            }
                break;
            case CLT_ROBOT: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_ROBOT");

                ClientRobot clientRobot = (ClientRobot) cltMsg;
                boolean isRobot = clientRobot.isRobot();

                int type = Constants.ROBOT_NO_TYPE;
                if (isRobot) {
                    type = Constants.ROBOT_YES_TYPE;
                }

                SettingRobotReq settingRobotReq = new SettingRobotReq();
                settingRobotReq.setTeamid(mRoomInfo.getRoomId());
                settingRobotReq.setType(type);

                lst.add(settingRobotReq);
            }
                break;
            case CLT_CHAT: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_CHAT");
                ClientChat clientChat = (ClientChat) cltMsg;
                int chatId = clientChat.getChatId();
                String message = clientChat.getMessage();

                SendChatReq sendChatReq = new SendChatReq();
                sendChatReq.setType(0);
                sendChatReq.setTeamId(mRoomInfo.getRoomId());
                sendChatReq.setContent(message);
                sendChatReq.setContentId(chatId);
                sendChatReq.setEmotionId(-1);
                lst.add(sendChatReq);
            }
                break;
            case CLT_EMOTION: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_EMOTION");
                ClientEmotion clientEmotion = (ClientEmotion) cltMsg;
                int emotionId = clientEmotion.getEmotionId();

                SendChatReq sendChatReq = new SendChatReq();
                sendChatReq.setType(1);
                sendChatReq.setTeamId(mRoomInfo.getRoomId());
                sendChatReq.setEmotionId(emotionId);
                lst.add(sendChatReq);
            }
                break;
            case CLT_LEAVE_ROOM: {
                LogUtil.d(TAG, "ClientMessage msgType = CLT_LEAVE_ROOM");

                SettingRobotReq settingRobotReq = new SettingRobotReq();
                settingRobotReq.setTeamid(mRoomInfo.getRoomId());
                settingRobotReq.setType(Constants.ROBOT_YES_TYPE);
                lst.add(settingRobotReq);
            }
                break;
            default:
                LogUtil.d(TAG, "ClientMessage msgType = " + msgType);
                break;
        }

        return lst;
    }

    ServerRoomInfos infos = null;
    
    @SuppressWarnings("null")
    public ArrayList<ServerMessage> fromBaseMessageToServerMessage(BaseMessage baseMsg)
            throws UidException {
        ArrayList<ServerMessage> lst = new ArrayList<ServerMessage>();

        RoomInfo roomInfo = null;

      

        LogUtil.d(TAG, "BaseMessage name=" + baseMsg.getName());

        if (baseMsg instanceof DealCardResp) {
            LogUtil.d(TAG, "BaseMessage DealCardResp");
            DealCardResp r = (DealCardResp) baseMsg;

            // ServerAllocCards
            int selfPlayerId = mRoomInfo.getSelfPlayers().getPlayerId();
            int[] cardIds = r.getCards();
            cardIds = CardIdAdapter.toClientCardIds(cardIds);
            ServerAllocCards srvAllocCards = new ServerAllocCards(selfPlayerId, cardIds);
            lst.add(srvAllocCards);

            // ServerPlsDeclare
            int declarePlayerId = mRoomInfo.getPlayerId(r.getDeclareLordUid());
            ServerPlsDeclare srvPlsDeclare = new ServerPlsDeclare(declarePlayerId, r.getWaitTime()
                    * ONE_SEC);
            lst.add(srvPlsDeclare);
        } else if (baseMsg instanceof DeclareLordPush) {
            LogUtil.d(TAG, "BaseMessage DeclareLordPush");
            DeclareLordPush r = (DeclareLordPush) baseMsg;

            // ServerLastDeclare
            int lastDeclarePlayerId = mRoomInfo.getPlayerId(r.getUid());
            int declareNum = r.getScore();
            int maxDeclareNum = r.getMaxScore();
            ServerLastDeclare srvLastDeclare = new ServerLastDeclare(lastDeclarePlayerId,
                    declareNum, maxDeclareNum);
            lst.add(srvLastDeclare);

            // ServerPlsDeclare
            long nextDeclareUid = r.getNextDeclareUid();
            if (nextDeclareUid >= 0) {
                int declarePlayerId = mRoomInfo.getPlayerId(nextDeclareUid);
                ServerPlsDeclare srvPlsDeclare = new ServerPlsDeclare(declarePlayerId,
                        r.getWaitTime() * ONE_SEC);
                lst.add(srvPlsDeclare);
            }

        } else if (baseMsg instanceof DeclareLordResultPush) {
            LogUtil.d(TAG, "BaseMessage DeclareLordResultPush");
            DeclareLordResultPush r = (DeclareLordResultPush) baseMsg;

            // ServerDeclareResult
            int lordPlayerId = mRoomInfo.getPlayerId(r.getUid());
            int finalDeclareNum = r.getScore();
            int[] bottomCardIds = r.getBaseCards();
            bottomCardIds = CardIdAdapter.toClientCardIds(bottomCardIds);
            ServerDeclareResult srvDeclareResult = new ServerDeclareResult(true, finalDeclareNum,
                    lordPlayerId,
                    bottomCardIds);
            lst.add(srvDeclareResult);

            // ServerPlsShow
            ServerPlsShow srvPlsShow = new ServerPlsShow(lordPlayerId, r.getWaitTime() * ONE_SEC);
            lst.add(srvPlsShow);
        } else if (baseMsg instanceof ShowCardPush) {
            LogUtil.d(TAG, "BaseMessage ShowCardPush");
            ShowCardPush r = (ShowCardPush) baseMsg;
            LogUtil.d(TAG, "ShowCardPush getShowResult = " + r.getShowResult());
            if (r.getShowResult() == ShowCardPush.RESULT_OK) {

                // ServerShowResult
                int lastShowPlayerId = mRoomInfo.getPlayerId(r.getLastUid());
                int[] lastShowCardIds = r.getLastCards();
                lastShowCardIds = CardIdAdapter.toClientCardIds(lastShowCardIds);
                boolean isShow = isPlayTypeShow(r.getLastType());
                ServerShowResult srvShowResult = new ServerShowResult(lastShowPlayerId,
                        lastShowCardIds, isShow);
                lst.add(srvShowResult);

                boolean isNextShow = isPlayTypeShow(r.getNextType());
                long timeLeft = r.getWaitTime() * ONE_SEC;
                long nextUid = r.getNextUid();
                if (nextUid != Constants.NOT_NEXT_UID) {
                    int nextPlayerId = mRoomInfo.getPlayerId(nextUid);
                    if (nextPlayerId >= 0 && nextPlayerId < 3) { // 判断是否有下一个出牌人
                        if (isNextShow) {
                            // ServerPlsShow
                            ServerPlsShow srvPlsShow = new ServerPlsShow(nextPlayerId, timeLeft);
                            lst.add(srvPlsShow);
                        } else {
                            // ServerPlsFollow
                            int maxPlayerId = mRoomInfo.getPlayerId(r.getMaxUid());
                            int[] maxCardIds = r.getMaxCards();
                            maxCardIds = CardIdAdapter.toClientCardIds(maxCardIds);
                            ServerPlsFollow srvPlsFollow = new ServerPlsFollow(nextPlayerId,
                                    maxPlayerId, maxCardIds,
                                    timeLeft);
                            lst.add(srvPlsFollow);
                        }
                    }
                }

                // ServerSyncCards
                int[] selfCardIds = r.getSelfCards();
                int leftNum = -1;
                int rightNum = -1;
                OtherPlayserCard[] others = r.getOtherPlayserCard();
                for (OtherPlayserCard p : others) {
                    if (mRoomInfo.isLeftPlayer(p.uid)) {
                        leftNum = p.cardNum;
                    } else if (mRoomInfo.isRightPlayer(p.uid)) {
                        rightNum = p.cardNum;
                    }
                }

                ServerSyncCards srvSyncCards = new ServerSyncCards(selfCardIds, leftNum, rightNum);
                lst.add(srvSyncCards);
            }
        } else if (baseMsg instanceof CardMultPush) {
            LogUtil.d(TAG, "BaseMessage CardMultPush");
            CardMultPush r = (CardMultPush) baseMsg;

            // ServerUpdateRate
            ServerUpdateRate srvUpdateRate = new ServerUpdateRate(r.getMult());
            lst.add(srvUpdateRate);
        } else if (baseMsg instanceof SettingRobotPush) {
            SettingRobotPush p = (SettingRobotPush) baseMsg;
            int type = p.getType();

            boolean isRobot = false;
            if (type == Constants.ROBOT_YES_TYPE) {
                isRobot = true;
            }

            int selfPlayerId = mRoomInfo.getSelfPlayers().getPlayerId();
            ServerRobot srvRobot = new ServerRobot(selfPlayerId, isRobot);
            lst.add(srvRobot);
        } else if (baseMsg instanceof PushChat) {
            LogUtil.d(TAG, "BaseMessage PushChat");
            PushChat r = (PushChat) baseMsg;

            int playerId = mRoomInfo.getPlayerId(r.getFromUid());
            if (r.getType() == 0) {// chat
                // ServerChat
                String message = r.getContent();
                int chatId = r.getContentId();
                ServerChat srvChat = new ServerChat(playerId, message, chatId);
                lst.add(srvChat);
            } else if (r.getType() == 1) { // emotion
                int emotionId = r.getEmotionId();
                if (emotionId >= 0) {
                    // ServerEmotion
                    ServerEmotion srvEmotion = new ServerEmotion(playerId, emotionId);
                    lst.add(srvEmotion);
                }
            }

        } else if (baseMsg instanceof GameEndPush) {
            LogUtil.d(TAG, "BaseMessage GameEndPush");
            GameEndPush r = (GameEndPush) baseMsg;

            // ServerGameResult
            boolean lordWin = (r.getWinType() == Constants.LORD_WIN_TYPE);
            PlayerResult[] results = r.getResults();
            int len = results.length;
            int[] marks = new int[len];
            int[] totalMarks = new int[len];
            ArrayList<int[]> cardIdsList = new ArrayList<int[]>(len);

            SparseArray<int[]> tempList = new SparseArray<int[]>(len);
            for (PlayerResult p : results) {
                int playerId = mRoomInfo.getPlayerId(p.uid);
                marks[playerId] = p.winScore;
                totalMarks[playerId] = p.totalScore;
                int[] cardIds = CardIdAdapter.toClientCardIds(p.cards);
                tempList.put(playerId, cardIds);
            }

            for (int i = 0; i < len; i++) {
                cardIdsList.add(tempList.get(i));
            }
            tempList.clear();
            tempList = null;

            ServerGameResult srvGameResult = new ServerGameResult(lordWin, marks, totalMarks, null,
                    cardIdsList);
            lst.add(srvGameResult);
        } else if (baseMsg instanceof MatchEndPush) {
            MatchEndPush r = (MatchEndPush) baseMsg;

            // ServerMatchResult
            int point = r.getPoint();
            int gold = r.getMoneyGold();
            int heart = r.getMoneyHeart();
            int exp = r.getCurrExp();
            int rank = r.getRank();
            // TODO lhx 排名界面信息

            int isRised = r.riseResult;

            PlayersRank[] rankInfos = r.getResults();

            ServerMatchResult srvMatchResult = new ServerMatchResult(point, gold, heart, exp, rank,
                    isRised, rankInfos);
            lst.add(srvMatchResult);

        } else if (baseMsg instanceof RoleUpLevelPush) {
            LogUtil.d(TAG, "BaseMessage RoleUpLevelPush");
            RoleUpLevelPush r = (RoleUpLevelPush) baseMsg;

            // ServerPlayerUpgrade
            ServerPlayerUpgrade srvPlayerUpgrade = new ServerPlayerUpgrade(r.getLevel());
            lst.add(srvPlayerUpgrade);
        }
        // lhx 晋级界面的消息
        else if (baseMsg instanceof RiseInRankWaitPush) {
            RiseInRankWaitPush resp = (RiseInRankWaitPush) baseMsg;

            String avatar = resp.getAvatar();
            String compInfo = resp.getCompInfo();
            String tips = resp.getTips();
            String prizeInfo = resp.getPrizeInfo();
            String ruleInfo = resp.getRuleInfo();

            ServerRisedWait srvRisedWait = new ServerRisedWait(avatar, compInfo, tips, prizeInfo,
                    ruleInfo);
            lst.add(srvRisedWait);

        }
        // lhx 缓存roominfo 在晋级等待界面做处理
        else if (baseMsg instanceof RoomInfoPush) {

            RoomInfoPush resp = (RoomInfoPush) baseMsg;

            infos = new ServerRoomInfos();

            infos.setGameType(resp.getMatchType());

            infos.setMaxRound(resp.getMaxRound());

            infos.setTeamId(resp.getTeamId());

            infos.setPlayers(resp.getPlayers());
            
            infos.setInningNum(resp.getInningsNum());
        }
        else if (baseMsg instanceof PlsReadyPush) {

            PlsReadyPush resp = (PlsReadyPush) baseMsg;

            infos.setmPlsReadyTime(resp.getWaitTime());

            lst.add(infos);
        }

        return lst;
    }

    // type about show follow
    private boolean isPlayTypeShow(int type) {
        return (type == Constants.SHOW_CARD_TYPE);
    }

    // type about show follow
    private int getPlayType(boolean isFollow) {
        if (isFollow) {
            return Constants.FOLLOW_CARD_TYPE;
        } else {
            return Constants.SHOW_CARD_TYPE;
        }
    }

    // public ArrayList<ClientMessage>
    // fromBaseMessageToClientMessage(BaseMessage baseMsg) {
    // ArrayList<ClientMessage> lst = new ArrayList<ClientMessage>();
    // // TODO
    // return lst;
    // }

}
