package com.rriSecond;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class HoneyGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Beekeeper beekeeper;
    private Array<GameObjectDynamic> dynamicActors;
    private Score score;
    private End end;


    @Override
    public void create() {

        Assets.Load();
        Gdx.app.setLogLevel(Logger.DEBUG);

        score=new Score(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0,100);
        end=new End(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        // create a Rectangle to logically represents the beekeeper
        beekeeper = new Beekeeper(Assets.beekeeperImage.getWidth(),Assets.beekeeperImage.getHeight());
        dynamicActors= new Array<>();
        Assets.backgroundSound.play();
        // add first honey and bee
        spawnHoney();
        spawnBee();
    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        // clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);


        // process user input
        if(!score.isEnd()) {
            if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Keys.LEFT)) beekeeper.commandMoveLeft();
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) beekeeper.commandMoveRight();
            if (Gdx.input.isKeyPressed(Keys.A)) beekeeper.commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Keys.S)) beekeeper.commandMoveRightCorner();
            if (Gdx.input.isKeyPressed(Keys.ESCAPE)) commandExitGame();
        }
        if(!score.isEnd()){
                for (GameObjectDynamic dynamicActor : dynamicActors)
                    dynamicActor.update(Gdx.graphics.getDeltaTime());

            if (Honey.isItTimeForNewHoney()) spawnHoney();
            if (Bee.isItTimeForNewBee()) spawnBee();
        }

        batch.begin();
        {    // brackets added just for indent
            batch.draw(Assets.backgroundImage, 0, 0);
            beekeeper.render(batch);
            for (GameObjectDynamic dynamicActor : dynamicActors) {
                dynamicActor.render(batch);

            }
            score.render(batch);
            if(score.isEnd()){
                end.render(batch);
            }
        }
        batch.end();
        if(!score.isEnd()) {
            for (Iterator<GameObjectDynamic> it = dynamicActors.iterator(); it.hasNext(); ) {
                GameObjectDynamic dynamicActor = it.next();
                dynamicActor.bounds.y -= dynamicActor.velocity.y * Gdx.graphics.getDeltaTime();
                if (dynamicActor.bounds.y + dynamicActor.bounds.height < 0) it.remove();
                if (dynamicActor.bounds.overlaps(beekeeper.bounds)) {
                  dynamicActor.updateScore(score);
                  if(dynamicActor instanceof Honey)
                    it.remove();
                    // speeds up

                }
            }

        }

    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {

        Assets.dispose();
    }

    private void spawnHoney() {
        dynamicActors.add(new Honey(Assets.honeyImage.getWidth(),Assets.honeyImage.getHeight()));
        Honey.lastHoneyTime = TimeUtils.nanoTime();
    }

    private void spawnBee() {
        dynamicActors.add(new Bee(Assets.beeImage.getWidth(),Assets.beeImage.getHeight()));
        Bee.lastBeeTime = TimeUtils.nanoTime();
    }

    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        beekeeper.bounds.x=touchPos.x - Assets.beekeeperImage.getWidth() / 2f;
        beekeeper.position.x=beekeeper.bounds.x;

    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
