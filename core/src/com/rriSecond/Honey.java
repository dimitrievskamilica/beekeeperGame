package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Honey extends GameObjectDynamic{

    public static float speed=200;
    public static final long CREATE_HONEY_TIME = 1000000000;
    public static long lastHoneyTime;
    public float rotate;
    public float rotateSpeed=200;

    public Honey(float width,float height) {

        super(MathUtils.random(0, Gdx.graphics.getWidth() - width),
                Gdx.graphics.getHeight(),
                width,
                height);
        velocity.set(0,speed);

    }
    public static boolean isItTimeForNewHoney(){
        if (TimeUtils.nanoTime() - lastHoneyTime > CREATE_HONEY_TIME)
            return true;
            else return false;
    }
    @Override
    public void render(SpriteBatch batch){
      //  batch.draw(Assets.honeyImage, position.x, position.y);
        batch.draw(Assets.honeyImage, bounds.x, bounds.y, bounds.width/2, bounds.height/2, bounds.width, bounds.height, 1, 1, rotate,0,0, (int)bounds.width, (int)bounds.height,false,false);

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

        Assets.honeySound.play();
        score.honeyCollectedScore++;
        if (score.honeyCollectedScore % 10 == 0) Bee.speed+=66;
        System.out.printf("%f%n", Bee.speed);

    }

}
