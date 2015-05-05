package particle;

import java.util.Iterator;

import misc.Globals;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import core.IDraw;
import core.IUpdate;
import core.Room;
import entity.special.PlayerShot;

public class ParticleEffect implements IUpdate, IDraw {

	protected static final int MIN_NUM_PARTICLES = 7;
	protected static final int MAX_NUM_PARTICLES = 15;
	protected static final float MIN_PARTICLE_DURATION = 500;
	protected static final float MAX_PARTICLE_DURATION = 1500;
	
	protected Array<Particle> particles = new Array<Particle>();
	
	// TODO: Use Builder Pattern!!!
	protected ParticleEffect(String textureKey, Vector2 position, int numParticles, Boolean moveLeft, Boolean moveUp, Float maxDuration) {
		for(int i = 0; i < numParticles; i++) {
			float squareSize = Room.SQUARE_SIZE;
			float vx = MathUtils.random(squareSize / 6, squareSize);
			float vy = MathUtils.random(squareSize / 6, squareSize);
			Vector2 velocity = new Vector2(vx, vy);
			
			if(MathUtils.random() < 0.5) {
				velocity.x = 0 - velocity.x;
			}
			
			if(moveLeft != null) {
				if(moveLeft) {
					velocity.x = -Math.abs(velocity.x);
				} else {
					velocity.x = Math.abs(velocity.x);
				}
			}
			
			if(moveUp != null) {
				if(moveUp) {
					velocity.y = -Math.abs(velocity.y);
				} else {
					velocity.y = Math.abs(velocity.y);
				}
			}
			
			if(MathUtils.random() < 0.5) {
				velocity.y = 0 - velocity.y;
			}
			
			if(maxDuration == null) {
				maxDuration = MAX_PARTICLE_DURATION;
			}
			
			float duration = MathUtils.random(MIN_PARTICLE_DURATION, maxDuration);
			
			Particle particle = new Particle(textureKey, position, velocity, duration);
			particles.add(particle);
		}
	}
	
	public ParticleEffect(String textureKey, Vector2 position) {
		this(textureKey, position, MathUtils.random(MIN_NUM_PARTICLES, MAX_NUM_PARTICLES), null, null, null);
	}
	
	public static void startParticleEffect(String textureKey, Vector2 position, int numParticles, boolean moveLeft, float maxDuration) {
		ParticleEffect effect = new ParticleEffect(textureKey, position, numParticles, moveLeft, true, maxDuration);
		Globals.getInstance().getGame().addParticleEffect(effect);
	}
	
	public static void startParticleEffect(String textureKey, Vector2 position, int numParticles) {
		ParticleEffect effect = new ParticleEffect(textureKey, position, numParticles, null, null, null);
		Globals.getInstance().getGame().addParticleEffect(effect);
	}
	
	public static void startParticleEffect(String textureKey, Vector2 position) {
		ParticleEffect effect = new ParticleEffect(textureKey, position);
		Globals.getInstance().getGame().addParticleEffect(effect);
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
			}
		}
		
		return particles.size <= 0;
	}

	@Override
	public void done() {
	}
}
