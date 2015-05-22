package particle;

import java.util.Iterator;

import misc.Globals;
import misc.IDraw;
import misc.IUpdate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import core.Room;

public final class ParticleEffect implements IUpdate, IDraw {
	
	public static final int DEFAULT_MIN_NUM_PARTICLES = 6;
	public static final int DEFAULT_MAX_NUM_PARTICLES = 12;
	public static final float DEFAULT_MIN_DURATION = 400;
	public static final float DEFAULT_MAX_DURATION = 1300;
	public static final float DEFAULT_MIN_SIZE = Room.SQUARE_SIZE / 10;
	public static final float DEFAULT_MAX_SIZE = Room.SQUARE_SIZE / 8;
	public static final float DEFAULT_MIN_V = -Room.SQUARE_SIZE;
	public static final float DEFAULT_MAX_V = Room.SQUARE_SIZE;
	
	private static final Pool<Particle> PARTICLE_POOL = new Pool<Particle>() {
	    @Override
	    protected Particle newObject() {
	        return new Particle();
	    }
	};
		
	private float x;
	private float y;
	private String textureKey;
	private float minVX = DEFAULT_MIN_V;
	private float minVY = DEFAULT_MIN_V;
	private float maxVX = DEFAULT_MAX_V;
	private float maxVY = DEFAULT_MAX_V;
	private float minWidth = DEFAULT_MIN_SIZE;
	private float maxWidth = DEFAULT_MAX_SIZE;
	private float minHeight = DEFAULT_MIN_SIZE;
	private float maxHeight = DEFAULT_MAX_SIZE;
	private float minDuration = DEFAULT_MIN_DURATION;
	private float maxDuration = DEFAULT_MAX_DURATION;
	private int minNumParticles = DEFAULT_MIN_NUM_PARTICLES;
	private int maxNumParticles = DEFAULT_MAX_NUM_PARTICLES;
	private float startAlpha = 1;
	private float endAlpha = 0;
	private boolean fadeIn = false;
	private boolean fixSlowSpeed = false;
	private Color color = null;
	private Color endColor = null;
	private Array<Particle> particles = new Array<Particle>();

	public ParticleEffect(String textureKey, float x, float y) {
		this.textureKey = textureKey;
		this.x = x;
		this.y = y;
	}
	
	public void begin() {
		int numParticles = MathUtils.random(minNumParticles, maxNumParticles);	
		for(int i = 0; i < numParticles; i++) {
			Particle particle = ParticleEffect.PARTICLE_POOL.obtain();
			float width = MathUtils.random(minWidth, maxWidth);
			float height = MathUtils.random(minHeight, maxHeight);
			float vx = MathUtils.random(minVX, maxVX);
			float vy = MathUtils.random(minVY, maxVY);
			
			if(fixSlowSpeed && Math.abs(vx) < Math.abs(minVX) / 5) {
				vx = minVX / 5;
			}

			float duration = MathUtils.random(minDuration, maxDuration);
			particle.set(textureKey, x, y, width, height, vx, vy, duration, startAlpha, endAlpha, color, endColor, fadeIn);
			particles.add(particle);
		}
		
		Globals.getInstance().getGame().addParticleEffect(this);
	}
	
	public ParticleEffect minNumParticles(int numParticles) {
		this.minNumParticles = numParticles;
		return this;
	}
	
	public ParticleEffect maxNumParticles(int numParticles) {
		this.maxNumParticles = numParticles;
		return this;
	}
	
	public ParticleEffect minVX(float vx) {
		this.minVX = vx;
		return this;
	}
	
	public ParticleEffect maxVX(float vx) {
		this.maxVX = vx;
		return this;
	}
	
	public ParticleEffect minVY(float vy) {
		this.minVY = vy;
		return this;
	}
	
	public ParticleEffect maxVY(float vy) {
		this.maxVY = vy;
		return this;
	}
	
	public ParticleEffect minWidth(float size) {
		this.minWidth = size;
		return this;
	}
	
	public ParticleEffect maxWidth(float size) {
		this.maxWidth = size;
		return this;
	}
	
	public ParticleEffect minHeight(float size) {
		this.minHeight = size;
		return this;
	}
	
	public ParticleEffect maxHeight(float size) {
		this.maxHeight = size;
		return this;
	}
	
	public ParticleEffect minSize(float size) {
		this.minWidth = size;
		this.minHeight = size;
		return this;
	}
	
	public ParticleEffect maxSize(float size) {
		this.maxWidth = size;
		this.maxHeight = size;
		return this;
	}
	
	public ParticleEffect minDuration(float duration) {
		this.minDuration = duration;
		return this;
	}
	
	public ParticleEffect maxDuration(float duration) {
		this.maxDuration = duration;
		return this;
	}
	
	public ParticleEffect startAlpha(float alpha) {
		this.startAlpha = alpha;
		return this;
	}
	
	public ParticleEffect endAlpha(float alpha) {
		this.endAlpha = alpha;
		return this;
	}
	
	public ParticleEffect color(Color color) {
		this.color = color;
		return this;
	}
	
	public ParticleEffect endColor(Color color) {
		this.endColor = color;
		return this;
	}
	
	public ParticleEffect fadeIn(boolean fadeIn) {
		this.fadeIn = fadeIn;
		return this;
	}
	
	public ParticleEffect fixSlowSpeed(boolean fix) {
		this.fixSlowSpeed = fix;
		return this;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		for(Particle particle : particles) {
			particle.draw(batch);
		}
	}
	
	public String getTextureKey() {
		return textureKey;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

	@Override
	public boolean update() {
		Iterator<Particle> particlesItr = particles.iterator();
		while(particlesItr.hasNext()) {
			Particle particle = particlesItr.next();

			if(particle.update()) {
				particle.done();
				particlesItr.remove();
				
				PARTICLE_POOL.free(particle);
			}
		}
		
		return particles.size <= 0;
	}

	@Override
	public void done() {
	}
}
