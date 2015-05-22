package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class TransportEntity extends RectangleEntity {

	protected Vector2 transportPos;
	
	protected TransportEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 transportPos) {
		super(room, textureKey, bodyDef);
		
		this.transportPos = transportPos;
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");		
		int transportRow = custom.getInt("transport_row");
		int transportCol = custom.getInt("transport_col");
		
		Vector2 transportPos = Room.getWorldPosition(room, transportRow, transportCol);
		Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		TransportEntity entity = new TransportEntity(room, "block", bodyDef, transportPos);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public String getType() {
		return "transport";
	}
	
	@Override
	public void onBeginContact(final Entity entity) {
		super.onBeginContact(entity);
		
		if(isPlayer(entity)) {
			Gdx.app.postRunnable(new Runnable() {
			    @Override
				public void run() {
			    	SOUNDS.playSound("transport");
			    	entity.setLinearVelocity(0, 0.01f);
					entity.setPosition(transportPos.x, transportPos.y);
				}
			});	
		}
	}
}
