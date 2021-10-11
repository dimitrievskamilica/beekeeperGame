package com.rri.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rri.HoneyGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Collecting honey";
        config.width = 1024;
        config.height = 480;
        config.forceExit = false; // do I need it https://gamedev.stackexchange.com/questions/109047/how-to-close-an-app-correctly-on-desktop
        new LwjglApplication(new HoneyGame(), config);
    }
}
