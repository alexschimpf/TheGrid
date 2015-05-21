package core;

import java.util.Iterator;

import listener.InputListener;
import misc.Globals;
import parallax.ParallaxBackground;
import parallax.TextureRegionParallaxLayer;
import parallax.ParallaxLayer.TileMode;
import parallax.ParallaxUtils.WH;
import particle.ParticleEffect;
import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import entity.special.Player;

public class GameScreen implements Screen {

	private Matrix4 debugMatrix;
	private Box2DDebugRenderer debugRenderer;
	private ParallaxBackground background;
	private TheGrid theGrid;
	private Globals globals;
	private String text;
	private BitmapFont font;
	private Music music;
	private Stage stage;
	private InputListener inputListener;
	private Button moveButton;
	private Button jumpButton;
	private Button shootButton;
	private Integer movePointer;
	private Array<ParticleEffect> particleEffects = new Array<ParticleEffect>();
	
	private TheGame game;
	private SpriteBatch batch;
	
	public GameScreen(TheGame game) {
		this.game = game;
		this.batch = game.getSpriteBatch();
		
		globals = Globals.getInstance();
		globals.setGame(this);
		//globals.getCamera().zoom -= 0.4f;
		globals.getCamera().zoom += 0.1f;
		debugRenderer = new Box2DDebugRenderer();
		
		theGrid = new TheGrid();
		globals.setTheGrid(theGrid);
		theGrid.build();

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		inputListener = new InputListener();
		stage.addListener(inputListener);
		Gdx.input.setInputProcessor(stage);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("music/song1.mp3"));	
		
		createBackground();
		
		if(Gdx.app.getType() == ApplicationType.Android) {
			createUI();
		}
	}
	
	@Override
	public void show() {
		music.play();
		music.setVolume(0.5f);
		music.setLooping(true);
		music.setPosition(0.5f);
		
		// I don't know why this helps, but it does.
		Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);	
	}

	@Override
	public void render(float delta) {
		//Gdx.app.log("FPS", "" + Gdx.graphics.getFramesPerSecond());
		//Gdx.app.log("Max sprites in batch", "" + batch.maxSpritesInBatch);
		
		globals.updateCamera();

		update();
		draw();	
	}

	@Override
	public void resize(int width, int height) {
		globals.getCamera().viewportHeight = (Globals.VIEWPORT_WIDTH / width) * height;
        globals.updateCamera();
        
        stage.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

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
		
		GameState state = globals.getGameState();			
		if(state == GameState.CutScene) {
			updateOpeningScene();
		}
		
		// TODO:: Get this outta here.
		if(Gdx.app.getType() == ApplicationType.Android) {
			if(state == GameState.Running) {
				checkPressedButtons();
			}
			
			if(state != GameState.Running) {
				hideUI();
			} else {
				showUI();
			}
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
		createJumpButton(width, height, skin, player);
		createShootButton(width, height, skin, player); 
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
	
	private long lastMoveTime = TimeUtils.millis() + 3000;
	private long openingSceneStartTime = TimeUtils.millis();
	private float moveDuration = MathUtils.random(100, 150);
	private int moveNum = 0;
	private boolean stopped = false;
	private void updateOpeningScene() {
		if(TimeUtils.timeSinceMillis(openingSceneStartTime) > 10000) {
			globals.getCamera().zoom += (5.0f / 50.0f) * Gdx.graphics.getDeltaTime();
			if(globals.getCamera().zoom >= 1.1f) {
				globals.getCamera().zoom = 1.1f;
				globals.setGameState(GameState.Running);
				
				if(Gdx.app.getType() == ApplicationType.Android) {
					showUI();
				}
			}
		} else if(TimeUtils.timeSinceMillis(lastMoveTime) > 1500f){
			Player player = theGrid.getPlayer();
			
			if(TimeUtils.timeSinceMillis(lastMoveTime) > 1500f + moveDuration) {
				lastMoveTime = TimeUtils.millis();
				stopped = true;
				player.stopMove();
				return;
			}
			
			if(stopped) {
				moveDuration = MathUtils.random(100, 150);
				moveNum++;
				stopped = false;
			}
			
			if(!stopped && moveNum % 2 == 0) {
				player.moveLeft();
			} else if(!stopped && moveNum % 2 == 1) {
				player.moveRight();
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: This shouldn't be in this class.
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void createMoveButton(float screenWidth, float screenHeight, Skin skin, final Player player) {
		moveButton = new Button(skin);
		moveButton.setColor(1, 1, 1, 0.3f);
		moveButton.setSize(screenWidth / 2.75f, screenHeight / 5f);
		moveButton.setPosition(0, screenHeight / 32f);
		
		moveButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(globals.getGameState() == GameState.Running) {
					movePointer = pointer;
				}
				 
				return true;
			}
		});
		
		stage.addActor(moveButton);
	}
	
	private void createJumpButton(float screenWidth, float screenHeight, Skin skin, final Player player) {
		jumpButton = new Button(skin);
		jumpButton.setColor(1, 1, 1, 0.3f);
		jumpButton.setSize(screenWidth / 7f, screenHeight / 5f);
		float width = jumpButton.getWidth();
		
		jumpButton.setPosition(screenWidth - (width * 2.2f), screenHeight / 32f);

		jumpButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(globals.getGameState() == GameState.Running) { 
					player.jump();
				}
				
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(globals.getGameState() == GameState.Running) { 
					player.stopJump();
				}
			}
		});

		stage.addActor(jumpButton);
	}
	
	private void createShootButton(float screenWidth, float screenHeight, Skin skin, final Player player) {
		shootButton = new Button(skin);
		shootButton.setColor(1, 1, 1, 0.3f);
		shootButton.setSize(screenWidth / 7f, screenHeight / 5f);
		float width = shootButton.getWidth();
		
		shootButton.setPosition(screenWidth - (width * 1.1f), screenHeight / 32f);

		shootButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(globals.getGameState() == GameState.Running) { 
					player.shoot();
				}
				
				return true;
			}
		});

		stage.addActor(shootButton);
	}
	
	private void checkPressedButtons() {
		if(movePointer != null) {
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

		if(shootButton.isPressed()) {
			Player player = theGrid.getPlayer();
			player.shoot(); 				
		}
	}
	
	private void hideUI() {
		moveButton.setVisible(false);
		jumpButton.setVisible(false);
		shootButton.setVisible(false);
	}
	
	private void showUI() {
		moveButton.setVisible(true);
		jumpButton.setVisible(true);
		shootButton.setVisible(true);
	}
}
