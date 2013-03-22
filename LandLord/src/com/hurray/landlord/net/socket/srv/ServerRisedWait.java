
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerRisedWait extends ServerMessage {

    private String avatar;// String 形象
    private String compInfo; // String 完成描述
    private String tips; // String 提示信息
    private String prizeInfo;// String 奖品信息
    private String ruleInfo; // String 规则信息

    public ServerRisedWait(String avatar, String compInfo, String tips,
            String prizeInfo, String ruleInfo) {
        super(ServerMessage.SRV_RISED_WAIT);
        this.avatar = avatar;
        this.compInfo = compInfo;
        this.tips = tips;
        this.prizeInfo = prizeInfo;
        this.ruleInfo = ruleInfo;
        
    }

    private static final long serialVersionUID = 2886309629472632885L;

    @Override
    protected void unPackJson(String jsonString) {
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {

            avatar = getJsonString(json, "avatar");
            compInfo = getJsonString(json, "compInfo");
            tips = getJsonString(json, "tips");
            prizeInfo = getJsonString(json, "prizeInfo");
            ruleInfo = getJsonString(json, "ruleInfo");

        }

    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompInfo() {
        return compInfo;
    }

    public void setCompInfo(String compInfo) {
        this.compInfo = compInfo;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPrizeInfo() {
        return prizeInfo;
    }

    public void setPrizeInfo(String prizeInfo) {
        this.prizeInfo = prizeInfo;
    }

    public String getRuleInfo() {
        return ruleInfo;
    }

    public void setRuleInfo(String ruleInfo) {
        this.ruleInfo = ruleInfo;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
