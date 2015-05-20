package particle;

import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;

import core.IDraw;
import core.IUpdate;
import core.Room;

public class Particle implements IUpdate, IDraw, Poolable {

	protected long startTime;
	protected float duration;
	protected float vx;
	protected float vy;
	protected float x;
	protected float y;
	protected Color color;
	protected Color endColor;
	protected Color currColor;
	protected Sprite sprite;
	protected Textures textures = Textures.getInstance();
	
	public Particle() {
	}
	
	public Particle(String textureKey, float x, float y, float size, float vx, float vy, 
			        float duration, Color color, Color endColor) {		
		set(textureKey, x, y, size, vx, vy, duration, color, endColor);
	}

	@Override
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public boolean update() {
		float x = sprite.getX();
		float y = sprite.getY();
		
		sprite.setX(x + (vx * Gdx.graphics.getDeltaTime()));
		sprite.setY(y + (vy * Gdx.graphics.getDeltaTime()));

		if(!color.equals(endColor)) {
			float dr = endColor.r - color.r;
			float dg = endColor.g - color.g;
			float db = endColor.b - color.b;			
			float r = currColor.r + (dr * Gdx.graphics.getDeltaTime());
			float g = currColor.g + (dg * Gdx.graphics.getDeltaTime());
			float b = currColor.b = (db * Gdx.graphics.getDeltaTime());
			currColor.set(r, g, b, 1);
			sprite.setColor(currColor);
		}
		
		long timeActive = TimeUtils.timeSinceMillis(startTime);
		sprite.setAlpha(1 - (timeActive / duration));
		
		return timeActive > duration;
	}

	@Override
	public void done() {		
	}

	@Override
	public void reset() {	
		x = 0;
		y = 0;		
		vx = 0;
		vy = 0;
		startTime = 0;
		duration = 0;
		currColor = color;
		color = null;
		endColor = null;	
		sprite = null;
	}
	
	public void set(String textureKey, float x, float y, float size, float vx, float vy, 
			        float duration, Color color, Color endColor) {
		this.startTime = TimeUtils.millis();
		
		this.x = x;
		this.y = y;		
		this.vx = vx;
		this.vy = vy;
		this.duration = duration;
		this.color = color;
		this.currColor = color;
		this.endColor = endColor;
		
		sprite = textures.getSprite(textureKey);
		sprite.setSize(size, size);
		sprite.setPosition(x, y);
		sprite.setColor(color);
	}
}
