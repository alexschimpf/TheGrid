package entity.special;

import particle.ParticleEffect;
import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;

import core.Room;
import entity.Entity;
import entity.RectangleEntity;
import entity.special.Player.Direction;

public class PlayerShot extends RectangleEntity {

	public static final float SPEED = Player.MOVE_SPEED * 7;
	private static final float SIZE = Room.SQUARE_SIZE / 3;
	
	private Player player;
	
	public PlayerShot(Player player) {
		super(player.getRoom(), "white", PlayerShot.getEntityBodyDef(player));

		this.player = player;
		body.setBullet(true);
		body.setGravityScale(0);
	}
	
	public PlayerShot(Vector2 pos) {
		super(null, "white", PlayerShot.getEntityBodyDef(pos));

		body.setBullet(true);
		body.setGravityScale(0);
	}
	
	public static EntityBodyDef getEntityBodyDef(Player player) {
		float x = player.getCenterX();
		float y = player.getCenterY() - (SIZE / 2);
		
		if(player.getDirection() == Direction.Left) {
			x -= SIZE;
		}
		
		return new EntityBodyDef(new Vector2(x, y), new Vector2(SIZE, SIZE), BodyType.DynamicBody);
	}
	
	public static EntityBodyDef getEntityBodyDef(Vector2 pos) {
		return new EntityBodyDef(pos, new Vector2(SIZE, SIZE), BodyType.DynamicBody);
	}
	
	@Override
	public boolean update() {
		float scale = sprite.getScaleX();
		sprite.setScale(Math.min(SIZE, scale + (40 * Gdx.graphics.getDeltaTime())));
		
		return super.update();
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		if(entity instanceof Player) {
			return;
		}
		
		Body entityBody = entity.getBody();
		Fixture fixture = entityBody.getFixtureList().get(0);
		if(entityBody != null && fixture != null && fixture.isSensor()) {
			return;
		}
		
		ParticleEffect.startParticleEffect(new Vector2(getCenterX(), getCenterY()));
		getBodyData().setNeedsRemoved();
	}
	
	@Override
	public String getId() {
		return "shot";
	}
	
	@Override
	public String getType() {
		return "shot";
	}
	
	public void shoot() {
		setBodyData();	
		
		theGrid.addGlobalEntity(this);
		
		float vy = player.getLinearVelocity().y * 0.75f;
		float vx = PlayerShot.SPEED;
		if(player.getDirection() == Direction.Left) {
			vx = 0 - vx;
		}
		
		sprite.setScale(0);
				
		setLinearVelocity(vx, vy);
	}
	
	public void shoot(float vx, float vy) {
		setBodyData();
		
		theGrid.addGlobalEntity(this);
		
		setLinearVelocity(vx, vy);
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
		fixtureDef.filter.categoryBits = 0x0002;

		return fixtureDef;
	}
}
