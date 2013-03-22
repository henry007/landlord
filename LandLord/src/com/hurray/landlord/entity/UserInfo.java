
package com.hurray.landlord.entity;

import java.io.Serializable;

/**
 * 用户信息
 * 
 * @author yinjiaqing
 */
public class UserInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2132899013828997209L;
    
    private Long mUserId; //
    private String mAvatarUrl; // 头像
    private String mNickname; // 昵称
    private String mSignature; // 签名
    private Integer mExperience; // 拥有经验
    private Integer mNextExperience; // 升级经验
    private Integer mLevel; // 级别
    private Integer mVictoryNumber; // 胜利次数
    private Integer mFailureNumber; // 失败次数
    private Gender mGender; // 性别
    private Integer mGold; // 金币
    private Integer mMoney; // 元宝
    private PlayersStatus mStatus; // 状态
    private String mRank; // 头衔
    private String[] mAchievements; // 拥有成就
    private Integer mTotalAchievement; // 总的成就数
    private Integer mTopLevelNum; // 等级排名
    private Integer mTopWealthNum; // 财富排名
    private Integer mScore; // 分数
    public String userId; //用户名
    public String value;

    public UserInfo() {
    }

    public UserInfo(Long mUserId) {
        this.mUserId = mUserId;
    }

    public UserInfo(String mNickname, Integer mScore) {
        this.mNickname = mNickname;
        this.mScore = mScore;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long mUserId) {
        this.mUserId = mUserId;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String mAvatarUrl) {
        this.mAvatarUrl = mAvatarUrl;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String mNickname) {
        this.mNickname = mNickname;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String mSignature) {
        this.mSignature = mSignature;
    }

    public Integer getExperience() {
        return mExperience;
    }

    public void setExperience(Integer mExperience) {
        this.mExperience = mExperience;
    }

    public Integer getNextExperience() {
        return mNextExperience;
    }

    public void setNextExperience(Integer mNextExperience) {
        this.mNextExperience = mNextExperience;
    }

    public Integer getLevel() {
        return mLevel;
    }

    public void setLevel(Integer mLevel) {
        this.mLevel = mLevel;
    }

    public Integer getVictoryNumber() {
        return mVictoryNumber;
    }

    public void setVictoryNumber(Integer mVictoryNumber) {
        this.mVictoryNumber = mVictoryNumber;
    }

    public Integer getFailureNumber() {
        return mFailureNumber;
    }

    public void setFailureNumber(Integer mFailureNumber) {
        this.mFailureNumber = mFailureNumber;
    }

    public Gender getGender() {
        return mGender;
    }

    public void setGender(Gender mGender) {
        this.mGender = mGender;
    }

    public Integer getGold() {
        return mGold;
    }

    public void setGold(Integer mGold) {
        this.mGold = mGold;
    }

    public Integer getMoney() {
        return mMoney;
    }

    public void setMoney(Integer mMoney) {
        this.mMoney = mMoney;
    }

    public PlayersStatus getStatus() {
        return mStatus;
    }

    public void setStatus(PlayersStatus mStatus) {
        this.mStatus = mStatus;
    }

    public String getRank() {
        return mRank;
    }

    public void setRank(String mRank) {
        this.mRank = mRank;
    }

    public String[] getAchievements() {
        return mAchievements;
    }

    public void setAchievements(String[] mAchievements) {
        this.mAchievements = mAchievements;
    }

    public Integer getTotalAchievement() {
        return mTotalAchievement;
    }

    public void setTotalAchievement(Integer mTotalAchievement) {
        this.mTotalAchievement = mTotalAchievement;
    }

    public Integer getTopLevelNum() {
        return mTopLevelNum;
    }

    public void setTopLevelNum(Integer mTopLevelNum) {
        this.mTopLevelNum = mTopLevelNum;
    }

    public Integer getTopWealthNum() {
        return mTopWealthNum;
    }

    public void setTopWealthNum(Integer mTopWealthNum) {
        this.mTopWealthNum = mTopWealthNum;
    }

    public Integer getScore() {
        return mScore;
    }

    public void setScore(Integer mScore) {
        this.mScore = mScore;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue(String value){
    	return this.value;
    }
    
    public void setValue(String value){
    	this.value = value;
    }

}
