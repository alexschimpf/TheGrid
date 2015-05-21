package particle;

import java.util.Iterator;

import misc.Globals;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import core.IDraw;
import core.IUpdate;
import core.Room;

public class ParticleEffect implements IUpdate, IDraw {
	
	public static final int DEFAULT_MIN_NUM_PARTICLES = 6;
	public static final int DEFAULT_MAX_NUM_PARTICLES = 12;
	public static final float DEFAULT_MIN_DURATION = 400;
	public static final float DEFAULT_MAX_DURATION = 1300;
	public static final float DEFAULT_MIN_SIZE = Room.SQUARE_SIZE / 10;
	public static final float DEFAULT_MAX_SIZE = Room.SQUARE_SIZE / 8;
	public static final float DEFAULT_MIN_V = -Room.SQUARE_SIZE;
	public static final float DEFAULT_MAX_V = Room.SQUARE_SIZE;
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final Color DEFAULT_END_COLOR = Color.WHITE;
	
	protected static final Pool<Particle> PARTICLE_POOL = new Pool<Particle>() {
	    @Override
	    protected Particle newObject() {
	        return new Particle();
	    }
	};
		
	protected float x;
	protected float y;
	protected String textureKey;
	protected float minVX = DEFAULT_MIN_V;
	protected float minVY = DEFAULT_MIN_V;
	protected float maxVX = DEFAULT_MAX_V;
	protected float maxVY = DEFAULT_MAX_V;
	protected float minSize = DEFAULT_MIN_SIZE;
	protected float maxSize = DEFAULT_MAX_SIZE;
	protected float minDuration = DEFAULT_MIN_DURATION;
	protected float maxDuration = DEFAULT_MAX_DURATION;
	protected int minNumParticles = DEFAULT_MIN_NUM_PARTICLES;
	protected int maxNumParticles = DEFAULT_MAX_NUM_PARTICLES;
	protected Color color = DEFAULT_COLOR;
	protected Color endColor = DEFAULT_END_COLOR;
	protected Array<Particle> particles = new Array<Particle>();

	public ParticleEffect(String textureKey, float x, float y) {
		this.textureKey = textureKey;
		this.x = x;
		this.y = y;
	}
	
	public void begin() {
		int numParticles = MathUtils.random(minNumParticles, maxNumParticles);
		for(int i = 0; i < numParticles; i++) {
			Particle particle = ParticleEffect.PARTICLE_POOL.obtain();
			float size = MathUtils.random(minSize, maxSize);
			float vx = MathUtils.random(minVX, maxVX);
			float vy = MathUtils.random(minVY, maxVY);
			float duration = MathUtils.random(minDuration, maxDuration);
			particle.set(textureKey, x, y, size, vx, vy, duration, color, endColor);
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
	
	public ParticleEffect minSize(float size) {
		this.minSize = size;
		return this;
	}
	
	public ParticleEffect maxSize(float size) {
		this.maxSize = size;
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
	
	public ParticleEffect color(Color color) {
		this.color = color;
		return this;
	}
	
	public ParticleEffect endColor(Color color) {
		this.endColor = color;
		return this;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		for(Particle particle : particles) {
			particle.draw(batch);
		}
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
