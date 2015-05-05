package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.special.Player;

public class ProgrammableMovementEntity extends RectangleEntity {

	protected enum Direction {
		Left, Right, Up, Down, None
	}
	
	protected Direction direction = Direction.None;
	
	protected ProgrammableMovementEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
        super(room, textureKey, bodyDef);
    }
        
    public static Entity build(String id, Room room, Vector2 pos, Element elem) {
        float width = Room.SQUARE_SIZE * 2;
        float height = Room.SQUARE_SIZE;
        EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);
        
        ProgrammableMovementEntity entity = new ProgrammableMovementEntity(room, "programmable_block", bodyDef);
        entity.setId(id);
        entity.setBodyData();
        
        return entity;
    }

    @Override
    public boolean update() {
    	if(isPlayerInContact()) {
    		onBeginContact(theGrid.getPlayer());
    	}

    	return super.update();
    }
    
    @Override
    public String getType() {
    	return "programmable_movement";
    }
    
    @Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if(isShot(entity) || isPlayer(entity)) {		
			float entityx = entity.getCenterX();
			float left = getLeft();
			float width = getWidth();
			if(entityx < left + (width / 4)) {
				direction = Direction.Left;
			} else if(entityx < left + (width / 2)) {
				direction = Direction.Down;
			} else if(entityx < left + ((3.0f / 4.0f) * width)) {
				direction = Direction.Up;
			} else {
				direction = Direction.Right;
			}
			
			changeSprite();			
			deactivateOthers();
		}
	}
    
    public void onMovementTrigger() {
    	if(direction == Direction.None) {
    		return;
    	}
    	
    	Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				Vector2 gridPos = Room.getGridPosition(getLeft(), getTop());
		    	int currRow = (int)gridPos.x;
		    	int currCol = (int)gridPos.y;
		    	int roomBottomRow = room.getBottomRow(false);
		    	int roomTopRow = room.getTopRow(false);
		    	int roomLeftCol = room.getLeftCol(false);
		    	int roomRightCol = room.getRightCol(false);
		    	
		    	Vector2 leftTop = getLeftTop();
		    	switch(direction) {
					case Left:
						if(currCol - 1 >= roomLeftCol) {
							leftTop.x -= (getWidth() / 2);
						}
						break;
					case Down:
						if(currRow + 1 <= roomBottomRow) {
							leftTop.y += getHeight();
						}
						break;
					case Up:
						if(currRow - 1 >= roomTopRow) {
							leftTop.y -= getHeight();
						}
						break;
					case Right:
						if(currCol + 1 < roomRightCol) {
							leftTop.x += (getWidth() / 2);
						}
						break;
					default:
						break;
		    	}
		    	
		    	if(!room.entityExistsInArea(leftTop, getWidth(), getHeight(), ProgrammableMovementEntity.this)) {
					setPosition(leftTop.x, leftTop.y);
				}
			}  		
    	});
    }
    
    public boolean isActivated() {
    	return direction != Direction.None;
    }
    
    public void deactivate() {
    	direction = Direction.None;
    	changeSprite();
    }
    
    protected void deactivateOthers() {
    	for(Object entity : room.getEntities()) {
			if(entity instanceof ProgrammableMovementEntity) {
				ProgrammableMovementEntity other = (ProgrammableMovementEntity)entity;
				if(other != this) {
					other.deactivate();
				}
			}
		}
    }
    
    protected boolean isPlayerInContact() {
    	Player player = theGrid.getPlayer();
    	Fixture fixture = getBody().getFixtureList().get(0);
    	return fixture.testPoint(player.getCenterX(), player.getBottom());
    }
    
    protected void changeSprite() {
    	int num = 0;
    	switch(direction) {
    		case Left:
				num = 1;
				break;
			case Down:
				num = 2;
				break;
			case Up:
				num = 3;
				break;
			case Right:
				num = 4;
				break;
			case None:
				break;
    	}
    	
    	String key = "programmable_block";
    	if(num > 0) {
    		key += "_" + num;
    	}
    	sprite.setRegion(textures.getTextureRegion(key));
    }
}
