package misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import core.IUpdate;

public class Animation implements IUpdate {

	public static enum State {
		Paused,
		Playing,
		Stopped,
		Done
	};
	
	private float stateTime;
	private boolean loop;
	private State state = State.Stopped;
	private Sprite sprite;
	private com.badlogic.gdx.graphics.g2d.Animation animation;
	
	public Animation(String filename, int numRows, int numCols, float frameDuration, boolean loop) {
		FileHandle file = Gdx.files.internal("textures/" + filename);
		Texture sheet = new Texture(file);
		sheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		int frameWidth = sheet.getWidth() / numCols;
		int frameHeight = sheet.getHeight() / numRows;
        TextureRegion[][] temp = TextureRegion.split(sheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[numRows * numCols];
        
        int index = 0;
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                frames[index++] = temp[row][col];
            }
        }
        
        this.loop = loop;
        sprite = new Sprite();
        animation = new com.badlogic.gdx.graphics.g2d.Animation(frameDuration, frames);
        stateTime = 0;   
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
		return sprite;
	}
}
