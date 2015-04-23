package entity.sensor;

import script.Script;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.Entity;

public class SensorEntity extends Entity {
	
	protected Vector2 pos;
	protected Vector2 size;
	protected String scriptId;
	
	protected SensorEntity(Room room, Vector2 pos, Vector2 size, String scriptId) {
		super(room);
		
		this.pos = pos;
		this.size = size;
		this.scriptId = scriptId;
		
		createBody(BodyType.StaticBody);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String scriptId = custom.get("script_id");
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		SensorEntity entity = new SensorEntity(room, pos, new Vector2(width, height), scriptId);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}

	@Override
	public String getType() {
		return "sensor";
	}
	
	@Override
	public boolean update() {
		return false;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		return;
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		if(isPlayer(entity)) {
			Script script = room.getScriptById(scriptId);
			script.run();
		}
	}
	
	@Override
	protected void createBody(BodyType bodyType, FixtureDef fixtureDef) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		
		float cx = pos.x + (getWidth() / 2);
		float cy = pos.y + (getHeight() / 2);
		bodyDef.position.set(cx, cy);

		body = world.createBody(bodyDef);		
		body.setFixedRotation(true);
		body.setAwake(false);
		
		attachFixture(fixtureDef);		
	}

	@Override
	protected FixtureDef createFixtureDef() {
		PolygonShape shape = new PolygonShape();
		
		float width = (getWidth() / 2);
		float height = (getHeight() / 2);
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.isSensor = true;

		return fixtureDef;
	}
	
	@Override
	public float getWidth() {
		return size.x;
	}
	
	@Override
	public float getHeight() {
		return size.y;
	}
}
