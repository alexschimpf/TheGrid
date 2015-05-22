package screen;

import misc.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import core.TheGame;

public final class MainMenuScreen implements Screen {
	
	private TheGame game;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Stage stage;
	private Table table;
	TextButton buttonPlay;
	TextButton buttonQuit;
	
	public MainMenuScreen(TheGame game) {
		this.game = game;
		this.batch = game.getSpriteBatch();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(true, Globals.VIEWPORT_WIDTH, Globals.VIEWPORT_HEIGHT);
		
		stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		stage.addListener(new InputListener() {
			// TODO: 
		});
		Gdx.input.setInputProcessor(stage);
		
		createUI();
	}
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor((201.0f / 255), (238.0f / 255), (255.0f / 255), 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        stage.act();
        
        batch.begin();
        	stage.draw();
        batch.end();

        if(Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportHeight = (Globals.VIEWPORT_WIDTH / width) * height;
        stage.getViewport().update(width, height, true);
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
	
	protected void createUI() {
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		table = new Table(skin);
		
		buttonPlay = new TextButton("Play", skin);
		buttonQuit = new TextButton("Quit", skin);
		
		buttonPlay.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
                return true;
			}
		});
		
		buttonQuit.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
			}
		});
		
		float buttonWidth = Gdx.graphics.getWidth() / 2;
		float buttonHeight = Gdx.graphics.getHeight() / 5;
		buttonPlay.getStyle().font.setScale(5);
		buttonQuit.getStyle().font.setScale(5);
		table.add("Main Menu").size(buttonWidth / 2).row();
	    table.add(buttonPlay).size(buttonWidth, buttonHeight).padBottom(20).row();
	    table.add(buttonQuit).size(buttonWidth, buttonHeight).padBottom(20).row();

	    table.setFillParent(true);
	    stage.addActor(table);
	}
}
