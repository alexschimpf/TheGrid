package misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
	private State state = State.Playing;
	private Sprite sprite;
	private com.badlogic.gdx.graphics.g2d.Animation animation;
	
	public Animation(String filename, int numRows, int numCols, float frameDuration, boolean loop) {
		FileHandle file = Gdx.files.internal("textures/" + filename);
		Texture sheet = new Texture(file);
		
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
		
		if(animation.isAnimationFinished(stateTime)) {
			return true;
		}
		
		return false;
	}

	@Override
	public void done() {
		state = State.Done;
	}
	
	public Sprite getSprite() {
		TextureRegion textureRegion = animation.getKeyFrame(stateTime, loop);
		textureRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sprite.setRegion(textureRegion);
		return sprite;
	}
	
	public void pause() {
		state = State.Paused;
	}
	
	public void play() {
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
}
