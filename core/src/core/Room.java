package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import script.Script;
import misc.BodyData;
import misc.Globals;
import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import entity.Entity;

public class Room implements IUpdate, IDraw {

	public static final float SQUARE_SIZE = Globals.VIEWPORT_WIDTH / 16;
	protected static final float BORDER_LINE_THICKNESS = SQUARE_SIZE / 20;
	
	protected Vector2 gridPos;
	protected HashMap<String, Script> scriptMap = new HashMap<String, Script>();
	protected HashMap<String, Entity> entityMap = new HashMap<String, Entity>();
	protected Array<Sprite> borderLines = new Array<Sprite>();
	protected Array<Vector2> openings = new Array<Vector2>(); // these are NOT in local coords
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
		
		drawBorderLines(batch);
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
	
	public void createBorderLines() {
		createHorLines(0, TheGrid.ROOM_NUM_SQUARES_WIDE, 0);
		createHorLines(1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1, 1);
		createHorLines(0, TheGrid.ROOM_NUM_SQUARES_WIDE, TheGrid.ROOM_NUM_SQUARES_WIDE);
		createHorLines(1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1);
		createVertLines(0, TheGrid.ROOM_NUM_SQUARES_WIDE, 0);
		createVertLines(1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1, 1);
		createVertLines(0, TheGrid.ROOM_NUM_SQUARES_WIDE, TheGrid.ROOM_NUM_SQUARES_WIDE);
		createVertLines(1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1, TheGrid.ROOM_NUM_SQUARES_WIDE - 1);
		createOpeningLines();
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
	
	protected void createHorLines(int startCol, int endCol, int row) {
		TextureRegion horLine = Textures.getInstance().getTextureRegion("hor_line");
		for(int col = startCol; col < endCol; col++) {
			Vector2 currSquarePos = Room.getGridPosition(this, row, col);
			Vector2 upCurrSquarePos = Room.getGridPosition(this, row - 1, col);
			if(!theGrid.getRoomOpenings().contains(currSquarePos, false) && 
			   !theGrid.getRoomOpenings().contains(upCurrSquarePos, false)) {
				Vector2 pos = Room.getWorldPosition(this, row, col);
				addBorderLine(horLine, pos.x, pos.y, Room.SQUARE_SIZE, Room.BORDER_LINE_THICKNESS);
			}
		}
	}
	
	protected void createVertLines(int startRow, int endRow, int col) {
		TextureRegion vertLine = Textures.getInstance().getTextureRegion("vert_line");
		for(int row = startRow; row < endRow; row++) {
			Vector2 currSquarePos = Room.getGridPosition(this, row, col);
			Vector2 leftCurrSquarePos = Room.getGridPosition(this, row, col - 1);
			if(!theGrid.getRoomOpenings().contains(currSquarePos, false) &&
			   !theGrid.getRoomOpenings().contains(leftCurrSquarePos, false)) {
    			Vector2 pos = Room.getWorldPosition(this, row, col);
    			pos.x -= 0.15f;
    			addBorderLine(vertLine, pos.x, pos.y, Room.BORDER_LINE_THICKNESS, Room.SQUARE_SIZE);
			}
		}
	}
	
	protected void createOpeningLines() {
		Array<Vector2> allOpenings = theGrid.getRoomOpenings();
		for(Vector2 opening : openings) {
			int row = (int)opening.x;
			int col = (int)opening.y;
			Vector2 below = new Vector2(row + 1, col);
			Vector2 above = new Vector2(row - 1, col);
			boolean vert = allOpenings.contains(below, false) || allOpenings.contains(above, false);
			if(vert) {
				createOpeningVertLines(row, col);
			} else {
				createOpeningHorLines(row, col);
			}
			
		}
	}
	
	protected void createOpeningVertLines(int row, int col) {
		TextureRegion vertLine = Textures.getInstance().getTextureRegion("vert_line");
		
		Vector2 pos = Room.getWorldPosition(row, col);
		pos.x -= 0.15f;
		pos.y += 0.15f;
		addBorderLine(vertLine, pos.x, pos.y, Room.BORDER_LINE_THICKNESS, Room.SQUARE_SIZE);
		
		pos = Room.getWorldPosition(row, col + 1);
		pos.x -= 0.05f;
		pos.y += 0.15f;
		addBorderLine(vertLine, pos.x, pos.y, Room.BORDER_LINE_THICKNESS, Room.SQUARE_SIZE);
	}
	
	protected void createOpeningHorLines(int row, int col) {
		TextureRegion horLine = Textures.getInstance().getTextureRegion("hor_line");
		
		Vector2 pos = Room.getWorldPosition(row, col);
		addBorderLine(horLine, pos.x, pos.y, Room.SQUARE_SIZE, Room.BORDER_LINE_THICKNESS);

		pos = Room.getWorldPosition(row + 1, col);
		addBorderLine(horLine, pos.x, pos.y, Room.SQUARE_SIZE, Room.BORDER_LINE_THICKNESS);
		
	}
	
	protected void addBorderLine(TextureRegion texture, float x, float y, float width, float height) {
		Sprite sprite = new Sprite(texture);
		sprite.setSize(width, height);
		sprite.setPosition(x, y);		
		borderLines.add(sprite);
	}
	
	protected void drawBorderLines(SpriteBatch batch) {
		for(Sprite sprite : borderLines) {
			sprite.draw(batch);
		}
	}
}
