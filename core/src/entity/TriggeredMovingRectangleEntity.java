package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class TriggeredMovingRectangleEntity extends MovingRectangleEntity implements IBlockChainDone {

	public static final String TYPE = "triggered_moving_rectangle";
	
	protected TriggeredMovingRectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 startPos, 
                                Vector2 endPos, Vector2 startVelocity, Vector2 endVelocity) {
		super(room, textureKey, bodyDef, startPos, endPos, startVelocity, endVelocity);
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
    
    	TriggeredMovingRectangleEntity entity = 
    			new TriggeredMovingRectangleEntity(room, textureKey, bodyDef, startPos, endPos, startVelocity, endVelocity);
    	entity.setId(id);
    	entity.setBodyData();
    
    	return entity;
    }
    
    @Override
    public String getType() {
    	return TYPE;
    }
	
	@Override
	public void onChainDone() {
		fromStart = true;
		body.setLinearVelocity(startVelocity);
	}
}
