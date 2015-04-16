package script;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader.Element;

import core.Room;
import entity.TransportChainEntity;

public class UpdateTransportChainScript extends Script {

	protected int lastStartIndex = 2;
	protected boolean initialized = false;
	protected Array<String> chain;
	
	public UpdateTransportChainScript(Room room, Array<String> chain) {
		super(room);
		
		this.chain = chain;
	}
	
	public static Script build(String id, Room room, Element elem) {
		Element custom = elem.getChildByName("custom");
		Element chainIdsElem = custom.getChildByName("chain_ids");
		Array<Element> chainIds = chainIdsElem.getChildrenByName("id");
		Array<String> chain = new Array<String>();
		for(Element chainIdElem : chainIds) {
			String chainId = chainIdElem.getText();
			chain.add(chainId);
		}
		
		UpdateTransportChainScript script = new UpdateTransportChainScript(room, chain);
		script.setId(id);
		
		return script;
	}
	
	@Override
	public void run(Object... params) {
		boolean completed = (Boolean)params[0];
		int startIndex = completed ? ++lastStartIndex : lastStartIndex;		
		for(int i = 0; i < chain.size; i++) {
			TransportChainEntity entity = getChainEntity(i);
			if(i == startIndex) {
				entity.setTransport(false);
				entity.setColor(Color.BLUE);
			} else if(i < startIndex) {
				entity.setTransport(false);
				entity.setColor(Color.WHITE);
			} else {
				Color color = (i - startIndex) % 2 == 0 ? Color.BLUE : Color.WHITE;
				boolean transport = (i - startIndex) % startIndex == 0;
				entity.setTransport(transport);
				entity.setColor(color);
			}
		}
	}
	
	@Override
	public boolean update() {
		if(!initialized) {
			initialized = true;
			run(false);
		}
	
		return super.update();
	}
	
	protected int getTransportIndex(int startIndex) {
		if(startIndex == 1) {
			return 0;
		} else if(startIndex == 2) {
			return 0;
		} else if(startIndex == 3) {
			return 1;
		} else {
			return 3;
		}
	}
	
	protected TransportChainEntity getChainEntity(int i) {
		String id = chain.get(i);
		return (TransportChainEntity)room.getEntityById(id);
	}
}
