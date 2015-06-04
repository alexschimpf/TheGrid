package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class FlipGravityEntity extends RectangleEntity {

	public static final String TYPE = "gravity_block";
	
	protected FlipGravityEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		Vector2 size = new Vector2(width, height);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		FlipGravityEntity entity = new FlipGravityEntity(room, "block", bodyDef);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		World world = Entity.getWorld();
		if(isShot(entity)) {
			SOUNDS.playSound("transport");
			world.setGravity(world.getGravity().scl(-1));
			Entity.getPlayer().getBody().setAwake(true);
		}
	}
}
