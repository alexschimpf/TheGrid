package listener;

import misc.Globals;
import misc.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import core.TheGrid;
import entity.special.Player;

public class InputListenerOld implements InputProcessor {
	private Player player;
	private TheGrid theGrid;
	private Camera camera = Globals.getInstance().getCamera();
	
	public InputListenerOld() {
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
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
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
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.moveRight();
		} else if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.moveLeft();
		} else {
			player.stopMove();
		}
	}
}
