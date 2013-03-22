package com.hurray.landlord.entity;

public class OnlineHomeInfo {
    
    String nickName = null;
    
    int curExp = -1;
    
    int nextExp = -1;
    
    int level = -1;
    
    int hairId = -1;
    
    int clothesId = -1;
    
    boolean isMale = false;
    
    boolean isGest = false;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getCurExp() {
        return curExp;
    }

    public void setCurExp(int exp) {
        this.curExp = exp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHairId() {
        return hairId;
    }

    public void setHairId(int hairId) {
        this.hairId = hairId;
    }

    public int getClothesId() {
        return clothesId;
    }

    public void setClothesId(int clothesId) {
        this.clothesId = clothesId;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    public int getNextExp() {
        return nextExp;
    }

    public void setNextExp(int nextExp) {
        this.nextExp = nextExp;
    }

    public boolean isGest() {
        return isGest;
    }

    public void setGest(boolean isGest) {
        this.isGest = isGest;
    }

}
