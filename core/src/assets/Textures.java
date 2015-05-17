package assets;

import java.util.HashMap;

import misc.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Textures {

	private static final Color[] SCHEME_COLORS = new Color[4];
	
	private static Textures instance;
	
	private HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();
	private BodyEditorLoader loader;

	private Textures() {
		initSchemeColors();
		
		createTextureRegion("player.png", "player");	
		createTextureRegion("player_move.png", "player_move");
		createTextureRegion("player_blink.png", "player_blink");	
		createTextureRegion("player_jump.png", "player_jump");
		createTextureRegion("background.png", "background");
		createTextureRegion("shot.png", "shot");
		createTextureRegion("ball.png", "ball"); 
		createTextureRegion("disappearing_block.png", "disappearing_block");
		createTextureRegion("filling_block.png", "filling_block");
		createTextureRegion("question_mark_block.png", "question_mark_block");
		createTextureRegion("up_arrow_block.png", "up_arrow_block");
		createTextureRegion("portal.png", "portal");
		createTextureRegion("programmable_block.png", "programmable_block");
		createTextureRegion("programmable_block_1.png", "programmable_block_1");
		createTextureRegion("programmable_block_2.png", "programmable_block_2");
		createTextureRegion("programmable_block_3.png", "programmable_block_3");
		createTextureRegion("programmable_block_4.png", "programmable_block_4");
		
		createTextureRegion("block.png", "block");
		createTextureRegion("cracked_block_1.png", "cracked_block_1");
		createTextureRegion("cracked_block_2.png", "cracked_block_2");
		createTextureRegion("cracked_block_3.png", "cracked_block_3");
		createTextureRegion("cracked_block_4.png", "cracked_block_4");
		
		createTextureRegion("grid_corner_1.png", "gc1");
		createTextureRegion("grid_bottom_horizontal_1.png", "gbh1");
		createTextureRegion("grid_top_horizontal_1.png", "gth1");
		createTextureRegion("grid_edge_horizontal_1.png", "geh1");
		createTextureRegion("grid_edge_vertical_1.png", "gev1");
		createTextureRegion("grid_left_vertical_1.png", "glv1");
		createTextureRegion("grid_right_vertical_1.png", "grv1");
		createTextureRegion("grid_top_left.png", "gtl");
		createTextureRegion("grid_top_right.png", "gtr");
		createTextureRegion("grid_bottom_left.png", "gbl");
		createTextureRegion("grid_bottom_right.png", "gbr");
		
		createColorTextureRegion(1, 1, 1, 1, "white");
		createColorTextureRegion(0, 0, 0, 1, "black");
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
	
	public Sprite getRandomColorSprite(String key) {
		return getSprite(key, getRandomSchemeColor());
	}
	
	public Sprite getSprite(String key) {
		return getSprite(key, Color.WHITE);
	}

	public Sprite getSprite(String key, Color color) {
		TextureRegion texture = getTextureRegion(key);

		Sprite sprite = texture != null ? new Sprite(texture) : new Sprite();
		sprite.setFlip(false, true);		
		sprite.setColor(color);
		
		return sprite;
	}
	
	public BodyEditorLoader getLoader() {
		return loader;
	}
	
	private void initSchemeColors() {
		SCHEME_COLORS[0] = new Color(230 / 255.0f, 230 / 255.0f, 230 / 255.0f, 1);
		SCHEME_COLORS[1] = new Color(197 / 255.0f, 224 / 255.0f, 246 / 255.0f, 1);
		SCHEME_COLORS[2] = new Color(255 / 255.0f, 217 / 255.0f, 200 / 255.0f, 1);
		SCHEME_COLORS[3] = new Color(255 / 255.0f, 242 / 255.0f, 200 / 255.0f, 1);
	}
	
	public Color getRandomSchemeColor() {
		int pos = MathUtils.random(0, SCHEME_COLORS.length - 1);
		return SCHEME_COLORS[pos];
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
