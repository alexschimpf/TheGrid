package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;
import entity.special.PlayerShot;

public class PortalEntity extends RectangleEntity {

	protected enum Direction {
		Left, Right, Up, Down
	}
	
	protected float blue = 0;
	protected String exitId;
	protected Direction exitDirection;
	
	protected PortalEntity(Room room, String textureKey, EntityBodyDef bodyDef, String exitId, String exitDirection) {
		super(room, textureKey, bodyDef);
		
		this.exitId = exitId;
		
		if(exitDirection != null) {
			for(Direction direction : Direction.values()) {
				if(direction.toString().toLowerCase().equals(exitDirection)) {
					this.exitDirection = direction;
					break;
				}
			}
		}
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String exitId = custom.get("exit_id", null);
		String exitDirection = custom.get("exit_direction");
		
		Vector2 size = determineSize(exitDirection);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		PortalEntity entity = new PortalEntity(room, "blue", bodyDef, exitId, exitDirection);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	protected static Vector2 determineSize(String exitDirection) {
		float width, height;
		
		if(exitDirection.equals("left") || exitDirection.equals("right")) {
			width = Room.SQUARE_SIZE / 2;
			height = Room.SQUARE_SIZE;
		} else {
			width = Room.SQUARE_SIZE;
			height = Room.SQUARE_SIZE / 2;
		}
		
		return new Vector2(width, height);
	}
	
	@Override
	public boolean update() {
		blue += 0.1;
		sprite.setColor(0, 0, Math.abs(MathUtils.sin(blue)), 1);
		
		return super.update();
	}
	
	@Override
	public String getType() {
		return "portal";
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if(exitId != null && isShot(entity)) {
			PortalEntity exit = (PortalEntity)room.getEntityById(exitId);
			exit.onExit(entity);	
		}
	}
	
	public void onExit(final Entity entity) {
		Gdx.app.postRunnable(new Runnable() {
	        @Override
	        public void run() {
	        	Vector2 exitVelocity = determineExitVelocity();
	        	float vx = exitVelocity.x;
	    		float vy = exitVelocity.y;

	    		float left = 0;
	    		float top = 0;
	    		switch(exitDirection) {
	    			case Down:
	    				left = getCenterX() - (entity.getWidth() / 2);
	        			top = getBottom() + 0.1f;
	    				break;
	    			case Up:
	    				left = getCenterX() - (entity.getWidth() / 2);
	        			top = getTop() - entity.getHeight() - 0.1f;
	    				break;
	    			case Right:
	    				left = getRight();
	        			top = getTop() + (entity.getHeight() / 2) + 0.1f;
	    				break;
	    			case Left:
	    				left = getLeft() - entity.getWidth();
	        			top = getTop() + (entity.getHeight() / 2) - 0.1f;
	    				break;
	    		}
        		
        		if(isShot(entity)) {
	    			PlayerShot shot = new PlayerShot(new Vector2(left, top));
	    			shot.shoot(vx, vy);
	    		} else {
	    			entity.setPosition(left, top);
	    			entity.setLinearVelocity(vx, vy);	
	    		}
	        }
		});
	}
	
	protected Vector2 determineExitVelocity() {
		float vx = 0;
		float vy = 0;
		
		float speed = PlayerShot.SPEED;
		
		switch(exitDirection) {
			case Left:
				vx = -speed;
				vy = 0;
				break;
			case Right:
				vx = speed;
				vy = 0;
				break;
			case Up:
				vx = 0;
				vy = -speed;
				break;
			case Down:
				vx = 0;
				vy = speed;
				break;
		}
		
		return new Vector2(vx, vy);
	}
}
