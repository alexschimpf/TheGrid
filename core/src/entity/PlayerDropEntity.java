package entity;

import misc.EntityBodyDef;
import misc.Globals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.special.Player;

public class PlayerDropEntity extends RectangleEntity {

	protected PlayerDropEntity(Room room, float x, float y) {
		super(room, "player_drop", PlayerDropEntity.getEntityBodyDef(x, y));
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
	public void onBeginContact(Entity entity) {
		if(isShot(entity)) {
			sounds.playSound("drop_player_trigger");
			Gdx.app.postRunnable(new Runnable() {
		        @Override
		        public void run() {
		            float roomTop = room.getTop();
		     		Player player = Globals.getInstance().getTheGrid().getPlayer();
		     		float newTop = roomTop + Room.SQUARE_SIZE;
		     		
		     		Vector2 playerVelocity = player.getBody().getLinearVelocity();
		     		player.setPosition(player.getLeft(), newTop);
		     		player.getBody().setLinearVelocity(playerVelocity.x, playerVelocity.y + 0.01f);
		         }
		     });
		}
	}
}
