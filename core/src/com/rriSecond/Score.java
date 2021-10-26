package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Score extends GameObject{

    public int honeyCollectedScore;
    public int beekeeperHealth;
   // public static boolean pause=true;
    public Score(float x, float y, float width, float height,int honeyCollectedScore,int beekeeperHealth) {
        super(x, y, width, height);
        this.honeyCollectedScore=honeyCollectedScore;
        this.beekeeperHealth=beekeeperHealth;

    }
    @Override
    public void render(SpriteBatch batch) {
        Assets.font.setColor(Color.YELLOW);
        Assets.font.draw(batch, "" + honeyCollectedScore, bounds.width - 50, bounds.height - 20);
        Assets.font.setColor(Color.GREEN);
        Assets.font.draw(batch, "" + beekeeperHealth, 20, bounds.height - 20);
        if(powerUpActivated()) {
            Assets.font.setColor(Color.GREEN);
            Assets.font.draw(batch, "" + (10000-(TimeUtils.millis() - AntiBeeSpray.lastActivatedTime)), Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 50);
        }
    }

    public boolean isEnd() {
        return (beekeeperHealth<=0);
    }
    public static boolean powerUpActivated() {
        return (TimeUtils.millis() - AntiBeeSpray.lastActivatedTime < 10000);
    }
   // public boolean isPaused(){return pause;}
    /*public static void pauseGame () {
        pause = !pause;
    }*/
}
