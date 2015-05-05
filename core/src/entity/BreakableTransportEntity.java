package entity;

import particle.ParticleEffect;
import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class BreakableTransportEntity extends TransportChainEntity {

	protected static final float TRANSPORT_COOLDOWN = 4000;
	
	protected int startHealth;
	protected int health;
	protected long lastTransportTime = 0;
	
	protected BreakableTransportEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 transportPos, int numShots) {
		super(room, textureKey, bodyDef, transportPos);
		
		this.startHealth = numShots;
		this.health = numShots;
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");		
		int transportRow = custom.getInt("transport_row");
		int transportCol = custom.getInt("transport_col");
		int numShots = custom.getInt("num_shots");
		
		Vector2 transportPos = Room.getWorldPosition(room, transportRow, transportCol);
		Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		BreakableTransportEntity entity = new BreakableTransportEntity(room, "blue_block", bodyDef, transportPos, numShots);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}

	@Override
	public String getType() {
		return "breakable_transport";
	}
	
	@Override
	public void onBeginContact(final Entity entity) {
		if(isShot(entity) && startHealth > -1) {
			health--;
			Color color = sprite.getColor();
			sprite.setColor(color.r, color.g, color.b, color.a - (1.0f / startHealth));
			
			if(health <= 0) {
				sounds.playSound("explode");
				ParticleEffect.startParticleEffect("blue", getCenter(), 20);
				getBodyData().setNeedsRemoved();
			} else {
				sounds.playSound("transport");
			}
		}
		
		if(TimeUtils.timeSinceMillis(lastTransportTime) > BreakableTransportEntity.TRANSPORT_COOLDOWN && 
		   health > 0 && isShot(entity)) {
			lastTransportTime = TimeUtils.millis();
			Gdx.app.postRunnable(new Runnable() {
			    @Override
				public void run() {
			    	updateChain(true);
					theGrid.getPlayer().setPosition(transportPos.x, transportPos.y);
				}
			});	
		}
	}
}
