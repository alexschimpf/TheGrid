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
	protected Animation animation;
	
	protected DisappearingRectangleTriggerEntity(Room room, EntityBodyDef bodyDef, String targetId) {
		super(room, "question_mark_block", bodyDef);
		
		this.targetId = targetId;
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");		
		String targetId = custom.get("target_id");
		
		Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		DisappearingRectangleTriggerEntity entity = new DisappearingRectangleTriggerEntity(room, bodyDef, targetId);
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
		animation.update();
		sprite = animation.getSprite();
		
		return super.update();
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation("question_mark_block", 0.36f, false, false);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
		sprite.setColor(TEXTURES.getRandomSchemeColor());
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);	
		
		if(isShot(entity)) {
			Entity targetEntity = room.getEntityById(targetId);
			if(targetEntity != null) {
				animation.play();
				DisappearingRectangleEntity target = (DisappearingRectangleEntity)targetEntity;
				target.disappear();
			}
		}
	}
}
