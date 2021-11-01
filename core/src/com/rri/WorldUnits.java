package com.rri;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldUnits extends ApplicationAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private Viewport hudViewport;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private BitmapFont font;

    private DebugCameraController debugCameraController;
    private boolean debug = false;

    // world units
    private static final float WORLD_WIDTH = 600f;
    private static final float WORLD_HEIGHT = 300f;

    /**
     * Called when the Application is first created.
     */
    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2);
        // font = new BitmapFont(); // Creates a BitmapFont using the default 15pt Arial font included in the libgdx JAR file.

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
    }

    /**
     * Called when the Application is resized.
     *
     * @param width  the new width in pixels
     * @param height the new height in pixels
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
        ViewportUtils.debugPixelsPerUnit(viewport);
    }

    /**
     * Called when the Application should render itself. Runs every frame.
     */
    @Override
    public void render() {
        // clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            debugCameraController.applyTo(camera);
        }

        hudViewport.apply();
        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();

        draw();

        batch.end();

        if (debug) {
            renderDebug();
        }
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

    private void renderDebug() {
        ViewportUtils.drawGrid(viewport, renderer, 30);

        viewport.apply();

        Color oldColor = new Color(renderer.getColor());
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        drawDebug();

        renderer.end();
        renderer.setColor(oldColor);
    }

    private void drawDebug() {
        renderer.setColor(Color.CYAN);
        renderer.circle(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 50f, 25);
    }

    /**
     * Called when the Application is destroyed. Release all native resources.
     */
    @Override
    public void dispose() {
        renderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
