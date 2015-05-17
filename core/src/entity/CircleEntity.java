package entity;

import particle.ParticleEffect;
import misc.EntityBodyDef;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class CircleEntity extends Entity {

	protected boolean removeOnContact = false;
	
	protected CircleEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
		super(room);
		
		Vector2 pos = bodyDef.position;
		Vector2 size = bodyDef.size;
		createSprite(textureKey, pos.x, pos.y, size.x, size.y);	
		createBody(bodyDef.bodyType);
		
		body.setFixedRotation(false);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		String textureKey = custom.get("texture_key");
		float radius = custom.getFloat("radius_scale") * Room.SQUARE_SIZE;
		boolean removeOnContact = custom.getBoolean("remove_on_contact", false);
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(radius * 2, radius * 2), BodyType.DynamicBody);
		CircleEntity entity = new CircleEntity(room, textureKey, bodyDef);
		entity.setId(id);
		entity.setBodyData();
		entity.setRemoveOnContact(removeOnContact);
		
		return entity;
	}
	
	@Override
	public String getType() {
		return "circle";
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);

		if(isShot(entity)) {
			body.setAngularVelocity(body.getAngularVelocity() + MathUtils.random(-0.5f, 0.5f));
		}
		
		if(removeOnContact && !isPlayer(entity)) {
			ParticleEffect.startParticleEffect("ball", getCenter());
			getBodyData().setNeedsRemoved();
		}
	}
	
	public void setRemoveOnContact(boolean remove) {
		removeOnContact = remove;
	}
	
	@Override
	protected FixtureDef createFixtureDef() {
		CircleShape shape = new CircleShape();
		
		float radius = (getWidth() / 2) * 0.92f;
		shape.setRadius(radius);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.2f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0.75f;

		return fixtureDef;
	}
}
