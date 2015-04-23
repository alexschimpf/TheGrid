package entity;

import script.UpdateTransportChainScript;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class TransportChainEntity extends RectangleEntity {

	protected boolean transport = false;
	protected Vector2 transportPos;
	
	protected TransportChainEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 transportPos) {
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
		TransportChainEntity entity = new TransportChainEntity(room, "", bodyDef, transportPos);
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
		
		if(transport && isPlayer(entity)) {
			Gdx.app.postRunnable(new Runnable() {
			    @Override
				public void run() {
			    	sounds.playSound("transport");
			    	updateChain(false);
					entity.setPosition(transportPos.x, transportPos.y);
				}
			});	
		}
	}
	
	public void updateChain(boolean completed) {
		for(Object object : room.getScripts()) {
			if(object instanceof UpdateTransportChainScript) {
				UpdateTransportChainScript script = (UpdateTransportChainScript)object;
				script.run(completed);
				return;
			}
		}
	}
	
	public void setColor(Color color) {
		String textureKey = "gray_block";
		if(color.equals(Color.BLUE)) {
			textureKey = "blue_block";
		} else if(color.equals(Color.RED)) {
			textureKey = "red_block";
		}

		TextureRegion tr = textures.getTextureRegion(textureKey);
		sprite.setRegion(tr);
	}
	
	public void setTransport(boolean transport) {
		this.transport = transport;
	}
}
