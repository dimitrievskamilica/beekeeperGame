package com.rri.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rri.HoneyGame;

public class DesktopLauncherUnits {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "World units example";
        config.width = 1200;
        config.height = 600;
        config.forceExit = false;    // https://gamedev.stackexchange.com/questions/109047/how-to-close-an-app-correctly-on-desktop
        new LwjglApplication(new HoneyGame(), config);
    }
}
