package com.rriSecond;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private boolean pause=false;


    @Override
    public void create() {

        Assets.Load();
        //Gdx.app.setLogLevel(Logger.DEBUG);

        score=new Score(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),0,100);
        end=new End(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        //pause=new Pause(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

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
        spawnPowerUp();
    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        if(score.isEnd()){
            batch.begin();
            {
                end.render(batch);
            }
            batch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                create();
            }
        }else if(pause){
            batch.begin();
            {
                Assets.font.setColor(Color.YELLOW);
                Assets.font.draw(batch, "PAUSED", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
                Assets.backgroundSound.stop();
            }
            batch.end();
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) pause = !pause;
        }else{
            if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) beekeeper.commandMoveLeft();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) beekeeper.commandMoveRight();
            if (Gdx.input.isKeyPressed(Input.Keys.A)) beekeeper.commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Input.Keys.S)) beekeeper.commandMoveRightCorner();
            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) pause = !pause;
            for (GameObjectDynamic dynamicActor : dynamicActors)
                dynamicActor.update(Gdx.graphics.getDeltaTime());

            if (Honey.isItTimeForNewHoney()) spawnHoney();
            if (Bee.isItTimeForNewBee()) spawnBee();
            if (AntiBeeSpray.isItTimeForNewAntiBeeSpray()) spawnPowerUp();

            batch.begin();
            {    // brackets added just for indent
                batch.draw(Assets.backgroundImage, 0, 0);
                beekeeper.render(batch);
                for (GameObjectDynamic dynamicActor : dynamicActors) {
                    dynamicActor.render(batch);

                }
                score.render(batch);
                Assets.font.draw(batch, "Active elements " + dynamicActors.size, 20, Gdx.graphics.getHeight() - 40);
                Assets.font.draw(batch, "Cheeses in pool " + Honey.honeyPool.getFree(), 20, Gdx.graphics.getHeight() - 60);
            }
            batch.end();
            for (Iterator<GameObjectDynamic> it = dynamicActors.iterator(); it.hasNext(); ) {
                GameObjectDynamic dynamicActor = it.next();
                //dynamicActor.bounds.y -= dynamicActor.velocity.y * Gdx.graphics.getDeltaTime();
                if (dynamicActor.bounds.y + dynamicActor.bounds.height < 0) {
                    it.remove();
                    if (dynamicActor instanceof Honey) {
                        Honey.honeyPool.free((Honey) dynamicActor);
                    }
                    if (dynamicActor instanceof Bee && Score.powerUpActivated()) {
                        Bee.beePool.free((Bee)dynamicActor);
                    }
                    if(dynamicActor instanceof AntiBeeSpray){
                        AntiBeeSpray.antiBeeSprayPool.free((AntiBeeSpray) dynamicActor);
                    }
                }
                if (dynamicActor.bounds.overlaps(beekeeper.bounds)) {
                    dynamicActor.updateScore(score);
                    if (dynamicActor instanceof Honey) {
                        it.remove();
                        Honey.honeyPool.free((Honey) dynamicActor);
                    }
                    if (dynamicActor instanceof Bee && Score.powerUpActivated()) {
                        it.remove();
                        Bee.beePool.free((Bee)dynamicActor);
                    }
                    if(dynamicActor instanceof AntiBeeSpray){
                        it.remove();
                        AntiBeeSpray.antiBeeSprayPool.free((AntiBeeSpray) dynamicActor);
                    }
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
        batch.dispose();
    }

    private void spawnHoney() {
        Honey honey = Honey.honeyPool.obtain();
        honey.init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.honeyImage.getWidth()), Gdx.graphics.getHeight(),Assets.honeyImage.getWidth(),Assets.honeyImage.getHeight());
        dynamicActors.add(honey);
        //dynamicActors.add(new Honey(Assets.honeyImage.getWidth(),Assets.honeyImage.getHeight()));
        Honey.lastHoneyTime = TimeUtils.nanoTime();
    }

    private void spawnBee() {
        Bee bee = Bee.beePool.obtain();
        bee.init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.beeImage.getWidth()), Gdx.graphics.getHeight(),Assets.beeImage.getWidth(),Assets.beeImage.getHeight());
        dynamicActors.add(bee);
       // dynamicActors.add(new Bee(Assets.beeImage.getWidth(),Assets.beeImage.getHeight()));
        Bee.lastBeeTime = TimeUtils.nanoTime();
    }
    private void spawnPowerUp() {
        AntiBeeSpray antiBeeSpray = AntiBeeSpray.antiBeeSprayPool.obtain();
        antiBeeSpray.init(MathUtils.random(0, Gdx.graphics.getWidth() - Assets.powerUpImage.getWidth()), Gdx.graphics.getHeight(),Assets.powerUpImage.getWidth(),Assets.powerUpImage.getHeight());
        dynamicActors.add(antiBeeSpray);
        //dynamicActors.add(new AntiBeeSpray(Assets.powerUpImage.getWidth(),Assets.powerUpImage.getHeight()));
        AntiBeeSpray.lastAntiBeeSprayTime = TimeUtils.nanoTime();
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
