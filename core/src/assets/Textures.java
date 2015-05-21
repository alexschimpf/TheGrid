package assets;

import java.util.HashMap;

import misc.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Textures {

	private static final Color[] SCHEME_COLORS = new Color[4];
	
	private static Textures instance;
	
	private HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();
	private HashMap<String, Array<AtlasRegion>> textureListMap = new HashMap<String, Array<AtlasRegion>>();
	private BodyEditorLoader loader;
	private TextureAtlas atlas;

	private Textures() {
		initSchemeColors();
		
		atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));

		// Single images
		createTextureRegion("player", "player");	
		createTextureRegion("player_move", "player_move");		
		createTextureRegion("background", "background");
		createTextureRegion("shot", "shot");
		createTextureRegion("ball", "ball"); 		
		createTextureRegion("block", "block");
		createTextureRegion("block_chain_start", "block_chain_start");		
		createTextureRegion("up_arrow_block", "up_arrow_block");	
		createTextureRegion("programmable_block", "programmable_block");
		createTextureRegion("programmable_block1", "programmable_block_1");
		createTextureRegion("programmable_block2", "programmable_block_2");
		createTextureRegion("programmable_block3", "programmable_block_3");
		createTextureRegion("programmable_block4", "programmable_block_4");		
		createTextureRegion("cracked_block1", "cracked_block_1");
		createTextureRegion("cracked_block2", "cracked_block_2");
		createTextureRegion("cracked_block3", "cracked_block_3");
		createTextureRegion("cracked_block4", "cracked_block_4");		
		createTextureRegion("grid_corner1", "gc1");
		createTextureRegion("grid_bottom_horizontal1", "gbh1");
		createTextureRegion("grid_top_horizontal1", "gth1");
		createTextureRegion("grid_edge_horizontal1", "geh1");
		createTextureRegion("grid_edge_vertical1", "gev1");
		createTextureRegion("grid_left_vertical1", "glv1");
		createTextureRegion("grid_right_vertical1", "grv1");
		createTextureRegion("grid_top_left", "gtl");
		createTextureRegion("grid_top_right", "gtr");
		createTextureRegion("grid_bottom_left", "gbl");
		createTextureRegion("grid_bottom_right", "gbr");
		
		// Animations
		createTextureRegions("player_blink", "player_blink");	
		createTextureRegions("player_jump", "player_jump");
		createTextureRegions("disappearing_block", "disappearing_block");
		createTextureRegions("filling_block", "filling_block");
		createTextureRegions("question_mark_block", "question_mark_block");
		createTextureRegions("portal", "portal");
		
//		createColorTextureRegion(1, 1, 1, 1, "white");
//		createColorTextureRegion(0, 0, 0, 1, "black");
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
	
	public Array<AtlasRegion> getTextureRegions(String key) {
		return textureListMap.get(key);
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
	
	public Sprite getRandomColorSprite(String key) {
		return getSprite(key, getRandomSchemeColor());
	}
	
	public Color getRandomSchemeColor() {
		int pos = MathUtils.random(0, SCHEME_COLORS.length - 1);
		return SCHEME_COLORS[pos];
	}
	
	public BodyEditorLoader getLoader() {
		return loader;
	}
	
	private void createTextureRegions(String filename, String key) {
		Array<AtlasRegion> regions = atlas.findRegions(filename);
		textureListMap.put(key, regions);
	}
	
	private void createTextureRegion(String filename, String key) {
		AtlasRegion region = atlas.findRegion(filename);
		textureMap.put(key,  region);
	}
	
	private void initSchemeColors() {
		SCHEME_COLORS[0] = new Color(230 / 255.0f, 230 / 255.0f, 230 / 255.0f, 1);
		SCHEME_COLORS[1] = new Color(197 / 255.0f, 224 / 255.0f, 246 / 255.0f, 1);
		SCHEME_COLORS[2] = new Color(255 / 255.0f, 217 / 255.0f, 200 / 255.0f, 1);
		SCHEME_COLORS[3] = new Color(255 / 255.0f, 242 / 255.0f, 200 / 255.0f, 1);
	}
	
//	private TextureRegion createColorTextureRegion(float r, float g, float b, float a, String key) {
//		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//		pix.setColor(r, g, b, a);
//		pix.fill();
//		TextureRegion texture = new TextureRegion(new Texture(pix));
//		textureMap.put(key, texture);
//		return texture;
//	}
}
