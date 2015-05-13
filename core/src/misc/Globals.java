package misc;

import com.badlogic.gdx.graphics.OrthographicCamera;

import core.GameScreen;
import core.GameState;
import core.TheGame;
import core.TheGrid;

public class Globals {

	public static final float VIEWPORT_WIDTH = 60.0f;
	public static final float VIEWPORT_HEIGHT = 100.0f;
	
	private GameState state;
	private GameScreen game;
	private TheGrid theGrid;
	private OrthographicCamera camera;

	private static Globals instance;

	private Globals() {
		camera = new OrthographicCamera();
		camera.setToOrtho(true, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		//state = GameState.CutScene;
		state = GameState.Running;
	}

	public static Globals getInstance() {
		if (instance == null) {
			instance = new Globals();
		}

		return instance;
	}
	
	public void updateCamera() {
		
		if(theGrid.getPlayer() != null) {
			camera.position.x = theGrid.getPlayer().getCenterX();
			camera.position.y = theGrid.getPlayer().getCenterY();
		}	
		
		camera.update();
	}
	
	public float getCameraTop() {
		return camera.position.y - (Globals.VIEWPORT_HEIGHT / 2);
	}
	
	public float getCameraBottom() {
		return camera.position.y + (Globals.VIEWPORT_HEIGHT / 2);
	}
	
	public float getCameraLeft() {
		return camera.position.x + (Globals.VIEWPORT_WIDTH / 2);
	}
	
	public float getCameraRight() {
		return camera.position.x + (Globals.VIEWPORT_WIDTH / 2);
	}
	
	public void setTheGrid(TheGrid theGrid) {
		this.theGrid = theGrid;
	}
	
	public void setGame(GameScreen game) {
		this.game = game;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public GameScreen getGame() {
		return game;
	}
	
	public TheGrid getTheGrid() {
		return theGrid;
	}
	
	public GameState getGameState() {
		return state;
	}
	
	public void setGameState(GameState state) {
		this.state = state;
	}
}
