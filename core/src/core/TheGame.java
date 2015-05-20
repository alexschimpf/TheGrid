package core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TheGame extends Game {

	private SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new MainMenuScreen(this));
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
