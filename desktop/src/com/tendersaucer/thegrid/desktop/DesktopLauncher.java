package com.tendersaucer.thegrid.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import core.TheGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = true;

        TexturePacker.process("/Users/schimpf1/Desktop/The Grid/textures", "../android/assets", "game");
		
		new LwjglApplication(new TheGame(), config);
	}
}
