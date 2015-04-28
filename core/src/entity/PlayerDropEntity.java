package entity;

import misc.Animation;
import misc.EntityBodyDef;
import misc.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.special.Player;

public class PlayerDropEntity extends RectangleEntity {

	private Animation animation;
	
	protected PlayerDropEntity(Room room, float x, float y) {
		super(room, "up_arrow_block", PlayerDropEntity.getEntityBodyDef(x, y));
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		PlayerDropEntity entity = new PlayerDropEntity(room, pos.x, pos.y);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	public static EntityBodyDef getEntityBodyDef(float x, float y) {
		return new EntityBodyDef(new Vector2(x, y), new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE), BodyType.StaticBody);
	}
	
	@Override
	public String getType() {
		return "player_drop";
	}
	
	@Override
	public boolean update() {
		animation.update();
		sprite = animation.getSprite();
		
		return super.update();
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		if(isShot(entity)) {
			sounds.playSound("transport");
			animation.play();
			Gdx.app.postRunnable(new Runnable() {
		        @Override
		        public void run() {
		            float roomTop = room.getTop();
		     		Player player = Globals.getInstance().getTheGrid().getPlayer();
		     		float newTop = roomTop + Room.SQUARE_SIZE;
		     		Vector2 worldPos = new Vector2(player.getLeft(), newTop);
		     		
		     		while(room.entityExistsInArea(worldPos, player.getWidth() * 0.75f, player.getHeight())) {
		     			worldPos.y += Room.SQUARE_SIZE;
		     		}
		     		
		     		Vector2 playerVelocity = player.getBody().getLinearVelocity();
		     		player.setPosition(worldPos.x, worldPos.y);
		     		player.getBody().setLinearVelocity(playerVelocity.x, playerVelocity.y + 0.01f);
		         }
		     });
		}
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation("up_arrow_block.png", 1, 5, 0.075f, false);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
	}
}
