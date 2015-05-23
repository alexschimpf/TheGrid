package core;

import misc.GlobalIdMapper;
import screen.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class TheGame extends Game {

	public static boolean DEBUG = false;
	
	private SpriteBatch batch;

	@Override
	public void create() {
		if(DEBUG) {
			GlobalIdMapper.createGlobalIdMappings();
		}
		
		batch = new SpriteBatch();
		setScreen(new GameScreen(this));
	}

	@Override
	public void render() {		
		super.render();
	}
	
	@Override
	public void resize(int width, int height) {
        
	}
	
	public SpriteBatch getSpriteBatch() {
		return batch;
	}
}
