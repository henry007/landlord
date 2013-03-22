
package com.hurray.landlord.entity;

public class GameInfo {

    private int mGameType;

    private String mName;
    
    private int mPlayerNum;
    
    private String mMatchTime;

    private String mDesc;
    
    private String mGameInfo; //lhx 按钮弹出的界面信息

    private int mTotalNum;

    private int mFaleNum;

    private boolean mIsShowDesc;

    public int getGameType() {
        return mGameType;
    }

    public void setGameType(int gameType) {
        mGameType = gameType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public Integer getTotalNum() {
        return mTotalNum;
    }

    public void setOnlineNum(int totalNum) {
        mTotalNum = totalNum;
    }

    public Integer getFaleNum() {
        return mFaleNum;
    }

    public void setFaleNum(int faleNum) {
        mFaleNum = faleNum;
    }

    public boolean isShowDesc() {
        return mIsShowDesc;
    }

    public void setIsShowDesc(boolean isShowDesc) {
        mIsShowDesc = isShowDesc;
    }

    public String getGameInfo() {
        return mGameInfo;
    }

    public void setGameInfo(String mGameInfo) {
        this.mGameInfo = mGameInfo;
    }

    public int getmPlayerNum() {
        return mPlayerNum;
    }

    public void setmPlayerNum(int mPlayerNum) {
        this.mPlayerNum = mPlayerNum;
    }

    public String getmMatchTime() {
        return mMatchTime;
    }

    public void setmMatchTime(String mMatchTime) {
        this.mMatchTime = mMatchTime;
    }

}
