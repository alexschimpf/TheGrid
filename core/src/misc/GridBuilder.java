package misc;

import java.lang.reflect.Method;
import java.util.HashMap;

import script.Script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import core.TheGrid;
import entity.Entity;
import entity.RectangleEntity;

public final class GridBuilder {

	private TheGrid theGrid;
	private HashMap<String, String> entityTypeClassMap;
	private HashMap<String, String> scriptTypeClassMap;
	private Element root;
	
	public GridBuilder(TheGrid theGrid) {
		this.theGrid = theGrid;
		
		entityTypeClassMap = GlobalIdMapper.getEntityTypeClassMap();
		scriptTypeClassMap = GlobalIdMapper.getScriptTypeClassMap();
	}
	
	public void buildFromFile(String filename) {
		try {
			XmlReader reader = new XmlReader();
			root = reader.parse(Gdx.files.internal(filename));
			
			Element sizeElem = root.getChildByName("size");
			initGrid(sizeElem);
			
			Element roomsElem = root.getChildByName("rooms");
			createRooms(roomsElem);
			
			Element playerElem = root.getChildByName("player");
			int playerGridRow = playerElem.getInt("grid_row");
			int playerGridCol = playerElem.getInt("grid_col");
			int playerRow = playerElem.getInt("row");
			int playerCol = playerElem.getInt("col");
			theGrid.initPlayer(playerGridRow, playerGridCol, playerRow, playerCol);
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "parseFile", e);
		}
	}
	
	private void initGrid(Element sizeElem) {		
		int numRows = sizeElem.getInt("num_rows");
		int numCols = sizeElem.getInt("num_cols");
		theGrid.initGrid(numRows, numCols);
	}
	
	private void createRooms(Element roomsElem) {
		Array<Element> rooms = roomsElem.getChildrenByName("room");
		for(Element roomElem : rooms) {
			int row = roomElem.getInt("row");
			int col = roomElem.getInt("col");
			Vector2 roomPos = new Vector2(row, col);	
			createRoom(roomPos, roomElem);
		}
	}
	
	private void createRoom(Vector2 roomPos, Element roomElem) {
		Room room = new Room(roomPos);
		
		Element openingsElem = roomElem.getChildByName("openings");
		room.setOpenings(getRoomOpenings(openingsElem, null));
		
		Element borderOverridesElem = roomElem.getChildByName("border_overrides");
		room.setBorderOverrides(getBorderOverrides(borderOverridesElem));
		
		Element scriptsElem = roomElem.getChildByName("scripts");	
		createScripts(room, scriptsElem);
		
		Element chainPadsElem = roomElem.getChildByName("chain_pads");	
		if(chainPadsElem != null) {
			createChainPads(room, chainPadsElem);
		}
		
		Element entitiesElem = roomElem.getChildByName("entities");
		createEntities(room, entitiesElem);
		
		// Need the global coords now.
		Array<Vector2> openingLocations = getRoomOpenings(openingsElem, room);		
		createRoomBorder(room, openingLocations);
	
		theGrid.addRoom(room);
	}
	
	private void createEntities(Room room, Element entitiesElem) {
		if(entitiesElem == null) {
			return;
		}
		
		Array<Element> entities = entitiesElem.getChildrenByName("entity");
		for(Element entityElem : entities) {
			String type = entityElem.get("type");
			String id = entityElem.get("id", null);			
			int row = entityElem.getInt("row", -1);
			int col = entityElem.getInt("col", -1);
			String colorHex = entityElem.get("color", null);
			
			Array<String> ids = new Array<String>();
			Array<Vector2> positions = new Array<Vector2>();
			if(row > -1 && col > -1) {
				Vector2 pos = Room.getWorldPosition(room, row, col);
				positions.add(pos);
			}
			
			if(id != null) {
				ids.add(id);
			}
			
			Element locationsElem = entityElem.getChildByName("locations");			
			if(locationsElem != null) {
				Array<Element> locations = locationsElem.getChildrenByName("location");
				for(Element location : locations) {
					String locationId = location.get("id", null);
					int locationRow = location.getInt("row");
					int locationCol = location.getInt("col");
					Vector2 pos = Room.getWorldPosition(room, locationRow, locationCol);
					positions.add(pos);
					
					if(locationId != null) {
						ids.add(locationId);
					}
				}
			}			
			
			if(positions.size == 0) {
				// This is an invisible entity.
				Entity entity = createEntity(type, id, room, null, entityElem);
				room.addEntity(entity);
			} else {
				int i = 0;
				for(Vector2 pos : positions) {
					String currId = null;
					if(i < ids.size) {
						currId = ids.get(i);
					}
					
					Entity entity = createEntity(type, currId, room, pos, entityElem);
					if(colorHex != null) {
						Color color = Color.valueOf(colorHex);
						entity.setColor(color);
					}
					
					room.addEntity(entity);
					
					i++;
				}	
			}	
		}
	}
	
	private void createScripts(Room room, Element scriptsElem) {
		if(scriptsElem == null) {
			return;
		}
		
		Array<Element> scripts = scriptsElem.getChildrenByName("script");
		for(Element scriptElem : scripts) {
			String type = scriptElem.get("type");
			String id = scriptElem.get("id", null);	
			Script script = createScript(type, id, room, scriptElem);
			room.addScript(script);
		}
	}
	
	private void createChainPads(Room room, Element chainPadsElem) {
		// If vertical pad, will use the left side of the block.
		// If horizontal pad, will use the top side.
		Array<Element> chainPads = chainPadsElem.getChildrenByName("chain_pad");
		for(Element chainPad : chainPads) {
			int startRow = chainPad.getInt("start_row");
			int endRow = chainPad.getInt("end_row");
			int startCol = chainPad.getInt("start_col");
			int endCol = chainPad.getInt("end_col");
			boolean friction = chainPad.getBoolean("friction", true);
			boolean correct = chainPad.getBoolean("correct", false);
			createChainPad(room, startRow, endRow, startCol, endCol, friction, correct);		
		}
	}
	
	private void createChainPad(Room room, int startRow, int endRow, int startCol, int endCol, boolean friction, boolean correct) {
		boolean vert = startRow != endRow;
		Vector2 startWorldPos = Room.getWorldPosition(room, startRow, startCol);
		float startx = startWorldPos.x;
		float starty = startWorldPos.y;
		float len = (vert ? endRow - startRow + 1 : endCol - startCol + 1) * Room.SQUARE_SIZE * 0.95f;
     
		
		PolygonShape shape = new PolygonShape();		
		float width = vert ? 0.15f : len;
		float height = vert ? len : 0.15f;
		shape.setAsBox(width / 2, height / 2);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		
		float cx = startx + (width / 2);
		if(correct) {
			// This is a hack to correct the position of chain pads on the left side of a chain.
			cx += 0.145;
		}
		
		cx -= 0.145;
		
		float cy = starty + (height / 2) - 0.05f;
		if(vert) {
			cy += len * 0.05f / 2;
		} else {
			cx += len * 0.05f / 2;
		}		
		bodyDef.position.set(cx, cy);
		
		Body body = theGrid.getWorld().createBody(bodyDef);
		Fixture fixture = body.createFixture(shape, 0);
		
		if(!friction) {
			fixture.setFriction(0);
		}
		
		shape.dispose();
	}
	
	private Entity createEntity(String type, String id, Room room, Vector2 pos, Element entityElem) {	
		try {
			Class<?> c = getEntityClassFromType(type);
			Method method = c.getMethod("build", String.class, Room.class, Vector2.class, Element.class);
			Entity entity = (Entity)method.invoke(null, id, room, pos, entityElem);		
				
			return entity;
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "createEntity", e);
		}
		
		return null;
	}
	
	private Script createScript(String type, String id, Room room, Element scriptElem) {
		try {
			Class<?> c = getScriptClassFromType(type);
			Method method = c.getMethod("build", String.class, Room.class, Element.class);
			Script script = (Script)method.invoke(null, id, room, scriptElem);
			
			return script;
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "createScript", e);
		}
		
		return null;
	}
	
	private Class<?> getEntityClassFromType(String type) {
		try {
			String className = entityTypeClassMap.get(type);
			return Class.forName(className);
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "getEntityClassFromType", e);
		}
		
		return null;
	}
	
	private Class<?> getScriptClassFromType(String type) {
		try {
			String className = scriptTypeClassMap.get(type);
			return Class.forName(className);
		} catch(Exception e) {
			Gdx.app.error("tendersaucer", "getScriptClassFromType", e);
		}
		
		return null;
	}
	
	private void createRoomBorder(Room room, Array<Vector2> openings) {
		int startRow = room.getTopRow(true);
		int endRow = room.getBottomRow(true);
		int startCol = room.getLeftCol(true);
		int endCol = room.getRightCol(true);
		
		createGridBorderSide(0, startRow, endRow, startCol, endCol, room, openings);
		createGridBorderSide(1, startRow, endRow, startCol, endCol, room, openings);
		createGridBorderSide(2, startRow, endRow, startCol, endCol, room, openings);
		createGridBorderSide(3, startRow, endRow, startCol, endCol, room, openings);
	}
	
	private void createGridBorderSide(int side, int startRow, int endRow, int startCol, int endCol, Room room, Array<Vector2> openings) {
		int i;
		int j;
		int k;
		boolean vert = side == 1 || side == 3;
		
		if(side == 0) {
			i = startCol;
			j = startRow;
			k = endCol;
		} else if(side == 1) {
			i = startRow + 1;
			j = endCol;
			k = endRow;
		} else if(side == 2) {
			i = startCol;
			j = endRow;
			k = endCol - 1;
		} else {
			i = startRow + 1;
			j = startCol;
			k = endRow - 1;
		}
				
		int segLen = 0;
		int segStart = i;
		boolean prevWasOpening = false;
		while(segStart <= k && i <= k) {
			Vector2 currSquarePos = vert ? new Vector2(i, j) : new Vector2(j, i);
			if(!openings.contains(currSquarePos, false)) {
				segLen++;				
				prevWasOpening = false;
			} else {
				Vector2 pos = vert ? new Vector2(segStart, j) : new Vector2(j, segStart);
				pos = Room.getWorldPosition((int)pos.x, (int)pos.y);
				Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
				if(vert) {
					size.y *= segLen;
				} else {
					size.x *= segLen;
				}
				
				if(!prevWasOpening) {
					EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
					createGridBorderEntity(room, bodyDef);	
				}			
				
				segStart = i + 1;
				segLen = 0;
				
				prevWasOpening = true;
			}
			
			i++;
		}
		
		if(segStart <= k && i > k) {
			Vector2 pos = vert ? new Vector2(segStart, j) : new Vector2(j, segStart);
			pos = Room.getWorldPosition((int)pos.x, (int)pos.y);
			Vector2 size = new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE);
			if(vert) {
				size.y *= segLen;
			} else {
				size.x *= segLen;
			}
			
			EntityBodyDef bodyDef = new EntityBodyDef(pos, size, BodyType.StaticBody);
			createGridBorderEntity(room, bodyDef);	
		}
	}
	
	private void createGridBorderEntity(Room room, EntityBodyDef bodyDef) {
		RectangleEntity borderEntity = RectangleEntity.build(null, room, "", bodyDef);
		borderEntity.setBodyData();
		borderEntity.addFrictionTop(0.3f);
		theGrid.addGlobalEntity(borderEntity);
	}
	
	private Array<Vector2> getRoomOpenings(Element openingsElem, Room room) {
		Array<Vector2> openingLocations = new Array<Vector2>();		
		if(openingsElem != null) {
			Array<Element> openings = openingsElem.getChildrenByName("opening");
			for(Element openingElem : openings) {
				int row = openingElem.getInt("row");
				int col = openingElem.getInt("col");
				
				if(room == null) {
					openingLocations.add(new Vector2(row, col));
				} else {
					openingLocations.add(Room.getGridPosition(room, row, col));
				}				
			}
		}	
		
		return openingLocations;
	}
	
	private Array<Array<Object>> getBorderOverrides(Element borderOverridesElem) {
		Array<Array<Object>> borderOverrideList = new Array<Array<Object>>();		
		if(borderOverridesElem != null) {
			Array<Element> borderOverrides = borderOverridesElem.getChildrenByName("border_override");
			for(Element borderOverrideElem : borderOverrides) {
				int row = borderOverrideElem.getInt("row");
				int col = borderOverrideElem.getInt("col");
				String textureKey = borderOverrideElem.get("texture_key");
				Array<Object> borderOverride = new Array<Object>();
				borderOverride.add(row);
				borderOverride.add(col);
				borderOverride.add(textureKey);
				borderOverrideList.add(borderOverride);			
			}
		}	
		
		return borderOverrideList;
	}
}
