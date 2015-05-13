package entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.XmlReader.Element;

import misc.Animation;
import misc.EntityBodyDef;
import core.Room;

public class DisappearingRectangleEntity extends RectangleEntity {

	protected static final float DURATION = 1000;
	
	protected long startTime;
	protected boolean disappearing = false;
	protected Animation animation;
	
	protected DisappearingRectangleEntity(Room room, EntityBodyDef bodyDef) {
		super(room, "disappearing_gray_block", bodyDef);
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		
		Vector2 size = new Vector2(width, height);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
		DisappearingRectangleEntity entity = new DisappearingRectangleEntity(room, bodyDef);
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
		
		animation.update();
		sprite = animation.getSprite();
			
		if(disappearing) {
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
				DisappearingRectangleEntity entity = new DisappearingRectangleEntity(room, bodyDef);
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
			animation.play();
			disappearing = true;
			startTime = TimeUtils.millis();
		}
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		Vector2 pos = new Vector2(x, y);
		Vector2 size = new Vector2(width, height);
		
		animation = new Animation("disappearing_gray_block.png", 1, 10, (DURATION / 1000.0f) / 10, false);
		animation.setSprite(pos, size);
		sprite = animation.getSprite();		
	}
}
