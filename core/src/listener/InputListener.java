package listener;

import misc.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import core.GameState;
import core.Room;
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
		if(keyCode == Keys.ESCAPE) {
			Gdx.app.exit();
		}
		
		if(globals.getGameState() != GameState.Running) {
			return false;
		}
		
		Player player = theGrid.getPlayer();
		switch(keyCode) {
			case Keys.SPACE:
				player.jump();
				break;
			case Keys.A:
				player.shoot();
				break;
			case Keys.Q:
				showRoomSelectDialog();
				break;
		}
		return true;
	}
	
	@Override
	public boolean keyUp(InputEvent event, int keyCode) {
		if(globals.getGameState() != GameState.Running) {
			return false;
		}
		
		Player player = theGrid.getPlayer();
		switch(keyCode) {
			case Keys.SPACE:
				player.stopJump();
				break;
		}
		
		return true;
	}
	
	public void update() {
		if(globals.getGameState() != GameState.Running) {
			return;
		}
		
		Player player = theGrid.getPlayer();

		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			player.moveRight();
		} else if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			player.moveLeft();
		} else {
			player.stopMove();
		}
		
		if(Gdx.input.isKeyPressed(Keys.Z)) {
			globals.getCamera().zoom += 0.05f;
		} else if(Gdx.input.isKeyPressed(Keys.X)) {
			globals.getCamera().zoom -= 0.05f;
		}
	}
	
	private void showRoomSelectDialog() {
		Gdx.input.getTextInput(new TextInputListener() {
	        @Override
            public void input(String text) {
        	    String[] pieces = text.split(",");
        		int gridRow = Integer.parseInt(pieces[0]);
        		int gridCol = Integer.parseInt(pieces[1]);
        		int row = Integer.parseInt(pieces[2]);
        		int col = Integer.parseInt(pieces[3]);
        		Vector2 worldPos = Room.getWorldPosition(gridRow, gridCol, row, col);
        		player.setPosition(worldPos.x, worldPos.y);
        		Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);	
					}        			
        		});
        		
           }
        
           @Override
           public void canceled() {
           }
        }, "Format: gridRow,gridCol,row,col", "0,0,10,4", "");
	}
}
