
package com.hurray.landlord.game.data;

import com.hurray.lordserver.protocol.message.card.MatchEndPush.PlayersRank;

import java.io.Serializable;

public class GameResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7677255901928916065L;

    public String mNameOfRecord1;

    public int mSubTotalOfRecord1;

    public int mTotalOfRecord1;

    public String mNameOfRecord2;

    public int mSubTotalOfRecord2;

    public int mTotalOfRecord2;

    public String mNameOfRecord3;

    public int mSubTotalOfRecord3;

    public int mTotalOfRecord3;

    public int mLeftGameNum;

    public int mWinWinNum;
    
    public int mWinNum;
    
    public int mLoseNum;
    
    //是否晋级
    public boolean isRised;
    
    private PlayersRank[] results;
    
}
