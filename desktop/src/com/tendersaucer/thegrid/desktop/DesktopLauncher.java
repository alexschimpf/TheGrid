package com.tendersaucer.thegrid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import core.TheGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = true;
		config.vSyncEnabled = true;
		config.resizable = false;
		config.title = "The Grid";

		if(TheGame.DEBUG) {
			config.fullscreen = false;
			Settings settings = new Settings();
			settings.duplicatePadding = true;
			TexturePacker.process(settings, "/Users/schimpf1/Desktop/The Grid/textures", "/Users/schimpf1/Desktop/libgdx/TheGrid/android/assets", "game");		
		}
		
		new LwjglApplication(new TheGame(), config);
	}
}
