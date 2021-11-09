package com.rriSecond;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class Assets {
    public static Texture honeyImage;
    public static Texture beekeeperImage;
    public static Texture beeImage;
    public static Texture backgroundImage;
    public static Texture powerUpImage;
    public static Sound honeySound;
    public static Sound backgroundSound;
    public static BitmapFont font;
    public static ParticleEffect sparkle;
    public static ParticleEffect beeEffect;

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
        sparkle = new ParticleEffect();
        sparkle.load(Gdx.files.internal("sparkle.pe"), Gdx.files.internal(("")));
        beeEffect = new ParticleEffect();
        beeEffect.load(Gdx.files.internal("beeEffect.pe"), Gdx.files.internal(("")));

    }

    public static void dispose() {
        honeyImage.dispose();
        beekeeperImage.dispose();
        honeySound.dispose();
        powerUpImage.dispose();
        backgroundImage.dispose();
        backgroundSound.dispose();
        font.dispose();
        beeEffect.dispose();
        sparkle.dispose();
    }

}
