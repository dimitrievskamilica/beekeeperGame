package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Beekeeper extends GameObjectDynamic{

    public static float speed=600;

    public Beekeeper(float width,float height) {
        super(Gdx.graphics.getWidth() / 2f - width / 2f,
                20,
                width,
                height);
        velocity.set(speed,0);
    }
    @Override
    public void render(SpriteBatch batch){
        batch.draw(Assets.beekeeperImage, position.x, position.y);

    }
    public void commandMoveLeft() {
        bounds.x -= velocity.x * Gdx.graphics.getDeltaTime();
        if (bounds.x < 0)
            bounds.x=0;
        position.x=bounds.x;


    }

    public void commandMoveRight() {

        bounds.x += velocity.x * Gdx.graphics.getDeltaTime();
        if (bounds.x > Gdx.graphics.getWidth() - Assets.beekeeperImage.getWidth())
            bounds.x= Gdx.graphics.getWidth() - Assets.beekeeperImage.getWidth();
        position.x=bounds.x;

    }

    public void commandMoveLeftCorner() {

        position.x=0;
        bounds.x=0;
    }

    public void commandMoveRightCorner() {
        position.x=Gdx.graphics.getWidth() - Assets.beekeeperImage.getWidth();
        bounds.x=position.x;
    }

    @Override
    public void updateScore(Score score) {

    }
}
