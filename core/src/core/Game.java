package core;

import java.util.Iterator;

import parallax.ParallaxBackground;
import parallax.ParallaxLayer.TileMode;
import parallax.ParallaxUtils.WH;
import parallax.TextureRegionParallaxLayer;
import particle.ParticleEffect;
import listener.InputListener;
import misc.Globals;
import assets.Textures;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.sun.deploy.uitoolkit.impl.fx.Utils;

import entity.special.Player;

public class Game extends ApplicationAdapter {
	
	private Matrix4 debugMatrix;
	private Box2DDebugRenderer debugRenderer;
	private SpriteBatch batch;
	private ParallaxBackground background;
	private TheGrid theGrid;
	private Globals globals;
	private String text;
	private BitmapFont font;
	private Music music;
	private Stage stage;
	private InputListener inputListener;
	private Button moveButton;
	private Button actionButton;
	private Integer movePointer;
	private Array<ParticleEffect> particleEffects = new Array<ParticleEffect>();
	
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
		
		//Gdx.input.setInputProcessor(new InputListener());
		stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		inputListener = new InputListener();
		stage.addListener(inputListener);
		Gdx.input.setInputProcessor(stage);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("music/song1.mp3"));	
		music.play();
		music.setLooping(true);
		music.setPosition(5);
		
		createBackground();
		
		if(Gdx.app.getType() == ApplicationType.Android) {
			createUI();
		}
	}

	@Override
	public void render () {		
		globals.updateCamera();
		
		super.render();
		
		update();
		draw();	
	}
	
	@Override
	public void resize(int width, int height) {
        globals.getCamera().viewportHeight = (Globals.VIEWPORT_WIDTH / width) * height;
        globals.updateCamera();
        
        stage.getViewport().update(width, height, true);
	}
	
	public void setText(String text) {
		this.text = text;
		
		if(font == null) {
			FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Light.ttf"));
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.minFilter = TextureFilter.Nearest;
			parameter.magFilter = TextureFilter.MipMapLinearNearest;
			parameter.size = 16;
			parameter.flip = true;
			font = generator.generateFont(parameter);
			font.setColor(Color.RED);
			font.setUseIntegerPositions(false);
			font.setScale(0.1f, 0.1f);
			generator.dispose();
		}
	}
	
	public SpriteBatch getSpriteBatch() {
		return batch;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void addParticleEffect(ParticleEffect effect) {
		particleEffects.add(effect);
	}
	
	private void update() {
		debugMatrix = new Matrix4(globals.getCamera().combined);
		
		theGrid.update();
		
		updateParticleEffects();
		
		inputListener.update();
		
		// TODO:: Get this outta here.
		if(Gdx.app.getType() == ApplicationType.Android) {
			checkPressedButtons();
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	}
	
	private void draw() {
		Gdx.gl.glClearColor((201.0f / 255), (238.0f / 255), (255.0f / 255), 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(globals.getCamera().combined);
		batch.enableBlending();
		batch.begin(); {
			background.draw(globals.getCamera(), batch);
			drawParticleEffects(batch);
			theGrid.draw(batch);		
		} batch.end();
		
		//debugRenderer.render(theGrid.getWorld(), debugMatrix);
		
		stage.draw();
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
	
	private void createUI() {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		Player player = theGrid.getPlayer();
		createMoveButton(width, height, skin, player);
		createActionButton(width, height, skin, player);
		
	}
	
	private void updateParticleEffects() {
		Iterator<ParticleEffect> particleEffectsItr = particleEffects.iterator();
		while(particleEffectsItr.hasNext()) {
			ParticleEffect effect = particleEffectsItr.next();

			if(effect.update()) {
				effect.done();
				particleEffectsItr.remove();
			}
		}
	}
	
	private void drawParticleEffects(SpriteBatch batch) {
		for(ParticleEffect effect : particleEffects) {
			effect.draw(batch);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This shouldn't be in this class.
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void createMoveButton(float screenWidth, float screenHeight, Skin skin, final Player player) {
		moveButton = new Button(skin);
		moveButton.setColor(1, 1, 1, 0.3f);
		moveButton.setSize(screenWidth / 2.75f, screenHeight / 5f);
		moveButton.setPosition(0, screenWidth / 30f);
		
		moveButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				movePointer = pointer;
				 
				return true;
			}
		});
		
		stage.addActor(moveButton);
	}
	
	private void createActionButton(float screenWidth, float screenHeight, Skin skin, final Player player) {
		actionButton = new Button(skin);
		actionButton.setColor(1, 1, 1, 0.3f);
		actionButton.setSize(screenWidth / 2.75f, screenHeight / 5f);
		float width = actionButton.getWidth();
		
		actionButton.setPosition(screenWidth - width, screenHeight / 30f);

		actionButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				float centerX = actionButton.getWidth() / 2;
				if(x < centerX) {
					player.jump();
				} else {
					player.shoot();
				}
				 
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				player.stopJump();
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				float centerX = actionButton.getWidth() / 2;
				if(x < centerX) {
					player.jump();
				} else {
					player.shoot();
				}
			}
		});
		stage.addActor(actionButton);
	}
	
	private void checkPressedButtons() {
		if(movePointer == null) {
			return;
		}
		
		float moveCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
		float x = Gdx.input.getX(movePointer);		
		if(moveButton.isPressed()) {
			Player player = theGrid.getPlayer();
			if(x < moveCenterX) {
				player.moveLeft();
			} else {
				player.moveRight();
			}
		}
	}
}
