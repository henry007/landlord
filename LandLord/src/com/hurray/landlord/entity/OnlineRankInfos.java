package com.hurray.landlord.entity;

import java.io.Serializable;

public class OnlineRankInfos implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 8846069781486019833L;

    private String[] rank;
    
    private String[] nickname;
    
    private String[] score;

    public String[] getRank() {
        return rank;
    }

    public void setRank(String[] rank) {
        this.rank = rank;
    }

    public String[] getNickname() {
        return nickname;
    }

    public void setNickname(String[] nickname) {
        this.nickname = nickname;
    }

    public String[] getScore() {
        return score;
    }

    public void setScore(String[] score) {
        this.score = score;
    }
}
