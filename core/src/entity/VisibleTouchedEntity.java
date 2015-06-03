package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class VisibleTouchedEntity extends RectangleEntity {

	public static final String TYPE = "visible_touched";
	
	protected VisibleTouchedEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
		
		sprite.setAlpha(0);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key", null);
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.StaticBody);
		VisibleTouchedEntity entity = new VisibleTouchedEntity(room, textureKey, bodyDef);
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
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		sprite.setAlpha(1);
	}
	
	@Override
	public void onEndContact(Entity entity) {
		super.onEndContact(entity);
		
		if(numContacts == 0) {
			sprite.setAlpha(0);
		}
	}
}
