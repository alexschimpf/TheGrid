package entity.sensor;

import script.Script;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

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
}
