package entity;

import misc.EntityBodyDef;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class PolygonEntity extends Entity {

	protected Vector2 origin;
	protected String fixtureKey;
	
	protected PolygonEntity(Room room, String textureKey, String fixtureKey, EntityBodyDef bodyDef) {
		super(room);
		
		this.fixtureKey = fixtureKey;
		
		Vector2 pos = bodyDef.position;
		Vector2 size = bodyDef.size;
		createSprite(textureKey, pos.x, pos.y, size.x, size.y);	
		createBody(bodyDef.bodyType);
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		// TODO:
		return null;
	}
	
	@Override
	public String getType() {
		return "polygon";
	}

	@Override
	protected FixtureDef createFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.1f;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0.1f;
		
		return fixtureDef;
	}
	
	@Override
	protected void attachFixture(FixtureDef fixtureDef) {
//		BodyEditorLoader loader = Textures.getInstance().getLoader();
//		loader.attachFixture(body, fixtureKey, fixtureDef, getWidth());
//		origin = loader.getOrigin(fixtureKey, getWidth()).cpy();
	}
}
