package assets;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public final class Textures {

	private static final Color[] SCHEME_COLORS = new Color[4];
	
	private static Textures instance;
	
//	private final BodyEditorLoader loader = null;
	private final HashMap<String, TextureRegion> textureMap = new HashMap<String, TextureRegion>();
	private final HashMap<String, Array<AtlasRegion>> textureListMap = new HashMap<String, Array<AtlasRegion>>();
	private final HashMap<String, String> keyToAtlasFileMap = new HashMap<String, String>();
	private final TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));

	private Textures() {
		initSchemeColors();
		
		// Single images
		createTextureRegion("player", "player");	
		createTextureRegion("player_move", "player_move");		
		createTextureRegion("background", "background");
		createTextureRegion("shot", "shot");
		createTextureRegion("ball", "ball"); 		
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
		createTextureRegion("grid_top_left", "gtl");
		createTextureRegion("grid_top_right", "gtr");
		createTextureRegion("grid_bottom_left", "gbl");
		createTextureRegion("grid_bottom_right", "gbr");
		createTextureRegion("grid_top_rounded", "gtround");
		createTextureRegion("grid_bottom_rounded", "gbround");
		createTextureRegion("grid_left_rounded", "glround");
		createTextureRegion("grid_right_rounded", "grround");

		// Indexed
		createTextureRegions("block", "block");
		createTextureRegions("player_blink", "player_blink");	
		createTextureRegions("player_jump", "player_jump");
		createTextureRegions("disappearing_block", "disappearing_block");
		createTextureRegions("filling_block", "filling_block");
		createTextureRegions("question_mark_block", "question_mark_block");
		createTextureRegions("portal", "portal");
		createTextureRegions("cloud", "cloud");
		createTextureRegions("grid_corner", "gc");
		createTextureRegions("grid_bottom_horizontal", "gbh");
		createTextureRegions("grid_top_horizontal", "gth");
		createTextureRegions("grid_edge_horizontal", "geh");
		createTextureRegions("grid_edge_vertical", "gev");
		createTextureRegions("grid_left_vertical", "glv");
		createTextureRegions("grid_right_vertical", "grv");	
		
//		createColorTextureRegion(1, 1, 1, 1, "white");
//		createColorTextureRegion(0, 0, 0, 1, "black");
	}

	public static Textures getInstance() {
		if (instance == null) {
			instance = new Textures();
		}

		return instance;
	}
	
	public TextureRegion getTextureRegion(String key) {
		TextureRegion texture = textureMap.get(key);
		
		if(texture == null) {
			Array<AtlasRegion> textureRegions = getTextureRegions(key);
			if(textureRegions != null) {
				int index = getRandomVariation(key);
				texture = getTextureRegions(key).get(index);
			}
		}
		
		return texture;
	}
	
	public TextureRegion getTextureRegion(String key, int index) {
		return getTextureRegions(key).get(index);
	}
	
	public Array<AtlasRegion> getTextureRegions(String key) {
		return textureListMap.get(key);
	}
	
	public Sprite getSprite(String key) {
		return getSprite(key, Color.WHITE);
	}

	public Sprite getSprite(String key, Color color) {
		TextureRegion texture = getTextureRegion(key);
		
		if(texture == null) {
			// If there are multiple variations available, then always choose a random one.
			Array<AtlasRegion> textureRegions = getTextureRegions(key);
			if(textureRegions != null) {
				int index = getRandomVariation(key);
				texture = getTextureRegions(key).get(index);
			}
		}

		boolean flipHor = false;
		boolean flipVert = true;
		if(key.equals("block")) {
			flipHor = MathUtils.random() < 0.5f;
			flipVert = MathUtils.random() < 0.5f;
		}
		
		Sprite sprite = texture != null ? new Sprite(texture) : new Sprite();
		//sprite.setFlip(flipHor, flipVert);		
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
	
//	public BodyEditorLoader getLoader() {
//		return loader;
//	}
	
	private int getRandomVariation(String key) {
		int numVariations = atlas.findRegions(keyToAtlasFileMap.get(key)).size;
		return MathUtils.random(0, numVariations - 1);
	}
	
	private void createTextureRegions(String filename, String key) {
		Array<AtlasRegion> regions = atlas.findRegions(filename);
		for(AtlasRegion region : regions) {
			region.flip(false, true);
		}
		
		textureListMap.put(key, regions);
		keyToAtlasFileMap.put(key, filename);
	}
	
	private void createTextureRegion(String filename, String key) {
		AtlasRegion region = atlas.findRegion(filename);
		region.flip(false, true);
		textureMap.put(key,  region);
		keyToAtlasFileMap.put(key, filename);
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
