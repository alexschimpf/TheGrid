package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import script.Script;
import misc.BodyData;
import misc.EntityBodyDef;
import misc.Globals;
import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

import entity.Entity;
import entity.RectangleEntity;

public class Room implements IUpdate, IDraw {

	public static final float SQUARE_SIZE = Globals.VIEWPORT_WIDTH / 16;
	protected static final float BORDER_LINE_THICKNESS = SQUARE_SIZE / 32;
	protected static final float BORDER_LINE_LENGTH = SQUARE_SIZE;
	
	protected Vector2 gridPos;
	protected HashMap<String, Script> scriptMap = new HashMap<String, Script>();
	protected HashMap<String, Entity> entityMap = new HashMap<String, Entity>();
	protected Array<Vector2> openings = new Array<Vector2>();
	protected Array<Sprite> borderSprites = new Array<Sprite>();
	protected Globals globals = Globals.getInstance();
	protected TheGrid theGrid = globals.getTheGrid();
	protected World world = theGrid.getWorld();	
	
	public Room(Vector2 gridPos) {
		this.gridPos = gridPos;
	}
	
	public static Vector2 getGridPosition(float x, float y) {
		int row = MathUtils.floor(y / SQUARE_SIZE);
		int col = MathUtils.floor(x / SQUARE_SIZE);
		
		return new Vector2(row, col);
	}
	
	public static Vector2 getGridPosition(Room room, int row, int col) {
		Vector2 gridPos = room.getGridPosition();
		int topRow = (int)gridPos.x * TheGrid.ROOM_NUM_SQUARES_WIDE;
		int leftCol = (int)gridPos.y * TheGrid.ROOM_NUM_SQUARES_WIDE;			
		return new Vector2(topRow + row, leftCol + col);
	}
	
	public static Vector2 getWorldPosition(int row, int col) {
		float x = col * SQUARE_SIZE;
		float y = row * SQUARE_SIZE;
		
		return new Vector2(x, y);
	}
	
	public static Vector2 getWorldPosition(Room room, int row, int col) {
		Vector2 gridPos = room.getGridPosition();
		int topRow = (int)gridPos.x * TheGrid.ROOM_NUM_SQUARES_WIDE;
		int leftCol = (int)gridPos.y * TheGrid.ROOM_NUM_SQUARES_WIDE;			
		return getWorldPosition(topRow + row, leftCol + col);
	}
	
	public static Vector2 getWorldPosition(int gridRow, int gridCol, int row, int col) {
		Vector2 gridPos = new Vector2(gridRow, gridCol);
		int topRow = (int)gridPos.x * TheGrid.ROOM_NUM_SQUARES_WIDE;
		int leftCol = (int)gridPos.y * TheGrid.ROOM_NUM_SQUARES_WIDE;			
		return getWorldPosition(topRow + row, leftCol + col);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		for(Entity entity : entityMap.values()) {
			entity.draw(batch);
		}
		
		for(Sprite sprite : borderSprites) {
			sprite.draw(batch);
		}
	}

	@Override
	public boolean update() {
		updateEntities();
		updateScripts();
		
		return theGrid.getPlayer().getRoom() != this;
	}

	@Override
	public void done() {
	}
	
	public void createBorderSprites() {
		Vector2 gridPos = getGridPosition();
		
		boolean edge = gridPos.x == 0 || gridPos.x == theGrid.getNumRows() - 1 ||
				       gridPos.y == 0 || gridPos.y == theGrid.getNumCols() - 1;
		
		// Left side
		for(int i = 1; i < TheGrid.ROOM_NUM_SQUARES_WIDE - 1; i++) {
			Vector2 pos = new Vector2(i, 0);
			String textureKey = edge && gridPos.y == 0 ? "gev1" : "glv1";
			
			createBorderSprite(textureKey, pos);
		}
		
		// Right side
		for(int i = 1; i < TheGrid.ROOM_NUM_SQUARES_WIDE - 1; i++) {
			Vector2 pos = new Vector2(i, TheGrid.ROOM_NUM_SQUARES_WIDE - 1);
			String textureKey = edge && gridPos.y == theGrid.getNumCols() - 1 ? "gev1" : "grv1";
			createBorderSprite(textureKey, pos);
		}
		
		// Top side
		for(int i = 1; i < TheGrid.ROOM_NUM_SQUARES_WIDE - 1; i++) {
			Vector2 pos = new Vector2(0, i);
			String textureKey = edge && gridPos.x == 0 ? "geh1" : "gth1";
			createBorderSprite(textureKey, pos);
		}
		
		// Bottom side
		for(int i = 1; i < TheGrid.ROOM_NUM_SQUARES_WIDE - 1; i++) {
			Vector2 pos = new Vector2(TheGrid.ROOM_NUM_SQUARES_WIDE - 1, i);
			String textureKey = edge && gridPos.x == theGrid.getNumRows() - 1 ? "geh1" : "gbh1";
			createBorderSprite(textureKey, pos);
		}
		
		createBorderCorners(gridPos);
	}
	
