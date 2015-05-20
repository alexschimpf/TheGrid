package entity.special;

import particle.ParticleEffect;
import misc.Animation;
import misc.EntityBodyDef;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;

import core.Room;
import entity.RectangleEntity;

public class Player extends RectangleEntity {

	public static enum Direction {
		Left,
		Right
	}
	
	public static final float MOVE_SPEED = 18;
	public static final float JUMP_IMPULSE = -100;
	public static final float SHOOT_COOLDOWN = 150;
	public static final float JUMP_COOLDOWN = 100;

	private boolean jumping = false;
	private int numFootContacts = 0;
	private long lastShotTime = 0;
	private long lastJumpTime = 0;
	private long lastBlinkTime = TimeUtils.millis();
	private float blinkCooldown = MathUtils.random(1000, 6000);
	private Direction direction = Direction.Right;
	private Animation blinkAnimation;
	private Animation jumpAnimation;
	
	public Player(Vector2 worldPos) {
		super(null, "player", Player.getEntityBodyDef(worldPos));
		
		id = "player";
		
		attachExtraFixtures();
		
		body.setAwake(true);
		body.setBullet(true);
		MassData md = body.getMassData();
		md.mass = 5.6953125f;
		body.setMassData(md);
	}
	
	private static EntityBodyDef getEntityBodyDef(Vector2 worldPos) {
		float width = Room.SQUARE_SIZE * 0.75f;
		float height = Room.SQUARE_SIZE;
		return new EntityBodyDef(worldPos, new Vector2(width, height), BodyType.DynamicBody);
	}
	
	@Override
	public String getType() {
		return "player";
	}
	
	@Override
	public boolean update() {
		if(!isJumping() && TimeUtils.timeSinceMillis(lastBlinkTime) > blinkCooldown) {
			lastBlinkTime = TimeUtils.millis();
			blinkCooldown = MathUtils.random(1000, 5000);
			blinkAnimation.play();
		}
		
		if(jumpAnimation.isPlaying()) {
			jumpAnimation.update();
			sprite = jumpAnimation.getSprite();
		} else if(blinkAnimation.isPlaying()) {
			blinkAnimation.update();
			sprite = blinkAnimation.getSprite();
		}
		
		Room currRoom = theGrid.getRoomAt(getCenterX(), getCenterY());
		if(room != currRoom) {
			if(room != null) {
				//room.setAwake(false);
			}
			
			room = currRoom;
//			room.setAwake(true);
		} 
		
		sprite.setFlip(direction == Direction.Left, true);

		return super.update();
	}

	public void moveLeft() {
		move(Direction.Left);
	}
	
	public void moveRight() {
		move(Direction.Right);
	}
	
	public void jump() {
		if(!isJumping() && TimeUtils.timeSinceMillis(lastJumpTime) > JUMP_COOLDOWN) {
			jumpAnimation.play();
			lastJumpTime = TimeUtils.millis();
			sounds.playSound("jump");
			float x = body.getWorldCenter().x;
			float y = body.getWorldCenter().y;
			body.applyLinearImpulse(0, JUMP_IMPULSE, x, y, true);
		}
	}
	
	public void stopMove() {
		if(!jumping && !blinkAnimation.isPlaying()) {
			setSprite("player");
		}
		
		body.setLinearVelocity(0, body.getLinearVelocity().y);
	}
	
	public void stopJump() {
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(body.getLinearVelocity().x, 0.02f);
		}
	}
	
	public void shoot() {
		if(TimeUtils.timeSinceMillis(lastShotTime) > SHOOT_COOLDOWN) {
			lastShotTime = TimeUtils.millis();
			sounds.playSound("shoot");
			PlayerShot shot = new PlayerShot(this);
			shot.shoot();
		}		
	}
	
	public float getFrontX() {
		if(direction == Direction.Left) {
			return getLeft();
		} else {
			return getRight();
		}
	}
	
	public float getBackX() {
		if(direction == Direction.Left) {
			return getRight();
		} else {
			return getLeft();
		}
	}

	public Direction getDirection() {
		return direction;
	}
	
	public boolean isJumping() {
		return jumping;
	}
	
	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}
	
	public void incrementFootContacts() {
		numFootContacts++;	
		setJumping(numFootContacts < 1);
	}
	
	public void decrementFootContacts() {
		numFootContacts--;
		if(numFootContacts < 0) {
			numFootContacts = 0;
		}
		
		setJumping(numFootContacts < 1);
	}
	
	public boolean canSeeRoom(Room room) {
		Vector2 playerRoomPos = getRoom().getGridPosition();
		Vector2 roomPos = room.getGridPosition();
		int px = (int)playerRoomPos.x;
		int py = (int)playerRoomPos.y;
		int rx = (int)roomPos.x;
		int ry = (int)roomPos.y;
		
		return (px == rx && Math.abs(py - ry) <= 1) || (py == ry && Math.abs(px - rx) <= 1) ||
		       (Math.abs(py - ry) <= 1 && Math.abs(px - rx) <= 1);
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		super.createSprite(textureKey, x, y, width, height);
		
		Vector2 pos = new Vector2(x, y);
		Vector2 size = new Vector2(width, height);

		blinkAnimation = new Animation("player_blink", 0.56f, false, false);
		blinkAnimation.setSprite(pos, size);
		
		jumpAnimation = new Animation("player_jump", 0.27f, false, false);
		jumpAnimation.setSprite(pos, size);
	}
	
	@Override
	protected FixtureDef createFixtureDef() {
		PolygonShape shape = new PolygonShape();
		
		float width = (getWidth() / 2) * 0.90f;
		float height = (getHeight() / 2) * 0.90f;
		shape.setAsBox(width, height);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1;
		fixtureDef.friction = 0.1f;
		fixtureDef.restitution = 0;
		fixtureDef.filter.categoryBits = 0x0001;
		fixtureDef.filter.maskBits = ~0x0002;

		return fixtureDef;
	}
	
	@Override
	protected boolean isTintable() {
		return false;
	}
	
	private void attachExtraFixtures() {
		CircleShape shape = new CircleShape();

		float radius = getWidth() / 6f;
		shape.setRadius(radius);
		shape.setPosition(new Vector2(0.5f, 1.6f));
		Fixture fixture1 = body.createFixture(shape, 0);
		fixture1.setSensor(true);
		
		shape.setPosition(new Vector2(-0.5f, 1.6f));
		Fixture fixture2 = body.createFixture(shape, 0);
		fixture2.setSensor(true);
		
		shape.dispose();
	}

	private void move(Direction moveDirection) {
		direction = moveDirection;
		
		Vector2 pos = new Vector2(getLeft() + (getWidth() / 4.0f), getBottom() - (getHeight() / 15));
		float vx = Player.MOVE_SPEED;
		if(moveDirection == Direction.Left) {
			vx = 0 - vx;
			pos.x = getRight() - (getWidth() / 4.0f);
		}

		if(numContacts > 0 && !isJumping()) {
			setSprite("player_move");
			boolean particleMoveLeft = moveDirection == Direction.Right;
			ParticleEffect.startParticleEffect("shot", pos, 1, particleMoveLeft, 500);
		} else if(!jumpAnimation.isPlaying()){
			setSprite("player");
		}
		
		body.setLinearVelocity(vx, body.getLinearVelocity().y);
	}
	
	private void setSprite(String textureKey) {
		sprite.setRegion(textures.getTextureRegion(textureKey));
		sprite.setFlip(direction == Direction.Left, true);
	}
}
