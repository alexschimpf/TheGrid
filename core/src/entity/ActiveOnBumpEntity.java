package entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;
import entity.special.Player;

public class ActiveOnBumpEntity extends RectangleEntity {

	public static final String TYPE = "active_on_bump";
	
	protected String textureKey;
	
	protected ActiveOnBumpEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
		
		this.textureKey = textureKey;
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key", null);
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		boolean startActive = custom.getBoolean("start_active", true);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.StaticBody);
		ActiveOnBumpEntity entity = new ActiveOnBumpEntity(room, textureKey, bodyDef);
		entity.setId(id);
		entity.setBodyData();
		entity.setActive(startActive);
		
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
		
		Player player = Entity.getPlayer();
		if(isPlayer(entity) && player.getTop() >= getBottom() - (getHeight() / 10))	{
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					Fixture fixture = body.getFixtureList().get(0);
					setActive(fixture.isSensor());
				}				
			});
		}
	}
	
	public void setActive(boolean active) {
		Fixture fixture = body.getFixtureList().get(0);
		if(active) {	
			fixture.setSensor(false);
			sprite.setRegion(TEXTURES.getTextureRegion(textureKey));
		} else {
			fixture.setSensor(true);
			sprite.setRegion(TEXTURES.getTextureRegion("inactive_block"));
		}
	}
}
