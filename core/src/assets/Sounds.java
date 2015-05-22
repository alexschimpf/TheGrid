package assets;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public final class Sounds {

	private static Sounds instance;
	
	private final HashMap<String, Sound> soundMap = new HashMap<String, Sound>();

	private Sounds() {
		createSound("jump_low.wav", "jump");
		createSound("land.wav", "land");
		createSound("shoot.wav", "shoot");
		createSound("explode.wav", "explode");
		createSound("transport.wav", "transport");
	}

	public static Sounds getInstance() {
		if (instance == null) {
			instance = new Sounds();
		}

		return instance;
	}
	
	public Sound getSound(String key) {
		return soundMap.get(key);
	}
	
	public void playSound(String key) {
		Sound sound = soundMap.get(key);
		sound.setVolume(sound.play(), 0.4f);
	}
	
	private void createSound(String filename, String key) {
		Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + filename));
		soundMap.put(key, sound);
	}
}
