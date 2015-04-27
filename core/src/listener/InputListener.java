package listener;

import misc.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import core.TheGrid;
import entity.special.Player;

public class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {

	private Player player;
	private TheGrid theGrid;
	private Globals globals = Globals.getInstance();
	private Camera camera = Globals.getInstance().getCamera();
	
	public InputListener() {
		theGrid = globals.getTheGrid();
		player = theGrid.getPlayer();
	}
	
	@Override
	public boolean keyDown(InputEvent event, int keyCode) {
		Player player = theGrid.getPlayer();
		switch(keyCode) {
			case Keys.ESCAPE:
				Gdx.app.exit();
				return true;
			case Keys.SPACE:
				player.jump();
				break;
			case Keys.A:
				player.shoot();
				break;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(InputEvent event, int keyCode) {
		Player player = theGrid.getPlayer();
		switch(keyCode) {
			case Keys.SPACE:
				player.stopJump();
				break;
		}
		
		return true;
	}
	
	public void update() {
		Player player = theGrid.getPlayer();
		
		boolean touched = Gdx.input.isTouched();
		float x = Gdx.input.getX();
		float y = Gdx.input.getY();
		boolean lowerScreenHalf = y > Gdx.graphics.getHeight() / 2;
		boolean right = lowerScreenHalf && touched && x > Gdx.graphics.getWidth() / 2;
		boolean left = lowerScreenHalf && touched && x <= Gdx.graphics.getWidth() / 2;
				
		if(Gdx.input.isKeyPressed(Keys.RIGHT) || right) {
			player.moveRight();
		} else if(Gdx.input.isKeyPressed(Keys.LEFT) || left) {
			player.moveLeft();
		} else {
			player.stopMove();
		}
	}
}
