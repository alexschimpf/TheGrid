package core;

import java.util.Iterator;

import listener.CollisionListener;
import misc.BodyData;
import misc.Globals;
import misc.IDraw;
import misc.IUpdate;
import misc.GridBuilder;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import entity.Entity;
import entity.special.Player;

public final class TheGrid implements IUpdate, IDraw {

	public static float DEFAULT_GRAVITY = 18;
	public static int ROOM_NUM_SQUARES_WIDE = 12;
	public static float ROOM_SIZE = Room.SQUARE_SIZE * ROOM_NUM_SQUARES_WIDE;
	protected static final Globals GLOBALS = Globals.getInstance();
	
	private Player player;
	private World world;
	private Room[][] grid;	
	private Array<Entity> globalEntities = new Array<Entity>();
	
	public TheGrid() {
		world = new World(new Vector2(0, DEFAULT_GRAVITY), true);
		world.setContactListener(new CollisionListener());
	}
	
	public static Vector2 getRoomGridPosition(float x, float y) {
		int row = Math.max(0, MathUtils.floor(y / ROOM_SIZE));
		int col = Math.max(0, MathUtils.floor(x / ROOM_SIZE));
		
		return new Vector2(row, col);
	}
	
	public static Vector2 getRoomWorldPosition(int row, int col) {
		float x = col * ROOM_SIZE;
		float y = row * ROOM_SIZE;
		
		return new Vector2(x, y);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		for(Entity entity : globalEntities) {
			if(entity.isVisible()) {
				entity.draw(batch);
			}
		}
		
		for(int row = 0; row < getNumRows(); row++) {
			for(int col = 0; col < getNumCols(); col++) {
				Room room = getRoomAt(row, col);
				if(room != null) {
					room.draw(batch);
				}
			}
		}
		
		player.draw(batch);
	}

	@Override
	public boolean update() {
		world.step(1 / 45.0f, 5, 5);

		// This was above updateGlobals.
		player.update();
		
		updateGlobals();

		for(Room room : getRooms()) {
			if(room != null && room.update()) {
				room.done();
			}
		}
		
		return false;
	}

	@Override
	public void done() {
		
	}
	
	public void build() {
		GridBuilder builder = new GridBuilder(this);
		builder.buildFromFile("the_grid.xml");
		
		for(Room room : getRooms()) {
			room.createBorderSprites();
		}
	}
	
	public void initGrid(int numRows, int numCols) {
		grid = new Room[numRows][numCols];
	}
	
	public void initPlayer(int gridRow, int gridCol, int row, int col) {
		Vector2 worldPos = Room.getWorldPosition(gridRow, gridCol, row, col);
		player = new Player(worldPos);
		player.setBodyData();
	}
	
	public void addGlobalEntity(Entity entity) {
		globalEntities.add(entity);
	}
	
	public void addRoom(Room room) {
		Vector2 roomPos = room.getGridPosition();
		grid[(int)roomPos.x][(int)roomPos.y] = room;
	}
	
	public Array<Room> getRooms() {
		Array<Room> rooms = new Array<Room>();
		for(int row = 0; row < getNumRows(); row++) {
			for(int col = 0; col < getNumCols(); col++) {
				Room room = getRoomAt(row, col);
				rooms.add(room);
			}
		}
		
		return rooms;
	}
	
	public Room getRoomAt(float x, float y) {
		Vector2 gridPosition = TheGrid.getRoomGridPosition(x, y);
		return getRoomAt((int)gridPosition.x, (int)gridPosition.y);
	}
	
	public Room getRoomAt(int row, int col) {
		return grid[row][col];
	}
	
	public int getNumRows() {
		return grid.length;
	}
	
	public int getNumCols() {
		return grid[0].length;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isEntityAt(Vector2 worldPos) {
		for(Room room : getRooms()) {
			for(Entity entity : room.getEntities()) {
				if(worldPos.equals(entity.getLeftTop())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isOpeningAt(Vector2 worldPos) {
		for(Room room : getRooms()) {
			Array<Vector2> openings = room.getOpenings();
			for(Vector2 openingGridPos : openings) {
				Vector2 currWorldPos = Room.getWorldPosition(room, (int)openingGridPos.x, (int)openingGridPos.y);
				if(worldPos.equals(currWorldPos)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void updateGlobals() {
		Iterator<Entity> globalEntitiesItr = globalEntities.iterator();
		while(globalEntitiesItr.hasNext()) {
			Entity entity = globalEntitiesItr.next();
			if(entity == null || entity.getBodyData() == null) {
				continue;
			}
			
			BodyData bodyData = entity.getBodyData();
			if(entity.update() || bodyData.needsRemoved()) {
				entity.done();
			    globalEntitiesItr.remove();
			}
		}
	}
}
