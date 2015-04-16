package script;

import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class ShowMessageScript extends Script {

	protected String message;
	
	public ShowMessageScript(Room room, String message) {
		super(room);
		
		this.message = message;
	}
	
	public static Script build(String id, Room room, Element elem) {
		Element custom = elem.getChildByName("custom");
		String message = custom.get("message");
		
		ShowMessageScript script = new ShowMessageScript(room, message);
		script.setId(id);
		
		return script;
	}
	
	@Override
	public void run(Object... params) {
		// TODO: 
	}
}
