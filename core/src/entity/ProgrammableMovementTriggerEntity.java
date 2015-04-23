package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class ProgrammableMovementTriggerEntity extends RectangleEntity {

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
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if(isShot(entity)) {
			trigger();
		}
	}
	
	protected void trigger() {
		for(Object entity : room.getEntities()) {
			if(entity instanceof ProgrammableMovementEntity) {
				ProgrammableMovementEntity pme = (ProgrammableMovementEntity)entity;
				if(pme.isActivated()) {
					pme.onMovementTrigger();
				}
			}
		}
	}
}
