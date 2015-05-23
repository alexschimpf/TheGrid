package script;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class ShowMessageScript extends Script {

	public static final String TYPE = "show_message";
	
	protected String message;
	protected BitmapFont font;
	protected boolean started = false;
	protected long startTime;
	
	public ShowMessageScript(Room room, String message) {
		super(room);
		
		this.message = message;
	}
	
	public static Script build(String id, Room room, Element elem) {
		//Element custom = elem.getChildByName("custom");
		//String message = custom.get("message", null);
		
		ShowMessageScript script = new ShowMessageScript(room, null);
		script.setId(id);
		
		return script;
	}
	
	@Override
	public boolean update() {
//		TheGame game = globals.getGame();
//		if(started && TimeUtils.timeSinceMillis(startTime) < 10000) {
//			game.setText("You have entered room " + room.getGridPosition().x + ", " + room.getGridPosition().y);
//		} else {
//			started = false;
//			game.setText(null);
//		}
		
		return super.update();
	}
	
	@Override
	public String getType() {
		return TYPE;
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
