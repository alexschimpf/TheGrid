package script;

import misc.Globals;
import assets.Sounds;
import assets.Textures;

import com.badlogic.gdx.physics.box2d.World;

import core.IUpdate;
import core.Room;
import core.TheGrid;

public abstract class Script implements IUpdate {

	protected String id;
	protected Room room;
	protected Textures textures = Textures.getInstance();
	protected Sounds sounds = Sounds.getInstance();
	protected Globals globals = Globals.getInstance();
	protected TheGrid theGrid = globals.getTheGrid();
	protected World world = theGrid.getWorld();
	
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
