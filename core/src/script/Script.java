package script;

import core.IUpdate;
import core.Room;

public abstract class Script implements IUpdate {

	protected String id;
	protected Room room;
	
	public Script(Room room) {
		this.room = room;	
	}
	
	public abstract void run(Object... params);

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public void done() {
	}

	public void setId(String id) {
		if(id == null) {
			id = Integer.toString(hashCode());
		}
		
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
