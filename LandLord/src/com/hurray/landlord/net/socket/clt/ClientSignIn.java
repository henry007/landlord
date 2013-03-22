package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class ClientSignIn extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 7062498254511344916L;
    
    private int mGameType;
    
    public ClientSignIn(int gameType) {
        super(CLT_SIGN_IN);
        mGameType = gameType;
    }
    
    public int getGameType() {
        return mGameType;
    }
    
    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("game_type", mGameType);
            
            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return jsonString;
    }

}
