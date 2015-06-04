package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.special.Player;

public class DropPlayerEntity extends RectangleEntity {

	public static final String TYPE = "player_drop";
	
	protected DropPlayerEntity(Room room, float x, float y) {
		super(room, "up_arrow_block", DropPlayerEntity.getEntityBodyDef(x, y));
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		DropPlayerEntity entity = new DropPlayerEntity(room, pos.x, pos.y);
		entity.setId(id);
		entity.setBodyData();
		
		return entity;
	}
	
	public static EntityBodyDef getEntityBodyDef(float x, float y) {
		return new EntityBodyDef(new Vector2(x, y), new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE), BodyType.StaticBody);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public boolean hasRandomColor() {
		return true;
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		if(isShot(entity)) {
			SOUNDS.playSound("transport");
			Gdx.app.postRunnable(new Runnable() {
		        @Override
		        public void run() {
		            float roomTop = room.getTop();
		     		Player player = GLOBALS.getPlayer();
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
}
