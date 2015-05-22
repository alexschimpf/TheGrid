package misc;

import entity.Entity;

public final class BodyData {

	private boolean needsRemoved = false;
	private Entity entity;
	
	public BodyData(Entity entity) {
		this.entity = entity;;
	}
	
	public boolean needsRemoved() {
		return needsRemoved;
	}
	
	public void setNeedsRemoved() {
		needsRemoved = true;
		entity.getSprite().setAlpha(0);
	}
	
	public Entity getEntity() {
		return this.entity;
	}
}