	public boolean entityExistsInArea(Vector2 topLeft, float width, float height, Entity ignoreEntity) {
		LinkedList<Entity> entities = new LinkedList<Entity>();
		entities.addAll(getEntities());
		entities.add(theGrid.getPlayer());
		for(Entity entity : entities) {
			if(ignoreEntity != null && entity.equals(ignoreEntity)) {
				continue;
			}
			
			Rectangle checkRect = new Rectangle(topLeft.x, topLeft.y, width, height);
			Rectangle entityRect = new Rectangle(entity.getLeft(), entity.getTop(), entity.getWidth(), entity.getHeight());
			if(checkRect.overlaps(entityRect)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void setAwake(boolean awake) {
		for(Entity entity : entityMap.values()) {
			if(entity.getBody() != null) {
				entity.getBody().setAwake(awake);
			}
		}
	}
	
	public Array<Vector2> getOpenings() {
		return openings;
	}
	
	public void setOpenings(Array<Vector2> openings) {
		this.openings = openings;
	}

	public void addScript(Script script) {
		scriptMap.put(script.getId(), script);
	}
	
	public void removeScript(Script script) {
		scriptMap.remove(script.getId());
	}
	
	public void addEntity(Entity entity) {
		entityMap.put(entity.getId(), entity);
	}
	
	public void removeEntity(Entity entity) {
		entityMap.remove(entity.getId());
	}
	
	public Script getScriptById(String id) {
		return scriptMap.get(id);
	}
	
	public Entity getEntityById(String id) {
		return entityMap.get(id);
	}
	
	public Collection<Script> getScripts() {
		return scriptMap.values();
	}
	
	public Collection<Entity> getEntities() {
		return entityMap.values();
	}

	public Vector2 getGridPosition() {
		return gridPos;
	}
	
	public Vector2 getWorldPosition() {
		return TheGrid.getRoomWorldPosition((int)gridPos.x, (int)gridPos.y);
	}
	
	public float getLeft() {
		return getWorldPosition().x;
	}
	
	public float getRight() {
		return getLeft() + TheGrid.ROOM_SIZE;
	}
	
	public float getTop() {
		return getWorldPosition().y;
	}
	
	public float getBottom() {
		return getTop() + TheGrid.ROOM_SIZE;
	}
	
	public int getLeftCol(boolean includeBorder) {
		int leftCol = (int)(gridPos.y) * TheGrid.ROOM_NUM_SQUARES_WIDE;
		return includeBorder ? leftCol : leftCol + 1;
	}
	
	public int getRightCol(boolean includeBorder) {
		int rightCol = ((int)(gridPos.y) * TheGrid.ROOM_NUM_SQUARES_WIDE) + TheGrid.ROOM_NUM_SQUARES_WIDE - 1;
		return includeBorder ? rightCol : rightCol - 1;
	}
	
	public int getTopRow(boolean includeBorder) {
		int topRow = ((int)(gridPos.x) * TheGrid.ROOM_NUM_SQUARES_WIDE);
		return includeBorder ? topRow : topRow + 1;
	}
	
	public int getBottomRow(boolean includeBorder) {
		int bottomRow = ((int)(gridPos.x) * TheGrid.ROOM_NUM_SQUARES_WIDE) + TheGrid.ROOM_NUM_SQUARES_WIDE - 1;
		return includeBorder ? bottomRow : bottomRow - 1;
	}
	
	protected void updateEntities() {
		Iterator<Entity> entitiesItr = entityMap.values().iterator();
		while(entitiesItr.hasNext()) {
			Entity entity = entitiesItr.next();
			if(entity == null) {
				continue;
			}

			BodyData bodyData = entity.getBodyData();
			if(entity.update() || (bodyData != null && bodyData.needsRemoved())) {
				entity.done();
				entitiesItr.remove();
			}
		}
	}
	
	protected void updateScripts() {
		Iterator<Script> scriptsItr = scriptMap.values().iterator();
		while(scriptsItr.hasNext()) {
			Script script = scriptsItr.next();
			if(script == null) {
				continue;
			}

			if(script.update()) {
				script.done();
				scriptsItr.remove();
			}
		}
	}

	protected void createBorderCorners(Vector2 gridPos) {
		Vector2 topLeftPos = new Vector2(0, 0);
		boolean globalTopLeft = gridPos.x == 0 && gridPos.y == 0;
		String topLeftTextureKey = globalTopLeft ? "gtl" : "gc1";
		if(!globalTopLeft) {
			if(gridPos.x == 0) {
				topLeftTextureKey = "gbh1";
			} else if(gridPos.y == 0) {
				topLeftTextureKey = "grv1";
			}
			
		}
		createBorderSprite(topLeftTextureKey, topLeftPos);
		
		Vector2 topRightPos = new Vector2(0, TheGrid.ROOM_NUM_SQUARES_WIDE - 1);
		boolean globalTopRight = gridPos.x == 0 && gridPos.y == theGrid.getNumCols() - 1;
		String topRightTextureKey = globalTopRight ? "gtr" : "gc1";
		if(!globalTopRight) {
			if(gridPos.x == 0) {
				topRightTextureKey = "gbh1";
			} else if(gridPos.y == theGrid.getNumCols() - 1) {
				topRightTextureKey = "glv1";
			}
		}
		createBorderSprite(topRightTextureKey, topRightPos);
			
		Vector2 bottomLeftPos = new Vector2(TheGrid.ROOM_NUM_SQUARES_WIDE - 1, 0);
		boolean globalBottomLeft = gridPos.x == theGrid.getNumRows() - 1 && gridPos.y == 0;
		String bottomLeftTextureKey = globalBottomLeft ? "gbl" : "gc1";
		if(!globalBottomLeft) {
			if(gridPos.y == 0) {
				bottomLeftTextureKey = "grv1";
			} else if(gridPos.x == theGrid.getNumRows() - 1) {
				bottomLeftTextureKey = "gth1";
			}
		}
		createBorderSprite(bottomLeftTextureKey, bottomLeftPos);	
		
		Vector2 bottomRightPos = new Vector2(TheGrid.ROOM_NUM_SQUARES_WIDE - 1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1);
		boolean globalBottomRight = gridPos.x == theGrid.getNumRows() - 1 && gridPos.y == theGrid.getNumCols() - 1;
		String bottomRightTextureKey = globalBottomRight ? "gbr" : "gc1";
		if(!globalBottomRight) {
			if(gridPos.x == theGrid.getNumRows() - 1) {
				bottomRightTextureKey = "gth1";
			} else if(gridPos.y == theGrid.getNumCols() - 1) {
				bottomRightTextureKey = "glv1";
			}
		}
		createBorderSprite(bottomRightTextureKey, bottomRightPos);
	}
	
	protected void createBorderSprite(String textureKey, Vector2 pos) {
		if(openings.contains(pos, false)) {
			return;
		}
		
		Vector2 roomGridPos = getGridPosition();
		Vector2 leftPos = Room.getWorldPosition(this, (int)pos.x, (int)pos.y - 1);
		Vector2 rightPos = Room.getWorldPosition(this, (int)pos.x, (int)pos.y + 1);
		Vector2 abovePos = Room.getWorldPosition(this, (int)pos.x - 1, (int)pos.y);
		Vector2 belowPos = Room.getWorldPosition(this, (int)pos.x + 1, (int)pos.y);
		if(theGrid.isOpeningAt(belowPos)) {
			textureKey = pos.y == 0 ? "gbr" : "gbl";
			if(pos.x == 0) {
				textureKey = roomGridPos.x == 0 ? "geh1" : "gth1";
			}
		} else if(theGrid.isOpeningAt(abovePos)) {
			textureKey = pos.y == 0 ? "gtr" : "gtl";
			if(pos.x == TheGrid.ROOM_NUM_SQUARES_WIDE - 1) {
				textureKey = roomGridPos.x == theGrid.getNumRows() - 1 ? "geh1" : "gbh1";
			}
		} else if(theGrid.isOpeningAt(leftPos)) {
			textureKey = pos.x == 0 ? "gbl" : "gtl";
			if(pos.y == TheGrid.ROOM_NUM_SQUARES_WIDE - 1) {
				textureKey = roomGridPos.y == theGrid.getNumCols() - 1 ? "gev1" : "grv1";
			}
		} else if(theGrid.isOpeningAt(rightPos)) {
			textureKey = pos.x == 0 ? "gbr" : "gtr";
			if(pos.y == 0) {
				textureKey = roomGridPos.y == 0 ? "gev1" : "glv1";
			}
		}

		Vector2 worldPos = Room.getWorldPosition(this, (int)pos.x, (int)pos.y);
		Sprite sprite = Textures.getInstance().getSprite(textureKey);
		sprite.setPosition(worldPos.x, worldPos.y);
		sprite.setSize(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
		borderSprites.add(sprite);
	}
}
