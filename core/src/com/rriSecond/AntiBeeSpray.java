package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class AntiBeeSpray extends GameObjectDynamic implements Pool.Poolable{
    public static float speed=100;
    public static final long CREATE_ANTI_BEE_SPRAY_TIME = 2000000000;
    public static long lastAntiBeeSprayTime;
    public float rotate;
    public float rotateSpeed=30;
    public static long lastActivatedTime;
    public static final Pool<AntiBeeSpray> antiBeeSprayPool = Pools.get(AntiBeeSpray.class, 10);
    public AntiBeeSpray(){
        super();
        velocity.set(0,speed);
    }

    public AntiBeeSpray(float width, float height) {
        super(MathUtils.random(0, Gdx.graphics.getWidth() - width),
                Gdx.graphics.getHeight(),
                width,
                height);
        velocity.set(0,speed);
    }
    public static boolean isItTimeForNewAntiBeeSpray(){
        if (TimeUtils.nanoTime() - lastAntiBeeSprayTime > (CREATE_ANTI_BEE_SPRAY_TIME*2))
            return true;
        else return false;
    }
    @Override
    public void render(SpriteBatch batch){
        batch.draw(Assets.powerUpImage, bounds.x, bounds.y, bounds.width/2, bounds.height/2, bounds.width, bounds.height, 1, 1, rotate,0,0, (int)bounds.width, (int)bounds.height,false,false);

    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        rotate += deltaTime * rotateSpeed;
        if (rotate>360) rotate -= 360; //prevent high numbers
        if (rotate<-360) rotate += 360;

    }
    @Override
    public void updateScore(Score score){
        lastActivatedTime =TimeUtils.millis();
    }

    @Override
    public void reset() {
        init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.powerUpImage.getWidth()), Gdx.graphics.getHeight(),Assets.powerUpImage.getWidth(),Assets.powerUpImage.getHeight());
        velocity.set(0,speed);
    }

}
