package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class MovingRectangleEntity extends RectangleEntity {

	public static final String TYPE = "moving_rectangle";
	
	protected Vector2 startPos;
	protected Vector2 endPos;
	protected Vector2 startVelocity;
	protected Vector2 endVelocity;
	protected boolean fromStart = true;
	
	protected MovingRectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 startPos, 
                                    Vector2 endPos, Vector2 startVelocity, Vector2 endVelocity) {
        super(room, textureKey, bodyDef);
        
        this.startPos = startPos;
        this.endPos = endPos;
        this.startVelocity = startVelocity;
        this.endVelocity = endVelocity;
    }
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key");
		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);
					
		Vector2 startPos = extractPos(room, custom, "start_pos");
		Vector2 endPos = extractPos(room, custom, "end_pos");
		Vector2 startVelocity = extractVelocity(custom, "start_velocity");
		Vector2 endVelocity = extractVelocity(custom, "end_velocity");
		
		MovingRectangleEntity entity = new MovingRectangleEntity(room, textureKey, bodyDef, startPos, endPos, startVelocity, endVelocity);
		entity.setId(id);
		entity.setBodyData();
		entity.getBody().setLinearVelocity(startVelocity);
		
		return entity;
	}
	
	protected static Vector2 extractPos(Room room, Element elem, String childName) {
		Element posElem = elem.getChildByName(childName);
		int row = posElem.getInt("row");
		int col = posElem.getInt("col");
		return Room.getWorldPosition(room, row, col);
	}
	
	protected static Vector2 extractVelocity(Element elem, String childName) {
		Element velocityElem = elem.getChildByName(childName);
		float vx = velocityElem.getInt("x");
		float vy = velocityElem.getInt("y");
		return new Vector2(vx, vy);
	}
	
	@Override
	public boolean update() {
		checkPosition();
		
		return super.update();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public boolean hasRandomColor() {
		return true;
	}

	protected void checkPosition() {
		Vector2 velocity = fromStart ? startVelocity : endVelocity;
		Vector2 pos = fromStart ? startPos : endPos;
		
		if((velocity.x > 0 && getRight() >= pos.x) || (velocity.x < 0 && getLeft() <= pos.x) ||
		   (velocity.y > 0 && getBottom() >= pos.y) || (velocity.y < 0 && getTop() <= pos.y)) {			
			if(fromStart) {
				body.setLinearVelocity(endVelocity);
			} else {
				body.setLinearVelocity(startVelocity);
			}
			
			fromStart = !fromStart;
		}
	}	
}
