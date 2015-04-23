package particle;

import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import core.IDraw;
import core.IUpdate;
import core.Room;

public class Particle implements IUpdate, IDraw {

	protected long startTime;
	protected float duration;
	protected Sprite sprite;
	protected Vector2 velocity;
	protected Textures textures = Textures.getInstance();
	
	public Particle(Vector2 position, Vector2 velocity, float duration) {
		this.startTime = TimeUtils.millis();
		this.velocity = velocity;
		this.duration = duration;
		
		sprite = new Sprite(textures.getTextureRegion("light_gray"));
		sprite.setSize(Room.SQUARE_SIZE / 10, Room.SQUARE_SIZE / 10);
		sprite.setPosition(position.x, position.y);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public boolean update() {
		float x = sprite.getX();
		float y = sprite.getY();
		
		sprite.setX(x + (velocity.x * Gdx.graphics.getDeltaTime()));
		sprite.setY(y + (velocity.y * Gdx.graphics.getDeltaTime()));
		
		long timeActive = TimeUtils.timeSinceMillis(startTime);
		sprite.setAlpha(1 - (timeActive / duration));
		
		return timeActive > duration;
	}

	@Override
	public void done() {		
	}
}
