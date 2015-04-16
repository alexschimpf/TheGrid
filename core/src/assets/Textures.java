package assets;

import java.util.HashMap;

import misc.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

	private static Textures instance;
	
	private HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();
	private BodyEditorLoader loader;

	private Textures() {
		createTextureRegion("player.png", "player");
		createTextureRegion("block.png", "block");
		createTextureRegion("circle.png", "circle");
		createTextureRegion("breakable_block.png", "breakable_block");
		createTextureRegion("drop_player_trigger.png", "player_drop");
		createTextureRegion("background.jpg", "background");
		createColorTextureRegion(1, 1, 1, 1, "white");
		createColorTextureRegion(1, 0, 0, 1, "red");
		createColorTextureRegion(0, 1, 0, 1, "green");
		createColorTextureRegion(0, 0, 1, 1, "blue");
	}

	public static Textures getInstance() {
		if (instance == null) {
			instance = new Textures();
		}

		return instance;
	}
	
	public void initLoader() {
		//loader = new BodyEditorLoader(Gdx.files.internal("loader_data.json"));
	}
	
	public TextureRegion getTextureRegion(String key) {
		return textureMap.get(key);
	}
	
	public BodyEditorLoader getLoader() {
		return loader;
	}
	
	private void createTextureRegion(String filename, String key) {
		Texture texture = new Texture(Gdx.files.internal("textures/" + filename));
		TextureRegion textureRegion = new TextureRegion(texture);
		textureMap.put(key, textureRegion);
		textureRegion.flip(false, true);
	}
	
	private TextureRegion createColorTextureRegion(float r, float g, float b, float a, String key) {
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(r, g, b, a);
		pix.fill();
		TextureRegion texture = new TextureRegion(new Texture(pix));
		textureMap.put(key, texture);
		return texture;
	}
}
