package misc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public final class EntityBodyDef {

	public Vector2 position;
	public Vector2 size;
	public BodyType bodyType;
	
	public EntityBodyDef(Vector2 position, Vector2 size, BodyType bodyType) {
		this.position = position;
		this.size = size;
		this.bodyType = bodyType;
	}
}
