package listener;

import misc.BodyData;
import misc.Globals;
import assets.Sounds;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entity.DisappearingRectangleEntity;
import entity.Entity;
import entity.special.Player;
import entity.special.PlayerShot;

public class CollisionListener implements ContactListener {
	
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
		
		checkFootContacts(fixA, fixB, a, b, true);
		
		a.onBeginContact(b);
		b.onBeginContact(a);
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
		
		checkFootContacts(fixA, fixB, a, b, false);
		
		a.onEndContact(b);
		b.onEndContact(a);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	protected void checkFootContacts(Fixture fixA, Fixture fixB, Entity a, Entity b, boolean beginContact) {
		if(fixA.isSensor() && fixB.isSensor()) {
			return;
		}
		
		boolean footContact = (fixA.isSensor() && a.getType().equals("player")) ||
	                          (fixB.isSensor() && b.getType().equals("player")); 
        if(footContact) {
        	Player player = globals.getTheGrid().getPlayer();        	
        	if(beginContact) {
        		player.incrementFootContacts();
        	} else {
        		player.decrementFootContacts();
        	}
        }
	}
}
