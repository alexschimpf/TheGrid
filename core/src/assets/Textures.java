package assets;

import java.util.HashMap;

import misc.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Textures {

	private static Textures instance;
	
	private HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();
	private BodyEditorLoader loader;

	private Textures() {
		createTextureRegion("player.png", "player");	
		createTextureRegion("player_blink.png", "player_blink");	
		createTextureRegion("background.png", "background");
		createTextureRegion("ball.png", "ball");
		createTextureRegion("gray_block.png", "gray_block");
		createTextureRegion("red_block.png", "red_block");
		createTextureRegion("green_block.png", "green_block");
		createTextureRegion("blue_block.png", "blue_block");
		createTextureRegion("question_mark_block.png", "question_mark_block");
		createTextureRegion("up_arrow_block.png", "up_arrow_block");
		createTextureRegion("portal.png", "portal");
		createTextureRegion("programmable_block.png", "programmable_block");
		createTextureRegion("programmable_block_1.png", "programmable_block_1");
		createTextureRegion("programmable_block_2.png", "programmable_block_2");
		createTextureRegion("programmable_block_3.png", "programmable_block_3");
		createTextureRegion("programmable_block_4.png", "programmable_block_4");
		createTextureRegion("hor_line.png", "hor_line");
		createTextureRegion("vert_line.png", "vert_line");
		
		createColorTextureRegion(1, 1, 1, 1, "white");
		createColorTextureRegion(0.8f, 0.8f, 0.8f, 1, "light_gray");
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
		texture.setFilter(TextureFilter.Linear, TextureFilter.MipMapNearestLinear);
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
