package com.rri;

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
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class HoneyGame extends ApplicationAdapter {
    private Texture honeyImage;
    private Texture beekeeperImage;
    private Texture beeImage;
    private Texture backgroundImage;
    private Sound honeySound;
    private Sound backgroundSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle beekeeper;
    private Array<Rectangle> jarsOfHoney;    // special LibGDX Array
    private Array<Rectangle> bees;
    private long lastHoneyTime;
    private long lastBeeTime;
    private int honeyCollectedScore;
    private int beekeeperHealth;    // starts with 100

    public BitmapFont font;

    // all values are set experimental
    private static final int SPEED = 600;    // pixels per second
    private static final int SPEED_HONEY = 200; // pixels per second
    private static int SPEED_BEE = 100;    // pixels per second
    private static final long CREATE_HONEY_TIME = 1000000000;    // ns
    private static final long CREATE_BEE_TIME = 2000000000;    // ns

    @Override
    public void create() {
        font = new BitmapFont();
        font.getData().setScale(2);
        honeyCollectedScore = 0;
        beekeeperHealth = 100;

        // default way to load a texture
        beekeeperImage = new Texture(Gdx.files.internal("beekeeper.png"));
        honeyImage = new Texture(Gdx.files.internal("honey.png"));
        beeImage = new Texture(Gdx.files.internal("bee92.png"));
        backgroundImage = new Texture(Gdx.files.internal("bg.png"));
        backgroundSound = Gdx.audio.newSound(Gdx.files.internal("backgroundMusic.wav"));
        honeySound = Gdx.audio.newSound(Gdx.files.internal("collect.wav"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        // create a Rectangle to logically represents the beekeeper
        beekeeper = new Rectangle();
        beekeeper.x = Gdx.graphics.getWidth() / 2f - beekeeperImage.getWidth() / 2f;    // center the beekeeper horizontally
        beekeeper.y = 20;    // bottom left corner of the rocket is 20 pixels above the bottom screen edge
        beekeeper.width = beekeeperImage.getWidth();
        beekeeper.height = beekeeperImage.getHeight();

        jarsOfHoney = new Array<Rectangle>();
        bees = new Array<Rectangle>();
        backgroundSound.play();
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

        // process user input
        if(beekeeperHealth>0) {
            if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
            if (Gdx.input.isKeyPressed(Keys.LEFT)) commandMoveLeft();
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) commandMoveRight();
            if (Gdx.input.isKeyPressed(Keys.A)) commandMoveLeftCorner();
            if (Gdx.input.isKeyPressed(Keys.S)) commandMoveRightCorner();
            if (Gdx.input.isKeyPressed(Keys.ESCAPE)) commandExitGame();
        }

        // check if we need to create a new honey/bee
        if (TimeUtils.nanoTime() - lastHoneyTime > CREATE_HONEY_TIME) spawnHoney();
        if (TimeUtils.nanoTime() - lastBeeTime > CREATE_BEE_TIME) spawnBee();

        if (beekeeperHealth > 0) {    // is game end?
            // move and remove any that are beneath the bottom edge of
            // the screen or that hit the beekeeper
            for (Iterator<Rectangle> it = bees.iterator(); it.hasNext(); ) {
                Rectangle asteroid = it.next();
                asteroid.y -= SPEED_BEE * Gdx.graphics.getDeltaTime();
                if (asteroid.y + beeImage.getHeight() < 0) it.remove();
                if (asteroid.overlaps(beekeeper)) {
                    honeySound.play();
                    beekeeperHealth--;
                }
            }

            for (Iterator<Rectangle> it = jarsOfHoney.iterator(); it.hasNext(); ) {
                Rectangle astronaut = it.next();
                astronaut.y -= SPEED_HONEY * Gdx.graphics.getDeltaTime();
                if (astronaut.y + honeyImage.getHeight() < 0) it.remove();    // from screen
                if (astronaut.overlaps(beekeeper)) {
                    honeySound.play();
                    honeyCollectedScore++;
                    if (honeyCollectedScore % 10 == 0) SPEED_BEE += 66;    // speeds up
                    it.remove();    // smart Array enables remove from Array
                }
            }
        } else {    // health of beekeeper is 0 or less
            batch.begin();
            {
                font.setColor(Color.RED);
                font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
            }
            batch.end();
        }

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the beekeeper, jarsOfHoney, bees
        batch.begin();
        {    // brackets added just for indent
            batch.draw(backgroundImage,0,0);
            batch.draw(beekeeperImage, beekeeper.x, beekeeper.y);
            for (Rectangle bee : bees) {
                batch.draw(beeImage, bee.x, bee.y);
            }
            for (Rectangle jarOfHoney : jarsOfHoney) {
                batch.draw(honeyImage, jarOfHoney.x, jarOfHoney.y);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + honeyCollectedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + beekeeperHealth, 20, Gdx.graphics.getHeight() - 20);
        }
        batch.end();
    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        honeyImage.dispose();
        beeImage.dispose();
        beekeeperImage.dispose();
        backgroundImage.dispose();
        honeySound.dispose();
        backgroundSound.dispose();
        batch.dispose();
        font.dispose();
    }

    private void spawnHoney() {
        Rectangle honey = new Rectangle();
        honey.x = MathUtils.random(0, Gdx.graphics.getWidth() - honeyImage.getWidth());
        honey.y = Gdx.graphics.getHeight();
        honey.width = honeyImage.getWidth();
        honey.height = honeyImage.getHeight();
        jarsOfHoney.add(honey);
        lastHoneyTime = TimeUtils.nanoTime();
    }

    private void spawnBee() {
        Rectangle bee = new Rectangle();
        bee.x = MathUtils.random(0, Gdx.graphics.getWidth() - honeyImage.getWidth());
        bee.y = Gdx.graphics.getHeight();
        bee.width = beeImage.getWidth();
        bee.height = beeImage.getHeight();
        bees.add(bee);
        lastBeeTime = TimeUtils.nanoTime();
    }

    private void commandMoveLeft() {
        beekeeper.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (beekeeper.x < 0) beekeeper.x = 0;
    }

    private void commandMoveRight() {

            beekeeper.x += SPEED * Gdx.graphics.getDeltaTime();
            if (beekeeper.x > Gdx.graphics.getWidth() - beekeeperImage.getWidth())
                beekeeper.x = Gdx.graphics.getWidth() - beekeeperImage.getWidth();

    }

    private void commandMoveLeftCorner() {
        beekeeper.x = 0;
    }

    private void commandMoveRightCorner() {
        beekeeper.x = Gdx.graphics.getWidth() - beekeeperImage.getWidth();
    }

    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        beekeeper.x = touchPos.x - beekeeperImage.getWidth() / 2f;
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
