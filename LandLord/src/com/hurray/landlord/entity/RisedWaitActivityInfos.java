
package com.hurray.landlord.entity;

import java.io.Serializable;

public class RisedWaitActivityInfos implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7934195429271145604L;
    private String avatar;// String 形象
    private String compInfo; // String 完成描述
    private String tips; // String 提示信息
    private String prizeInfo;// String 奖品信息
    private String ruleInfo; // String 规则信息

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

}
