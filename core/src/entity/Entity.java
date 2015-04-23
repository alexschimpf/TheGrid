package entity;

import misc.BodyData;
import misc.Globals;
import assets.Sounds;
import assets.Textures;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import core.IDraw;
import core.IUpdate;
import core.Room;
import core.TheGrid;

public abstract class Entity implements IUpdate, IDraw, ICollide {

	protected int numContacts = 0;
	protected String id;
	protected Body body;
	protected Sprite sprite;
	protected Room room;	
	protected Textures textures = Textures.getInstance();
	protected Sounds sounds = Sounds.getInstance();
	protected Globals globals = Globals.getInstance();
	protected TheGrid theGrid = globals.getTheGrid();
	protected World world = theGrid.getWorld();
	
	protected Entity(Room room) {
		this.room = room;
	}
	
	public abstract String getType();

	@Override
	public boolean update() {
		sprite.setPosition(getLeft(), getTop());
		sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());

//		if(body.getType() != BodyType.StaticBody) {
//			Room currRoom = theGrid.getRoomAt(getCenterX(), getCenterY());
//			if(currRoom != room) {
//				setRoom(currRoom);
//			}
//		}
		
		return false;
	}
	
	@Override
	public void done() {
		world.destroyBody(body);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		if(sprite.getTexture() != null) {
			sprite.draw(batch);
		}
		
	}
	
	@Override
	public void onBeginContact(Entity entity) {
		numContacts++;
	}
	
	@Override
	public void onEndContact(Entity entity) {
		numContacts--;
	}
	
	public void setBodyData() {
		BodyData bodyData = new BodyData(this);
		body.setUserData(bodyData);
	}
	
	public BodyData getBodyData() {
		if(body == null) {
			return null;
		}
		
		return (BodyData)body.getUserData();
	}
	
	public void setPosition(float left, float top) {
		float centerX = left + (getWidth() / 2);
		float centerY = top + (getHeight() / 2);
		body.setTransform(centerX, centerY, body.getAngle());
		update();
	}
	
	public Body getBody() {
		return body;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setLinearVelocity(float vx, float vy) {
		body.setLinearVelocity(vx, vy);
	}
	
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}
	
	public float getWidth() {
		return sprite.getWidth();
	}
	
	public float getHeight() {
		return sprite.getHeight();
	}
	
	public float getLeft() {
		return getCenterX() - (getWidth() / 2);
	}
	
	public float getRight() {
		return getCenterX() + (getWidth() / 2);
	}
	
	public float getTop() {
		return getCenterY() - (getHeight() / 2);
	}
	
	public float getBottom() {
		return getCenterY() + (getHeight() / 2);
	}
	
	public float getCenterX() {
		return body.getPosition().x;
	}
	
	public float getCenterY() {	
		return body.getPosition().y;
	}
	
	public void setId(String id) {
		if(id == null) {
			id = Integer.toString(hashCode());
		}
		
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	protected abstract FixtureDef createFixtureDef();
	
	protected void createSprite(String textureKey, float x, float y, float width, float height) {
		TextureRegion textureRegion = textures.getTextureRegion(textureKey);
		sprite = textureRegion != null ? new Sprite(textureRegion) : new Sprite();
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(width / 2, height / 2);
		sprite.setFlip(false, true);
	}
	
	protected void createBody(BodyType bodyType) {
		FixtureDef fixtureDef = createFixtureDef();
		createBody(bodyType, fixtureDef);
	}
	
	protected void createBody(BodyType bodyType, FixtureDef fixtureDef) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		
		float cx = sprite.getX() + (getWidth() / 2);
		float cy = sprite.getY() + (getHeight() / 2);
		bodyDef.position.set(cx, cy);

		body = world.createBody(bodyDef);		
		body.setFixedRotation(true);
		body.setAwake(false);
		
		attachFixture(fixtureDef);		
	}
	
	protected void attachFixture(FixtureDef fixtureDef) {
		body.createFixture(fixtureDef);
		fixtureDef.shape.dispose();
	}
	
	protected void setRoom(Room room) {
		this.room.removeEntity(this);
		this.room = room;
		this.room.addEntity(this);
	}
	
	protected boolean isPlayer(Entity entity) {
		return entity.getType().equals("player");
	}
	
	protected boolean isShot(Entity entity) {
		return entity.getType().equals("shot");
	}
}
