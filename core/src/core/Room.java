package core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import script.Script;
import misc.BodyData;
import misc.Globals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import entity.Entity;

public class Room implements IUpdate, IDraw {

	public static final float SQUARE_SIZE = Globals.VIEWPORT_WIDTH / 16;
	
	protected Vector2 gridPos;
	protected HashMap<String, Script> scriptMap = new HashMap<String, Script>();
	protected HashMap<String, Entity> entityMap = new HashMap<String, Entity>();
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
	
	@Override
	public void draw(SpriteBatch batch) {
		for(Entity entity : entityMap.values()) {
			entity.draw(batch);
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
	
	public void setAwake(boolean awake) {
		for(Entity entity : entityMap.values()) {
			if(entity.getBody() != null) {
				entity.getBody().setAwake(awake);
			}
		}
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
}
