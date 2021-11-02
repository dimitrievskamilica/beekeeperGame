package com.rri;

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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rriSecond.AntiBeeSpray;
import com.rriSecond.Assets;
import com.rriSecond.Bee;
import com.rriSecond.GameObjectDynamic;
import com.rriSecond.Honey;

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
    private static final long CREATE_BEE_TIME = 2000000000;// ns

    // debug
    private DebugCameraController debugCameraController;
    private MemoryInfo memoryInfo;
    private boolean debug = false;

    private ShapeRenderer renderer;
    public Viewport viewport;

    private Viewport hudViewport;

    // world units
    private static final float WORLD_WIDTH = 600f;
    private static final float WORLD_HEIGHT = 300f;

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

        // debug
        memoryInfo = new MemoryInfo(500);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);

        renderer = new ShapeRenderer();


        // create a Rectangle to logically represents the beekeeper
        beekeeper = new Rectangle();
        beekeeper.x = WORLD_WIDTH / 2f - beekeeperImage.getWidth() / 2f;
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            memoryInfo.update();
        }

        // process user input
        if (beekeeperHealth > 0) {
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
                Rectangle bee = it.next();
                bee.y -= SPEED_BEE * Gdx.graphics.getDeltaTime();
                if (bee.y + beeImage.getHeight() < 0) it.remove();
                if (bee.overlaps(beekeeper)) {
                    honeySound.play();
                    beekeeperHealth--;
                }
            }

            for (Iterator<Rectangle> it = jarsOfHoney.iterator(); it.hasNext(); ) {
                Rectangle jarOfHoney = it.next();
                jarOfHoney.y -= SPEED_HONEY * Gdx.graphics.getDeltaTime();
                if (jarOfHoney.y + honeyImage.getHeight() < 0) it.remove();    // from screen
                if (jarOfHoney.overlaps(beekeeper)) {
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
                font.draw(batch, "The END", WORLD_HEIGHT / 2f, WORLD_WIDTH / 2f);
            }
            batch.end();
        }

        // tell the camera to update its matrices.
        camera.update();
        viewport.apply();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the beekeeper, jarsOfHoney, bees
        batch.begin();
        {    // brackets added just for indent
            batch.draw(backgroundImage, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(beekeeperImage, beekeeper.x, beekeeper.y);
            for (Rectangle bee : bees) {
                batch.draw(beeImage, bee.x, bee.y);
            }
            for (Rectangle jarOfHoney : jarsOfHoney) {
                batch.draw(honeyImage, jarOfHoney.x, jarOfHoney.y);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + honeyCollectedScore, WORLD_WIDTH - 50, WORLD_HEIGHT - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + beekeeperHealth, WORLD_WIDTH - 70, WORLD_HEIGHT - 50);
        }
        batch.end();
        if (debug) {
            debugCameraController.applyTo(camera);

            hudViewport.apply();
            batch.setProjectionMatrix(hudViewport.getCamera().combined);
            batch.begin();
            {
                draw();
                GlyphLayout layout = new GlyphLayout(font, "FPS:" + Gdx.graphics.getFramesPerSecond());
                font.setColor(Color.YELLOW);
                font.draw(batch, layout, Gdx.graphics.getWidth() - layout.width, Gdx.graphics.getHeight() - 50);

                // number of rendering calls, ever; will not be reset unless set manually
                font.setColor(Color.YELLOW);
                font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 20);

                memoryInfo.render(batch, font);
            }
            batch.end();
            batch.totalRenderCalls = 0;
            ViewportUtils.drawGrid(viewport, renderer, 50);
            renderer.setProjectionMatrix(camera.combined);
            // https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
            renderer.begin(ShapeRenderer.ShapeType.Line);
            {
                renderer.setColor(1, 1, 0, 1);
                    for (Rectangle bee : bees) {
                        renderer.rect(bee.x, bee.y, beeImage.getWidth(), beeImage.getHeight());
                    }
                    for (Rectangle jarOfHoney : jarsOfHoney) {
                        renderer.rect(jarOfHoney.x, jarOfHoney.y, honeyImage.getWidth(), honeyImage.getHeight());
                    }
                renderer.rect(beekeeper.x, beekeeper.y, beekeeperImage.getWidth(), beekeeperImage.getHeight());

            }
            renderer.end();
        }
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
        honey.x = MathUtils.random(0, WORLD_WIDTH - honeyImage.getWidth());
        honey.y = WORLD_HEIGHT;
        honey.width = honeyImage.getWidth();
        honey.height = honeyImage.getHeight();
        jarsOfHoney.add(honey);
        lastHoneyTime = TimeUtils.nanoTime();
    }

    private void spawnBee() {
        Rectangle bee = new Rectangle();
        bee.x = MathUtils.random(0, WORLD_WIDTH - honeyImage.getWidth());
        bee.y = WORLD_HEIGHT;
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
        if (beekeeper.x > WORLD_WIDTH - beekeeperImage.getWidth())
            beekeeper.x = WORLD_WIDTH - beekeeperImage.getWidth();

    }

    private void commandMoveLeftCorner() {
        beekeeper.x = 0;
    }

    private void commandMoveRightCorner() {
        beekeeper.x = WORLD_WIDTH - beekeeperImage.getWidth();
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelsPerUnit(viewport);
    }

    private void draw() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        String screenSize = "Screen/Window size: " + screenWidth + " x " + screenHeight + " px";
        String worldSize = "World size: " + (int) worldWidth + " x " + (int) worldHeight + " world units";
        String oneWorldUnit = "One world unit: " + (screenWidth / worldWidth) + " x " + (screenHeight / worldHeight) + " px";


        font.draw(batch,
                screenSize,
                20f,
                hudViewport.getWorldHeight() - 20f);

        font.draw(batch,
                worldSize,
                20f,
                hudViewport.getWorldHeight() - 50f);

        font.draw(batch,
                oneWorldUnit,
                20f,
                hudViewport.getWorldHeight() - 80f);
    }

}
