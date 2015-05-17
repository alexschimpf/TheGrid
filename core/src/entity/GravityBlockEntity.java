package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class GravityBlockEntity extends RectangleEntity {

	protected GravityBlockEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		Vector2 size = new Vector2(width, height);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		GravityBlockEntity entity = new GravityBlockEntity(room, "block", bodyDef);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public String getType() {
		return "gravity_block";
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		if(isShot(entity)) {
			sounds.playSound("transport");
			world.setGravity(world.getGravity().scl(-1));
			theGrid.getPlayer().getBody().setAwake(true);
		}
	}
}
