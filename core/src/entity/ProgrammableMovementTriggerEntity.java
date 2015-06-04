package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.Animation;
import misc.EntityBodyDef;
import core.Room;

public class ProgrammableMovementTriggerEntity extends RectangleEntity {

	public static final String TYPE = "programmable_movement_trigger";
	
	private Animation animation;
	
	protected ProgrammableMovementTriggerEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
        float size = Room.SQUARE_SIZE;
        EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(size, size), BodyType.StaticBody);
        
        ProgrammableMovementTriggerEntity entity = new ProgrammableMovementTriggerEntity(room, "question_mark_block", bodyDef);
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
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if(isShot(entity)) {
			animation.play();
			trigger();
		}
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
	
	protected void trigger() {
		for(Entity entity : room.getEntities()) {
			if(entity instanceof ProgrammableMovementEntity) {
				ProgrammableMovementEntity pme = (ProgrammableMovementEntity)entity;
				if(pme.isActivated()) {
					pme.onMovementTrigger();
				}
			}
		}
	}
}
