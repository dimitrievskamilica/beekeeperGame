package com.rriSecond;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
public class GameObject {
    public final Vector2 position;
    public final Rectangle bounds;

    public GameObject() {
        this.position = new Vector2();
        this.bounds = new Rectangle();
    }
    public GameObject (float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x , y , width, height);
    }
    public void updateScore(Score score){

    }
    public void render(SpriteBatch batch) {

    }

}
