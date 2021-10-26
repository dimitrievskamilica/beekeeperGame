package com.rriBouncingBall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class HoneyGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture ballImage;
    private Array<Circle> balls;
    private static double SPEED_BALL = 0;
    private static double MAX_SPEED;
    boolean up=false;
    private double firstY;

    public BitmapFont font;

    @Override
    public void create() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        ballImage = new Texture(Gdx.files.internal("bouncingBall.png"));

        Circle ball = new Circle();
        ball.setRadius(3);

        balls = new Array<Circle>();
        //spawnBall();
    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        if (Gdx.input.justTouched())
            spawnBall(camera);
        for (Circle ball : balls) {
            System.out.printf("%b%n", up);
            if(up){
                if(SPEED_BALL<1){
                    up=false;
                    //firstY=firstY-(firstY*0.25);
                    //ball.y >= (firstY*0.8)
                    firstY=firstY*0.8;
                }else {
                    ball.y += SPEED_BALL * Gdx.graphics.getDeltaTime();
                    SPEED_BALL -= 10;
                }

            }
            else {
                if(ball.y < 0){
                    up=true;
                    SPEED_BALL = SPEED_BALL-(SPEED_BALL * 0.1);
                    if(SPEED_BALL<10){SPEED_BALL=0; ball.y=0;}

                }else {
                    ball.y -= SPEED_BALL * Gdx.graphics.getDeltaTime();
                    SPEED_BALL += 10;
                }
            }

        }
        batch.begin();
        {
            for (Circle ball : balls) {
                batch.draw(ballImage, ball.x, ball.y);
            }
        }
        batch.end();

    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        ballImage.dispose();
        batch.dispose();
        font.dispose();
    }

    private void spawnBall(OrthographicCamera camera) {
        Circle ball = new Circle();
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        ball.x = touchPos.x - ballImage.getWidth() / 2f;
        ball.y = touchPos.y - ballImage.getHeight() / 2f;
        firstY=ball.y;
        balls.add(ball);

    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
