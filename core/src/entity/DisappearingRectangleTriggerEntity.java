package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.Animation;
import misc.EntityBodyDef;
import core.Room;

public class DisappearingRectangleTriggerEntity extends RectangleEntity {

	public static final String TYPE = "disappearing_rectangle_trigger";
	
	protected String targetId;
	protected boolean facingUp;
	protected boolean flipHor = false;
	protected Animation animation;
	
	protected DisappearingRectangleTriggerEntity(Room room, EntityBodyDef bodyDef, boolean facingUp, String targetId) {
		super(room, "trigger", bodyDef);
		
		this.targetId = targetId;
		this.facingUp = facingUp;
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");	
		boolean facingUp = custom.getBoolean("facing_up");
		String targetId = custom.get("target_id");
		
		Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		DisappearingRectangleTriggerEntity entity = new DisappearingRectangleTriggerEntity(room, bodyDef, facingUp, targetId);
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
		return false;
	}
	
	@Override
	public boolean update() {
		animation.update();
		sprite = animation.getSprite();
		
		sprite.setFlip(flipHor, !facingUp);
		
		return super.update();
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation(textureKey, 0.3f, false, false);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		
		if(facingUp) {
			sprite.setFlip(false, false);
		}
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);	
		
		if(isShot(entity)) {
			Entity targetEntity = room.getEntityById(targetId);
			if(targetEntity != null) {
				if(entity.getCenterX() < getCenterX()) {
					flipHor = true;
				} else {
					flipHor = false;
				}
				
				animation.play();
				DisappearingRectangleEntity target = (DisappearingRectangleEntity)targetEntity;
				target.disappear();
			}
		}
	}
}
