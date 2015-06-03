package entity;

import misc.Animation;
import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class CircleCreatorEntity extends MovingRectangleEntity {

	public static final String TYPE = "circle_creator";
	
	private Animation animation;
	
	protected CircleCreatorEntity(Room room, String textureKey, EntityBodyDef bodyDef, Vector2 startPos, Vector2 endPos,
			                      Vector2 startVelocity, Vector2 endVelocity) {
		super(room, textureKey, bodyDef, startPos, endPos, startVelocity, endVelocity);
	}

	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
		
		float width = custom.getFloat("width_scale", 1) * Room.SQUARE_SIZE;
		float height = custom.getFloat("height_scale", 1) * Room.SQUARE_SIZE;
		EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);
					
		Vector2 startPos = extractPos(room, custom, "start_pos");
		Vector2 endPos = extractPos(room, custom, "end_pos");
		Vector2 startVelocity = extractVelocity(custom, "start_velocity");
		Vector2 endVelocity = extractVelocity(custom, "end_velocity");
		
		CircleCreatorEntity entity = new CircleCreatorEntity(room, "question_mark_block", bodyDef, startPos, endPos, startVelocity, endVelocity);
		entity.setId(id);
		entity.setBodyData();
		entity.getBody().setLinearVelocity(startVelocity);
		
		return entity;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public boolean update() {
		animation.update();
		sprite = animation.getSprite();
		
		return super.update();
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if(isShot(entity)) {
			animation.play();
			if(!circleExists()) {
				createRandomCircle();
			}
		}
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		animation = new Animation("question_mark_block", 0.36f, false, false);
		sprite = animation.getSprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
	}
	
	protected void createRandomCircle() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Vector2 pos = getRandomPos();
				Vector2 size = new Vector2(Room.SQUARE_SIZE * 0.75f, Room.SQUARE_SIZE * 0.75f);
				EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.DynamicBody);
				CircleEntity circle = new CircleEntity(room, "ball", bodyDef);
				circle.setId(null);
				circle.setBodyData();
				circle.setRemoveOnContact(true);
				circle.getBody().setGravityScale(0);
				room.addEntity(circle);
			}			
		});
	}
	
	protected Vector2 getRandomPos() {
		// TODO: This is too room-specific.
		int leftCol = room.getLeftCol(false) + 2;
		int rightCol = room.getRightCol(false) - 2;
		int topRow = room.getTopRow(false) + 1;
		int bottomRow = room.getBottomRow(false) - 1;
		int row = MathUtils.random(topRow, bottomRow);
		int col = MathUtils.random(leftCol, rightCol);
		if(col == 5) {
			col++;
		}
		return Room.getWorldPosition(row, col);
	}
	
	protected boolean circleExists() {
		for(Entity entity : room.getEntities()) {
			if(entity instanceof CircleEntity) {
				return true;
			}
		}
		
		return false;
	}
}
