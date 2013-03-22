
package com.hurray.landlord.game.local;

public abstract class GameStep {

    abstract protected void start();

    abstract protected boolean loop();

    private boolean isFirstRun = true;
    
    public void reset() {
        isFirstRun = true;
    }

    public boolean run() {
        if (isFirstRun) {
            isFirstRun = false;
            start();
        }

        if (loop()) {
            isFirstRun = true;
            return true;
        }
        
        return false;
    }
}
