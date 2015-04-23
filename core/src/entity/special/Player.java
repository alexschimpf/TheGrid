package entity.special;

import misc.Animation;
import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import core.Room;
import entity.RectangleEntity;

public class Player extends RectangleEntity {

	public static enum Direction {
		Left,
		Right
	}
	
	public static final float MOVE_SPEED = 18;
	public static final float JUMP_IMPULSE = -100;
	
	private boolean jumping = false;
	private int numFootContacts = 0;
	private Direction direction = Direction.Right;
	private Animation animation;
	
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
		if(!isJumping() && getLinearVelocity().x != 0) {
			animation.play();
		} else {
			//animation.stop();
		}
		
		animation.update();
		sprite = animation.getSprite();
		
		Room currRoom = theGrid.getRoomAt(getCenterX(), getCenterY());
		if(room != currRoom) {
			if(room != null) {
				//room.setAwake(false);
			}
			
			room = currRoom;
			room.setAwake(true);
		} 
		
		if(direction == Direction.Left) {
			sprite.setFlip(true, true);
		} else {
			sprite.setFlip(false, true);
		}
		
		return super.update();
	}

	public void moveLeft() {
		move(Direction.Left);
	}
	
	public void moveRight() {
		move(Direction.Right);
	}
	
	public void jump() {
		if(!isJumping()) {
			sounds.playSound("jump");
			float x = body.getWorldCenter().x;
			float y = body.getWorldCenter().y;
			body.applyLinearImpulse(0, JUMP_IMPULSE, x, y, true);
		}
	}
	
	public void stopMove() {
		body.setLinearVelocity(0, body.getLinearVelocity().y);
	}
	
	public void stopJump() {
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(body.getLinearVelocity().x, 0.02f);
		}
	}
	
	public void shoot() {
		sounds.playSound("shoot");
		PlayerShot shot = new PlayerShot(this);
		shot.shoot();
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
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation("player.png", 1, 4, 0.09f, true);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
		
		animation.play();
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
		
		float vx = Player.MOVE_SPEED;
		if(moveDirection == Direction.Left) {
			vx = 0 - vx;
		}
		
		body.setLinearVelocity(vx, body.getLinearVelocity().y);
	}
}
