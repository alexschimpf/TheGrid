package entity.special;

import misc.EntityBodyDef;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import core.Room;
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
		float x = player.getFrontX() - SIZE;
		float y = player.getCenterY() - (SIZE / 2);
		
		if(player.getDirection() == Direction.Right) {
			x += SIZE;
		}
		
		return new EntityBodyDef(new Vector2(x, y), new Vector2(SIZE, SIZE), BodyType.DynamicBody);
	}
	
	public static EntityBodyDef getEntityBodyDef(Vector2 pos) {
		return new EntityBodyDef(pos, new Vector2(SIZE, SIZE), BodyType.DynamicBody);
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
				
		setLinearVelocity(vx, vy);
	}
	
	public void shoot(float vx, float vy) {
		setBodyData();
		
		theGrid.addGlobalEntity(this);
		
		setLinearVelocity(vx, vy);
	}
}
