package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

abstract public class GameObjectDynamic extends GameObject {
    public final Vector2 velocity;
    public GameObjectDynamic(){
        super();
    this.velocity=new Vector2();
    }
    public GameObjectDynamic(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity=new Vector2();

    }
    public void update(float deltaTime) {
        bounds.y -= velocity.y * deltaTime;

    }

    public void init(float x, float y,float width,float height) {
        this.bounds.height=height;
        this.bounds.width=width;
        this.bounds.x=x;
        this.bounds.y=y;

    }
    abstract public void updateScore(Score score);

}
