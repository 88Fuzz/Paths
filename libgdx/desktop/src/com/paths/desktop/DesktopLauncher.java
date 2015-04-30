package com.paths.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.paths.GameStates.GameState;

public class DesktopLauncher {
    private static boolean rebuildAtlas = true;
    private static boolean drawDebugOutline = false;

	public static void main (String[] arg) {
        if (rebuildAtlas) {
            Settings settings = new Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.debug = drawDebugOutline;
            TexturePacker2.process(settings, "assets-raw/images", "../android/assets/images", "tiles.pack");
        }
	    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Paths";
        config.useGL30 = false;
        config.width = 1600;
        config.height = 900;
//        config.width = 90;
//        config.height = 90;

		new LwjglApplication(new GameState(), config);
	}
}
