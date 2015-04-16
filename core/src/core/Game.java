package core;

import parallax.ParallaxBackground;
import parallax.ParallaxLayer.TileMode;
import parallax.ParallaxUtils.WH;
import parallax.TextureRegionParallaxLayer;
import listener.InputListener;
import misc.Globals;
import assets.Textures;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public class Game extends ApplicationAdapter {
	
	private Matrix4 debugMatrix;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private ParallaxBackground background;
	private TheGrid theGrid;
	private Globals globals;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();
		
		globals = Globals.getInstance();
		globals.setGame(this);
		globals.getCamera().zoom += 0.1f;
		
		theGrid = new TheGrid();
		globals.setTheGrid(theGrid);
		theGrid.build();
		Gdx.input.setInputProcessor(new InputListener());
		
		createBackground();
	}

	@Override
	public void render () {
		super.render();
		
		update();
		draw();
	}
	
	@Override
	public void resize(int width, int height) {
        globals.getCamera().viewportHeight = (Globals.VIEWPORT_WIDTH / width) * height;
        globals.updateCamera();
	}
	
	private void update() {
		debugMatrix = new Matrix4(globals.getCamera().combined);
		
		globals.updateCamera();
		theGrid.update();
		
		((InputListener)Gdx.input.getInputProcessor()).update();
	}
	
	private void draw() {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(globals.getCamera().combined);
		batch.begin(); {
			background.draw(globals.getCamera(), batch);
			theGrid.draw(batch);
		} batch.end();
		
		//debugRenderer.render(theGrid.getWorld(), debugMatrix);
	}
	
	private void createBackground() {
		background = new ParallaxBackground();
		Textures textures = Textures.getInstance();
		TextureRegion texture = textures.getTextureRegion("background");
		TextureRegionParallaxLayer layer = 
				new TextureRegionParallaxLayer(texture, Globals.VIEWPORT_WIDTH * 2, new Vector2(0.1f, 0), WH.width);	
		layer.setPadBottom(-0.6f * Globals.VIEWPORT_WIDTH * 2);
		layer.setPadLeft(-0.6f * Globals.VIEWPORT_WIDTH * 2);
		layer.setTileModeX(TileMode.repeat);
		layer.setTileModeY(TileMode.repeat);
		background.addLayers(layer);
	}
}
