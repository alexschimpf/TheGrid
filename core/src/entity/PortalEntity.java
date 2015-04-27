package entity;

import particle.ParticleEffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.Animation;
import misc.EntityBodyDef;
import core.Room;
import entity.special.PlayerShot;

public class PortalEntity extends RectangleEntity {

	protected enum Direction {
		Left, Right, Up, Down
	}

	protected String exitId;
	protected Direction exitDirection;
	protected Animation animation;
	
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
		
		if(this.exitDirection == Direction.Up || this.exitDirection == Direction.Down) {
			Vector2 pos = body.getPosition();
			body.setTransform(pos.x + (getHeight() / 4), pos.y - (getWidth() / 2), body.getAngle() + (MathUtils.degreesToRadians * 90));
		}
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String exitId = custom.get("exit_id", null);
		String exitDirection = custom.get("exit_direction");
		
		Vector2 size = new Vector2(Room.SQUARE_SIZE / 2, Room.SQUARE_SIZE);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		PortalEntity entity = new PortalEntity(room, "portal", bodyDef, exitId, exitDirection);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public boolean update() {
		animation.update();
		sprite = animation.getSprite();
		if(exitDirection == Direction.Up || exitDirection == Direction.Down) {
			sprite.setRotation(90);
		}
		
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
	    				left = getLeft() - entity.getWidth() - 0.1f;
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
        		
        		ParticleEffect.startParticleEffect("light_gray", entity.getCenter());
	        }
		});
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation("portal.png", 1, 4, 0.1f, true);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
		animation.play();
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
