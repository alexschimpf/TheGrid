package script;

import misc.Globals;
import misc.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Game;
import core.Room;

public class ShowMessageScript extends Script {

	protected String message;
	protected BitmapFont font;
	protected boolean started = false;
	protected long startTime;
	
	public ShowMessageScript(Room room, String message) {
		super(room);
		
		this.message = message;
	}
	
	public static Script build(String id, Room room, Element elem) {
		Element custom = elem.getChildByName("custom");
		//String message = custom.get("message", null);
		
		ShowMessageScript script = new ShowMessageScript(room, null);
		script.setId(id);
		
		return script;
	}
	
	@Override
	public boolean update() {
		Game game = globals.getGame();
		if(started && TimeUtils.timeSinceMillis(startTime) < 10000) {
			game.setText("You have entered room " + room.getGridPosition().x + ", " + room.getGridPosition().y);
		} else {
			started = false;
			game.setText(null);
		}
		
		return super.update();
	}
	
	@Override
	public void run(Object... params) {
		if(started) {
			return;
		}
		
		startTime = TimeUtils.millis();
		started = true;
	}
}
