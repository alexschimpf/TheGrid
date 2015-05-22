package particle;

import misc.IDraw;
import misc.IUpdate;
import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;

public final class Particle implements IUpdate, IDraw, Poolable {

	private static final Textures TEXTURES = Textures.getInstance();
	
	private long startTime;
	private float duration;
	private float startAlpha;
	private float endAlpha;
	private float vx;
	private float vy;
	private float x;
	private float y;
	private boolean fadingIn = false;
	private Color color;
	private Color endColor;
	private Sprite sprite;
	private String textureKey;

	public Particle() {
	}
	
	public Particle(String textureKey, float x, float y, float width, float height, float vx, float vy, 
			        float duration, float startAlpha, float endAlpha, Color color, Color endColor, boolean fadeIn) {		
		set(textureKey, x, y, width, height, vx, vy, duration, startAlpha, endAlpha, color, endColor, fadeIn);
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

		long timeActive = TimeUtils.timeSinceMillis(startTime);
		float timeRatio = Math.min(1, timeActive / duration);
		
		if(fadingIn) {
			float fadeTimeRatio = Math.min(1, timeActive / (duration / 2));
			float alpha = fadeTimeRatio * startAlpha;
			alpha = alpha < 0 ? 0 : (alpha > 1 ? 1 : alpha);
			sprite.setAlpha(alpha);
			
			if(timeActive >= duration / 2) {
				fadingIn = false;
				startTime = TimeUtils.millis();
				timeActive = 0;
				duration /= 2;
			}
		} else {
			float alpha = startAlpha + ((endAlpha - startAlpha) * timeRatio);
			alpha = alpha < 0 ? 0 : (alpha > 1 ? 1 : alpha);

			sprite.setAlpha(alpha);
		}
		
		if(color != null && endColor != null && !color.equals(endColor)) {
			float dr = endColor.r - color.r;
			float dg = endColor.g - color.g;
			float db = endColor.b - color.b;			
			float r = color.r + (timeRatio * dr);
			float g = color.g + (timeRatio * dg);
			float b = color.b + (timeRatio * db);
			
			r = r < 0 ? 0 : (r > 1 ? 1 : r);
			g = g < 0 ? 0 : (g > 1 ? 1 : g);
			b = b < 0 ? 0 : (b > 1 ? 1 : b);
			
			sprite.setColor(r, g, b, sprite.getColor().a);
		}
		
		return timeActive > duration;
	}

	@Override
	public void done() {		
	}

	@Override
	public void reset() {	
		color = null;
		endColor = null;	
		sprite = null;
	}
	
	public void set(String textureKey, float x, float y, float width, float height, float vx, float vy, 
			        float duration, float startAlpha, float endAlpha, Color color, Color endColor, boolean fadeIn) {
		this.startTime = TimeUtils.millis();
		
		this.textureKey = textureKey;
		this.x = x;
		this.y = y;		
		this.vx = vx;
		this.vy = vy;
		this.duration = duration;
		this.startAlpha = startAlpha;
		this.endAlpha = endAlpha;
		this.color = color;
		this.endColor = endColor;
		this.fadingIn = fadeIn;
		
		sprite = TEXTURES.getSprite(textureKey);
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		
		if(color != null) {
			sprite.setColor(color);
		}
		
		Color c = sprite.getColor();
			
		if(fadingIn) {
			sprite.setColor(c.r, c.g, c.b, 0);
		} else {
			sprite.setColor(c.r, c.g, c.b, startAlpha);
		}
	}
	
	@Override
	public String toString() {
		return String.join("\n", 
			"textureKey: " + textureKey,
			"x: " + x, 
			"y: " + y, 
			"vx: " + vx, 
			"vy: " + vy, 
			"duration: " + duration, 
			"startAlpha: " + startAlpha, 
			"endAlpha: " + endAlpha,
			"r: " + sprite.getColor().r,
			"g: " + sprite.getColor().g,
			"b: " + sprite.getColor().b,
			"a: " + sprite.getColor().a
		);
	}
}
