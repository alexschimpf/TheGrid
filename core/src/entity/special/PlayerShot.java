package entity.special;

import particle.ParticleEffect;
import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
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

public final class PlayerShot extends RectangleEntity {

	private static final String[] NO_PARTICLE_EFFECT_TYPES = new String[] {
		"portal",
		"breakable",
	};	
	public static final float SPEED = Player.MOVE_SPEED * 7;
	private static final float SIZE = Room.SQUARE_SIZE / 3;
	
	private Player player;
	
	public PlayerShot(Player player) {
		super(player.getRoom(), "shot", PlayerShot.getEntityBodyDef(player));

		this.player = player;
		body.setBullet(true);
		body.setGravityScale(0);
	}
	
	public PlayerShot(Vector2 pos) {
		super(null, "shot", PlayerShot.getEntityBodyDef(pos));

		this.room = Entity.getPlayer().getRoom();
		body.setBullet(true);
		body.setGravityScale(0);
	}
	
	public static EntityBodyDef getEntityBodyDef(Player player) {
		float x = player.getCenterX();
		float y = player.getCenterY() - (SIZE / 2);
		
		if(player.getDirection() == Direction.LEFT) {
			x -= SIZE;
		}
		
		return new EntityBodyDef(new Vector2(x, y), new Vector2(SIZE, SIZE), BodyType.DynamicBody);
	}
	
	public static EntityBodyDef getEntityBodyDef(Vector2 pos) {
		return new EntityBodyDef(pos, new Vector2(SIZE, SIZE), BodyType.DynamicBody);
	}
	
	protected static boolean doParticleEffect(String type) {
		for(String dont : NO_PARTICLE_EFFECT_TYPES) {
			if(type.equals(dont)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean update() {
		float scale = sprite.getScaleX();
		sprite.setScale(Math.min(SIZE, scale + (40 * Gdx.graphics.getDeltaTime())));
		sprite.setPosition(getLeft(), getTop());
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
		
		return false;
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		Vector2 velocity = getLinearVelocity();
		float y = getCenterY();
		float x = velocity.x > 0 ? getRight() : getLeft();
		if(velocity.y != 0) {
			y = velocity.y > 0 ? getBottom() : getTop();
		}
		
		// May have hit a chain pad, for example.
		if(entity == null) {
			createParticleEffect(x, y);
			getBodyData().setNeedsRemoved();
			return;
		}
		
		if(entity instanceof Player) {
			return;
		}
		
		Body entityBody = entity.getBody();
		Fixture fixture = entityBody.getFixtureList().get(0);
		if(entityBody != null && fixture != null && fixture.isSensor()) {
			return;
		}
		
		if(PlayerShot.doParticleEffect(entity.getType())) {
			createParticleEffect(x, y);
		}
		
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
		
		Entity.getTheGrid().addGlobalEntity(this);
		
		float vy = player.getLinearVelocity().y * 0.75f;
		float vx = PlayerShot.SPEED * 1.25f;
		
		if(vy > 0) {
			vy = Math.min(vy, Math.abs(vx) * 0.75f);
		}
		
		
		if(player.getDirection() == Direction.LEFT) {
			vx = 0 - vx;
		}
		
		sprite.setScale(0);
				
		body.setAngularVelocity(0.2f * (vx < 0 ? 1 : -1));
		setLinearVelocity(vx, vy);
	}
	
	public void shoot(float vx, float vy) {
		setBodyData();
		
		Entity.getTheGrid().addGlobalEntity(this);
		
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
		fixtureDef.density = 0.8f;
		fixtureDef.friction = 0.2f;
		fixtureDef.restitution = 0.2f;
		fixtureDef.filter.categoryBits = 0x0002;
		
		return fixtureDef;
	}
	
	protected void createParticleEffect(float x, float y) {
		ParticleEffect effect = new ParticleEffect("shot", x, y);
		effect.begin();
	}
}
