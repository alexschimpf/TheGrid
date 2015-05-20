package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class RectangleEntity extends Entity {

	protected RectangleEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room);
		
		Vector2 pos = bodyDef.position;
		Vector2 size = bodyDef.size;
		createSprite(textureKey, pos.x, pos.y, size.x, size.y);	
		createBody(bodyDef.bodyType);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key", null);
		
		BodyType bodyType;
		String bodyTypeKey = custom.get("body_type", null);
		if(bodyTypeKey == null || bodyTypeKey.equals("static")) {
			bodyType = BodyType.StaticBody;
		} else if(bodyTypeKey.equals("dynamic")) {
			bodyType = BodyType.DynamicBody;
		} else {
			bodyType = BodyType.KinematicBody;
		}
		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), bodyType);
		RectangleEntity entity = new RectangleEntity(room, textureKey, bodyDef);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}

	public static RectangleEntity build(String id, Room room, String textureKey, EntityBodyDef bodyDef) {
		RectangleEntity entity = new RectangleEntity(room, textureKey, bodyDef);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	@Override
	public String getType() {
		return "rectangle";
	}
	
	public void addFrictionTop(float friction) {        
		Vector2 center = new Vector2(getCenterX(), getTop());
		center = body.getLocalPoint(center);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((getWidth() / 2) * 0.95f, 0.01f, center, body.getAngle());		
		body.createFixture(shape, 0).setFriction(friction);
		
		shape.dispose();
	}
	
	@Override
	protected FixtureDef createFixtureDef() {
		PolygonShape shape = new PolygonShape();
		
		float width = (getWidth() / 2); //* 0.95f;
		float height = (getHeight() / 2); //* 0.90f;
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;

		return fixtureDef;
	}
}
