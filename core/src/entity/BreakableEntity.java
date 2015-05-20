package entity;

import particle.ParticleEffect;
import misc.EntityBodyDef;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class BreakableEntity extends RectangleEntity {

	protected float breakSpeed;
	protected int startHealth;
	protected int health;
	protected String collisionEntity;
	
	protected BreakableEntity(Room room, float x, float y, float breakSpeed, int numShots, String shotEntity) {
		super(room, "cracked_block_1", BreakableEntity.getEntityBodyDef(x, y));
		
		this.breakSpeed = breakSpeed;
		this.startHealth = numShots;
		this.health = numShots;
		this.collisionEntity = shotEntity;
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		float breakSpeed = custom.getFloat("break_speed", 0);
		int numShots = custom.getInt("num_shots", -1);
		String shotEntity = custom.get("shot_entity", null);
		boolean frictionTop = custom.getBoolean("friction_top", false);
		BreakableEntity entity = new BreakableEntity(room, pos.x, pos.y, breakSpeed, numShots, shotEntity);
		entity.setId(id);
		entity.setBodyData();
		
		if(frictionTop) {
			entity.addFrictionTop(0.3f);
		}
		
		return entity;
	}
	
	public static EntityBodyDef getEntityBodyDef(float x, float y) {
		return new EntityBodyDef(new Vector2(x, y), new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE), BodyType.StaticBody);
	}
	
	@Override
	public String getType() {
		return "breakable";
	}
	
	@Override
	public boolean isTintable() {
		return false;
	}

	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if((breakSpeed != 0 && theGrid.getPlayer().getLinearVelocity().y >= breakSpeed)) {
			sounds.playSound("explode");
			createParticleEffect();
			getBodyData().setNeedsRemoved();	
		} else if(((collisionEntity == null && isShot(entity)) || 
				   (collisionEntity != null && entity.getType().equals(collisionEntity))) && 
				   startHealth > -1) {
			// TODO: This is getting called twice on each shot collision.
			health--;

			if(health <= 0) {
				sounds.playSound("explode");
				createParticleEffect();
				getBodyData().setNeedsRemoved();
			} else {
				int num = Math.min(4, 5 - (int)((((float)health) / startHealth) * 4));
				setSprite(num);
			}
		}
	}
	
	protected void setSprite(int num) {
		String key = "cracked_block_" + num;
		sprite.setRegion(textures.getTextureRegion(key));
	}
	
	protected void createParticleEffect() {
		ParticleEffect effect = new ParticleEffect("shot", getCenterX(), getCenterY());
		effect.minNumParticles(18)
		.maxNumParticles(20)
		.color(sprite.getColor())
		.endColor(sprite.getColor())
		.begin();
	}
}
