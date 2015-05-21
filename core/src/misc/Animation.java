package misc;

import assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import core.IUpdate;

public class Animation implements IUpdate {

	public static enum State {
		Paused,
		Playing,
		Stopped,
		Done
	};
	
	private int numFrames;
	private float stateTime;
	private boolean loop;
	private State state = State.Stopped;
	private Sprite sprite;
	private com.badlogic.gdx.graphics.g2d.Animation animation;
	private Color color;
	
	public Animation(String textureKey, float totalDuration, boolean loop, boolean tint) {
		Array<AtlasRegion> regions = Textures.getInstance().getTextureRegions(textureKey);
		
		numFrames = regions.size;
		
        this.loop = loop;
        sprite = new Sprite();
        
        float frameDuration = totalDuration / numFrames;
        animation = new com.badlogic.gdx.graphics.g2d.Animation(frameDuration, regions);
        stateTime = 0;
        
        if(tint) {
        	color = Textures.getInstance().getRandomSchemeColor();
        }
	}

	@Override
	public boolean update() {
		if(state == State.Playing) {
			stateTime += Gdx.graphics.getDeltaTime();
		}
		
		if(!loop && animation.isAnimationFinished(stateTime)) {
			state = State.Done;
		}
		
		return false;
	}

	@Override
	public void done() {	
	}
	
	public void setSprite(Vector2 position, Vector2 size) {
		sprite.setPosition(position.x, position.y);
		sprite.setSize(size.x, size.y);
		sprite.setOrigin(size.x / 2, size.y / 2);
	}
	
	public Sprite getSprite() {
		TextureRegion textureRegion = animation.getKeyFrame(stateTime, loop);
		return getSprite(textureRegion);
	}
	
	public Sprite getSprite(int frame) {
		TextureRegion textureRegion = animation.getKeyFrames()[frame];
		return getSprite(textureRegion);
	}
	
	public void pause() {
		state = State.Paused;
	}
	
	public void play() {
		stateTime = 0;
		state = State.Playing;
	}
	
	public void stop() {
		state = State.Stopped;
		stateTime = 0;
	}
	
	public State getState() {
		return state;
	}
	
	public int getNumFrames() {
		return numFrames;
	}
	
	public boolean isPlaying() {
		return state == State.Playing;
	}
	
	public void setLooping(boolean loop) {
		this.loop = loop;
	}
	
	public boolean isLooping() {
		return loop;
	}
	
	private Sprite getSprite(TextureRegion textureRegion) {
		textureRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite.setRegion(textureRegion);
		sprite.setFlip(false, true);
		
		if(color != null) {
			sprite.setColor(color);
		}

		return sprite;
	}
}
