package entity;

import misc.EntityBodyDef;
import misc.IBlockChainDone;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import core.TheGrid;
import entity.special.Player;

public class MovingRectangleEntity extends RectangleEntity implements IBlockChainDone {

	public static final String TYPE = "moving_rectangle";
	
	protected Vector2 startPos;
	protected Vector2 endPos;
	protected Vector2 startVelocity;
	protected Vector2 endVelocity;
	protected boolean fromStart = true;
	protected boolean started = false;
	protected boolean isMovingVertically = false;
	protected boolean isPlayerTouching = false;
	
	protected MovingRectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 startPos, 
                                    Vector2 endPos, Vector2 startVelocity, Vector2 endVelocity) {
        super(room, textureKey, bodyDef);
        
        this.startPos = startPos;
        this.endPos = endPos;
        this.startVelocity = startVelocity;
        this.endVelocity = endVelocity;
        
        if(endPos.y != startPos.y) {
        	isMovingVertically = true;
        }
    }
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key");
		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		boolean startOnCreate = custom.getBoolean("start_on_create", true);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);
					
		Vector2 startPos = extractPos(room, custom, "start_pos");
		Vector2 endPos = extractPos(room, custom, "end_pos");
		Vector2 startVelocity = extractVelocity(custom, "start_velocity");
		Vector2 endVelocity = extractVelocity(custom, "end_velocity");
		
		MovingRectangleEntity entity = new MovingRectangleEntity(room, textureKey, bodyDef, startPos, endPos, startVelocity, 
				                                                 endVelocity);
		entity.setId(id);
		entity.setBodyData();

		if(startOnCreate) {
			entity.getBody().setLinearVelocity(startVelocity);
			entity.start();
		}
		
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
		if(!started) {
			return false;
		}
		
		checkPosition();
		
		// Terrible hack to fix jumping on vertically moving blocks.
		Player player = Entity.getPlayer();
		if(isMovingVertically && !player.isJumping() && isPlayerTouching && 
		   player.getBottom() + 0.1f >= getTop() && ((player.getLeft() < getRight() && player.getRight() > getRight()) || 
				                                     (player.getRight() > getLeft() && player.getLeft() < getLeft()) ||
				                                     (player.getLeft() >= getLeft() && player.getRight() <= getRight()))) {
			float fy = TheGrid.DEFAULT_GRAVITY * 150;
			if(getLinearVelocity().y > 0) {
				fy = TheGrid.DEFAULT_GRAVITY * 115;
			}
			
			player.setOnVertRide(getLinearVelocity().y > 0);
			
			player.getBody().applyForce(0, fy, getCenterX(), getCenterY(), true);
		}
		
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
	
	@Override
	public void onBeginContact(Entity entity) {
		if(isPlayer(entity)) {	
			isPlayerTouching = true;
		}
	}
	
	@Override
	public void onEndContact(Entity entity) {
		if(isPlayer(entity)) {
			isPlayerTouching = false;
			((Player)entity).setOnVertRide(null);
		}
	}

	@Override
	public void onChainDone() {
		fromStart = true;
		body.setLinearVelocity(startVelocity);
		start();
	}
	
	public void start() {
		started = true;
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
