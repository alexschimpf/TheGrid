package script;

import misc.Globals;
import misc.IUpdate;
import assets.Sounds;
import assets.Textures;

import com.badlogic.gdx.physics.box2d.World;

import core.Room;
import core.TheGrid;
import entity.special.Player;

public abstract class Script implements IUpdate {

	protected static final Globals GLOBALS = Globals.getInstance();
	protected static final Textures TEXTURES = Textures.getInstance();
	protected static final Sounds SOUNDS = Sounds.getInstance();
	protected static final TheGrid THE_GRID = GLOBALS.getTheGrid();
	
	protected String id;
	protected Room room;
	
	public static Player getPlayer() {
		return THE_GRID.getPlayer();
	}
	
	public static World getWorld() {
		return THE_GRID.getWorld();
	}
	
	public Script(Room room) {
		this.room = room;	
	}
	
	public abstract String getType();
	
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
