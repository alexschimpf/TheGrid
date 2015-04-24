package listener;

import misc.Globals;
import misc.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;

import core.TheGrid;
import entity.special.Player;

public class InputListener implements InputProcessor {
	private Player player;
	private TheGrid theGrid;
	private Camera camera = Globals.getInstance().getCamera();
	
	public InputListener() {
		theGrid = Globals.getInstance().getTheGrid();
		player = theGrid.getPlayer();
	}

	@Override
	public boolean keyDown(int keyCode) {
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
	public boolean keyUp(int keyCode) {
		switch(keyCode) {
			case Keys.SPACE:
				player.stopJump();
				break;
		}
		
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean right = screenX > Gdx.graphics.getWidth() / 2;
		boolean top = screenY < Gdx.graphics.getHeight() / 2;
		if(right && top) {
			player.shoot();
		} else if(!right && top) {
			player.jump();
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean left = screenX < Gdx.graphics.getWidth() / 2;
		boolean top = screenY < Gdx.graphics.getHeight() / 2;
		if(left && top) {
			player.stopJump();
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	public void update() {
		boolean touched = Gdx.input.isTouched();
		float x = Gdx.input.getX();
		float y = Gdx.input.getY();
		boolean lowerScreenHalf = y > Gdx.graphics.getHeight() / 2;
		boolean right = lowerScreenHalf && touched && x > Gdx.graphics.getWidth() / 2;
		boolean left = lowerScreenHalf && touched && x <= Gdx.graphics.getWidth() / 2;
		
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT) || right) {
			player.moveRight();
		}if(Gdx.input.isKeyPressed(Keys.LEFT) || left) {
			player.moveLeft();
		} else {
			player.stopMove();
		}
	}
}
