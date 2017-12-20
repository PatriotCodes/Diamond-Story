package com.alextriukhan.match3.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.alextriukhan.match3.DiamondStoryGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Diamond Story";
		config.width = 320;
		config.height = 480;
		new LwjglApplication(new DiamondStoryGame(), config);
	}
}
