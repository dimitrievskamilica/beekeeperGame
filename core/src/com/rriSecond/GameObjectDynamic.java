package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class GameObjectDynamic extends GameObject{
    public final Vector2 velocity;
    public GameObjectDynamic(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity=new Vector2();

    }
    public void update(float deltaTime) {
        bounds.y -= velocity.y * Gdx.graphics.getDeltaTime();

    }
}
