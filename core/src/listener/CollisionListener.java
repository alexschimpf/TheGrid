package listener;

import misc.BodyData;
import misc.Globals;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entity.Entity;
import entity.special.Player;
import entity.special.PlayerShot;

public class CollisionListener implements ContactListener {
	
	private int numFootContacts = 0;
	private Globals globals = Globals.getInstance();
	
	public CollisionListener() {	
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		Body bodyA = fixA.getBody();
		Body bodyB = fixB.getBody();

		BodyData dataA = (BodyData)bodyA.getUserData();
		BodyData dataB = (BodyData)bodyB.getUserData();
		if(dataA == null || dataB == null) {
			return;
		}
		
		Entity a = dataA.getEntity();
		Entity b = dataB.getEntity();
		
		if(a == null || b == null) {
			return;
		}
		
		boolean footContact = (fixA.isSensor() && a.getType().equals("player")) ||
				              (fixB.isSensor() && b.getType().equals("player")); 
		if(footContact) {
			numFootContacts++;
			globals.getTheGrid().getPlayer().setJumping(numFootContacts < 1);
		}
		
		a.onBeginContact(b);
		b.onBeginContact(a);
		
		checkShotCollision(a, b);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		Body bodyA = fixA.getBody();
		Body bodyB = fixB.getBody();

		BodyData dataA = (BodyData)bodyA.getUserData();
		BodyData dataB = (BodyData)bodyB.getUserData();
		if(dataA == null || dataB == null) {
			return;
		}
		
		Entity a = dataA.getEntity();
		Entity b = dataB.getEntity();
		
		if(a == null || b == null) {
			return;
		}
		
		boolean footContact = (fixA.isSensor() && a.getType().equals("player")) ||
                              (fixB.isSensor() && b.getType().equals("player")); 
        if(footContact) {
        	numFootContacts--;
        	globals.getTheGrid().getPlayer().setJumping(numFootContacts < 1);
        }
		
		a.onEndContact(b);
		b.onEndContact(a);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
	
	public void checkShotCollision(Entity a, Entity b) {
		if(!a.getType().equals("shot") && !b.getType().equals("shot")) {
			return;
		}
		
		PlayerShot shot = (PlayerShot)(a.getType().equals("shot") ? a : b);
		Entity other = a.getType().equals("shot") ? b : a;
		
		shot.getBodyData().setNeedsRemoved();	

		if(other.getType().equals("shot")) {
			((PlayerShot)other).getBodyData().setNeedsRemoved();
		}
	}
}
