package entity;

import java.util.HashMap;

import misc.Animation;
import misc.EntityBodyDef;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;

public class BlockChainEntity extends RectangleEntity {

	private static final float ACTIVATED_DURATION = 880; //780;

	private long activationStartTime = 0;
	private boolean activated = false;
	private int state = 0;
	private String chainStartId;
	private Color color;
	protected Animation animation;
	private HashMap<Integer, String> stateMachine = new HashMap<Integer, String>();
	
	protected BlockChainEntity(Room room, String textureKey, float x, float y, String chainStartId, 
			                   HashMap<Integer, String> stateMachine) {
		super(room, textureKey, BlockChainEntity.getEntityBodyDef(x, y));
		
		this.chainStartId = chainStartId;
		this.stateMachine = stateMachine;
		
		color = textures.getRandomSchemeColor();
	}
	
	public static Entity build(String id, Room room, Vector2 pos, Element elem) {
		Element custom = elem.getChildByName("custom");
			
		String chainStartId = custom.get("start_block_id");
		
		HashMap<Integer, String> stateMachine = new HashMap<Integer, String>();
		Element stateMap = custom.getChildByName("state_map");
		if(stateMap != null) {
			Array<Element> events = stateMap.getChildrenByName("event");
			for(Element event : events) {
				int state = event.getInt("state");
				String nextId = event.get("next");
				stateMachine.put(state, nextId);
			}
		}	
		
		BlockChainEntity entity = new BlockChainEntity(room, "filling_block", pos.x, pos.y, chainStartId, stateMachine);
		entity.setId(id);
		entity.setBodyData();
		
		if(entity.isChainStart()) {
			entity.activate();
		}
		
		return entity;
	}
	
	public static EntityBodyDef getEntityBodyDef(float x, float y) {
		return new EntityBodyDef(new Vector2(x, y), new Vector2(Room.SQUARE_SIZE, Room.SQUARE_SIZE), BodyType.StaticBody);
	}
	
	@Override
	public boolean update() {	
		animation.update();
		
		updateSprite();

		float timeSinceActivated = TimeUtils.timeSinceMillis(activationStartTime);
		if(activated && !(isChainStart() && state == 0) &&
		   timeSinceActivated > ACTIVATED_DURATION) {
			restartChain();
		}
		
		return super.update();
	}
	
	@Override
	public String getType() {
		return "block_chain";
	}

	@Override
	public void onBeginContact(Entity entity) {
		super.onBeginContact(entity);
		
		if((isPlayer(entity) || isShot(entity)) && activated) {
			String nextId = stateMachine.get(state);
			
			deactivate();
			state++;
			
			if(nextId != null) {
				Entity next = room.getEntityById(nextId);
				
				if(next.getType().equals("block_chain")) {
					((BlockChainEntity)next).activate();
				} else {
					((IBlockChainDone)next).onChainDone();
				}			
			}
		}
	}
	
	public void activate() {
		if(isChainStart() && state == 0) {
			sprite.setRegion(textures.getTextureRegion("block_chain_start"));
		} else {
			animation.play();
		}
		
		activationStartTime = TimeUtils.millis();
		activated = true;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	@Override
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		Vector2 pos = new Vector2(x, y);
		Vector2 size = new Vector2(width, height);
		
		animation = new Animation("filling_block", ACTIVATED_DURATION / 1000.0f, false, false);
		animation.setSprite(pos, size);

		sprite = new Sprite(textures.getTextureRegion("block"));
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
	}

	private void deactivate() {
		activated = false;
		animation.stop();
	}

	private void restartChain() {
		for(Entity entity : room.getEntities()) {
			if(entity instanceof BlockChainEntity) {
				BlockChainEntity blockChainEntity = (BlockChainEntity)entity;
				blockChainEntity.setState(0);
				blockChainEntity.deactivate();
			}
		}
		
		BlockChainEntity startBlock = (BlockChainEntity)room.getEntityById(chainStartId);
		startBlock.activate();
	}
	
	private void updateSprite() {
		if(animation.isPlaying()) {
			sprite = animation.getSprite();
		} else if(isChainStart()) {
			if(state == 0) {
				sprite.setRegion(textures.getTextureRegion("block_chain_start"));
			} else {
				sprite.setRegion(textures.getTextureRegion("block"));
			}
 			
		} else {
			sprite.setRegion(textures.getTextureRegion("block"));
		}
		
		sprite.setColor(color);
	}
	
	private boolean isChainStart() {
		return id.equals(chainStartId);
	}
}
