package entity;

import misc.EntityBodyDef;

import com.badlogic.gdx.Gdx;
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
		Left, Right, Up, Down
	}
	
	protected Sprite directionSprite;
	protected Direction direction = null;
	
	protected ProgrammableMovementEntity(Room room, String textureKey, EntityBodyDef bodyDef) {
        super(room, textureKey, bodyDef);
        
        directionSprite = new Sprite(textures.getTextureRegion("red"));
        directionSprite.setSize(getWidth() / 4, getHeight());
		directionSprite.setFlip(false, true);
    }
        
    public static Entity build(String id, Room room, Vector2 pos, Element elem) {
        float width = Room.SQUARE_SIZE * 2;
        float height = Room.SQUARE_SIZE;
        EntityBodyDef bodyDef = new EntityBodyDef(pos, new Vector2(width, height), BodyType.KinematicBody);
        
        ProgrammableMovementEntity entity = new ProgrammableMovementEntity(room, "gray_block", bodyDef);
        entity.setId(id);
        entity.setBodyData();
        
        return entity;
    }
    
    @Override
    public void draw(SpriteBatch batch) {
    	super.draw(batch);
    	
    	if(direction != null) {
    		directionSprite.draw(batch);
    	}  	
    }
    
    @Override
    public boolean update() {
    	if(isPlayerInContact()) {
    		onBeginContact(theGrid.getPlayer());
    	}
    	
    	if(direction == null) {
    		return false;
    	}
    	
    	float top = getTop();
		float left = getLeft();
		float width = getWidth();
    	
    	switch(direction) {
			case Left:
				directionSprite.setPosition(left, top);
				break;
			case Down:
				directionSprite.setPosition(left + (width / 4), top);
				break;
			case Up:
				directionSprite.setPosition(left + (width / 2), top);
				break;
			case Right:
				directionSprite.setPosition(left + ((3.0f / 4.0f) * width), top);
				break;
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
			
			deactivateOthers();
		}
	}
    
    public void onMovementTrigger() {
    	if(direction == null) {
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
		    	
		    	switch(direction) {
					case Left:
						if(currCol - 1 >= roomLeftCol) {
							setPosition(getLeft() - (getWidth() / 2), getTop());
						}
						break;
					case Down:
						if(currRow + 1 <= roomBottomRow) {
							setPosition(getLeft(), getTop() + getHeight());
						}
						break;
					case Up:
						if(currRow - 1 >= roomTopRow) {
							setPosition(getLeft(), getTop() - getHeight());
						}
						break;
					case Right:
						if(currCol + 1 < roomRightCol) {
							setPosition(getLeft() + (getWidth() / 2), getTop());
						}
						break;
		    	}
			}  		
    	});
    }
    
    public boolean isActivated() {
    	return direction != null;
    }
    
    public void deactivate() {
    	direction = null;
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
}
