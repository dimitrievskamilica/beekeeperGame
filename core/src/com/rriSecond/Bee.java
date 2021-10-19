package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

public class Bee extends GameObjectDynamic{

    public static float speed=100;
    public static final long CREATE_BEE_TIME = 2000000000;
    public static long lastBeeTime;
    public float rotate;
    public float rotateSpeed=30;

    public Bee(float width,float height) {
        super(MathUtils.random(0, Gdx.graphics.getWidth() - width),
                Gdx.graphics.getHeight(),
                width,
                height);
        velocity.set(0,speed);
    }
    public static boolean isItTimeForNewBee(){
        if (TimeUtils.nanoTime() - lastBeeTime > CREATE_BEE_TIME)
            return true;
        else return false;
    }
    @Override
    public void render(SpriteBatch batch){
        //batch.draw(Assets.beeImage, position.x, position.y);
        batch.draw(Assets.beeImage, bounds.x, bounds.y, bounds.width/2, bounds.height/2, bounds.width, bounds.height, 1, 1, rotate,0,0, (int)bounds.width, (int)bounds.height,false,false);

    }
    public void update(float deltaTime) {
        super.update(deltaTime);
        rotate += deltaTime * rotateSpeed;
        if (rotate>360) rotate -= 360; //prevent high numbers
        if (rotate<-360) rotate += 360;

    }
    @Override
    public void updateScore(Score score){
        Assets.honeySound.play();
        score.beekeeperHealth--;
    }


}
