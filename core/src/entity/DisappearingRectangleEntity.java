package entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.EntityBodyDef;
import core.Room;

public class DisappearingRectangleEntity extends RectangleEntity {

	protected static final float DURATION = 1000;
	
	protected long startTime;
	protected boolean disappearing = false;
	protected String textureKey;
	
	protected DisappearingRectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room, textureKey, bodyDef);
		
		this.textureKey = textureKey;
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");		
		String textureKey = custom.get("texture_key");
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		
		Vector2 size = new Vector2(width, height);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		DisappearingRectangleEntity entity = new DisappearingRectangleEntity(room, textureKey, bodyDef);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public String getType() {
		return "disappearing_rectangle";
	}
	
	@Override
	public boolean update() {
		float timeSinceStart = TimeUtils.timeSinceMillis(startTime);
			
		if(disappearing) {
			Color color = sprite.getColor();
			color.a = Math.max(0, 1 - (timeSinceStart / DURATION));
			sprite.setColor(color.r, color.g, color.b, Math.min(1, color.a));
			
			if(timeSinceStart > DURATION) {
				disappearing = false;
				startTime = TimeUtils.millis();
				getBodyData().setNeedsRemoved();
			}
		}
		
		return super.update();
	}
	
	@Override
	public void done() {
		final Vector2 size = new Vector2(getWidth(), getHeight());
		final Vector2 pos = getLeftTop();
		final EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		
		Timer timer = new Timer();
		timer.scheduleTask(new Task() {
			@Override
			public void run() {
				DisappearingRectangleEntity entity = new DisappearingRectangleEntity(room, textureKey, bodyDef);
				entity.setId(id);
				entity.setBodyData();
				room.addEntity(entity);
			}			
		}, DURATION / 1000);							
		
		super.done();
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);	
		
		if(isPlayer(entity) && !disappearing) {
			disappearing = true;
			startTime = TimeUtils.millis();
		}
	}
}
