package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    public static Texture honeyImage;
    public static Texture beekeeperImage;
    public static Texture beeImage;
    public static Texture backgroundImage;
    public static Texture powerUpImage;
    public static Sound honeySound;
    public static Sound backgroundSound;
    public static BitmapFont font;

    public static void Load() {
        beekeeperImage = new Texture(Gdx.files.internal("beekeeper.png"));
        honeyImage = new Texture(Gdx.files.internal("honey.png"));
        beeImage = new Texture(Gdx.files.internal("bee92.png"));
        honeySound = Gdx.audio.newSound(Gdx.files.internal("collect.wav"));
        backgroundImage = new Texture(Gdx.files.internal("bg.png"));
        backgroundSound = Gdx.audio.newSound(Gdx.files.internal("backgroundMusic.wav"));
        powerUpImage=new Texture(Gdx.files.internal("powerUp.png"));
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    public static void dispose() {
        honeyImage.dispose();
        beekeeperImage.dispose();
        honeySound.dispose();
        powerUpImage.dispose();
        backgroundImage.dispose();
        backgroundSound.dispose();
        font.dispose();
    }

}
