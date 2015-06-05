package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class PathRectangleEntity extends RectangleEntity {

	public static final String TYPE = "path_rectangle";
	
	protected int pathPos = 0;
	protected long lastMoveTime;
	protected float moveDelay = 1000;
	protected boolean reverseOnDone = true;
	protected boolean reverseOnBlocked = false;
	protected Array<Vector2> path;
	
	protected PathRectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef, Array<Vector2> path, 
			                      float moveDelay, boolean reverseOnDone, boolean reverseOnBlocked) {
		super(room, textureKey, bodyDef);
	
		this.path = path;
		this.moveDelay = moveDelay;
		this.reverseOnDone = reverseOnDone;
		this.reverseOnBlocked = reverseOnBlocked;
		
		lastMoveTime = TimeUtils.millis();
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key");
		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		float moveDelay = custom.getFloat("move_delay", 1000);
		boolean reverseOnDone = custom.getBoolean("reverse_on_done", false);
		boolean reverseOnBlocked = custom.getBoolean("reverse_on_blocked", false);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);

		Array<Vector2> path = new Array<Vector2>();
		path.add(pos);
		
		Element pathElem = custom.getChildByName("path");
		if(pathElem != null) {
			Array<Element> positions = pathElem.getChildrenByName("position");
			for(Element position : positions) {
				int row = position.getInt("row");
				int col = position.getInt("col");
				path.add(Room.getWorldPosition(room, row, col));
			}
		}
		
		PathRectangleEntity entity = new PathRectangleEntity(room, textureKey, bodyDef, path, moveDelay, reverseOnDone, reverseOnBlocked);
		entity.setId(id);
		entity.setBodyData();

		return entity;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public boolean hasRandomColor() {
		return true;
	}
	
	@Override
	public boolean update() {
		if(TimeUtils.timeSinceMillis(lastMoveTime) > moveDelay) {
			moveNext();
		}
		
		return super.update();
	}
	
	protected void moveNext() {
		int absPathPos = Math.abs(pathPos);

		pathPos++;
		if(absPathPos + 1 >= path.size) {
			if(reverseOnDone) {
				pathPos = 0 - pathPos + 2;
			} else {
				pathPos = 0;
			}	
		}
		
		absPathPos = Math.abs(pathPos);
		Vector2 nextPos = path.get(absPathPos);
		
		if(room.entityExistsInArea(nextPos, getWidth(), getHeight(), this)) {
			if(reverseOnBlocked) {
				pathPos = 0 - pathPos + 1;
			} else {
				pathPos--;
			}
		} else {
			lastMoveTime = TimeUtils.millis();
			setPosition(nextPos.x, nextPos.y);
			Vector2 pv = Entity.getPlayer().getLinearVelocity();
			Entity.getPlayer().setLinearVelocity(pv.x + 0.01f, pv.y + 0.1f);
		}			
	}
}
