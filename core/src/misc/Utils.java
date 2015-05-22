package misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public final class Utils {
	
	private static final Globals GLOBALS = Globals.getInstance();
	
	public static float toMetersX(float pixels) {
		return GLOBALS.getCameraLeft() + (pixels * Globals.VIEWPORT_WIDTH) / Gdx.graphics.getWidth();
	}
	
	public static float toMetersY(float pixels) {
		return GLOBALS.getCameraTop() + (pixels * Globals.VIEWPORT_HEIGHT) / Gdx.graphics.getHeight();
	}
	
	public static float getCameraTop() {
		return GLOBALS.getCamera().position.y - (Globals.VIEWPORT_HEIGHT / 2);
	}
	
	public static float getCameraBottom() {
		return GLOBALS.getCamera().position.y + (Globals.VIEWPORT_HEIGHT / 2);
	}
	
	public static float getCameraLeft() {
		return GLOBALS.getCamera().position.x - (Globals.VIEWPORT_WIDTH / 2);
	}
	
	public static float getCameraRight() {
		return GLOBALS.getCamera().position.x + (Globals.VIEWPORT_WIDTH / 2);
	}
	
	public static float[] getLocalVertices(PolygonShape shape) {
		return Utils.getVertices(null, shape);
	}
	
	public static float[] getWorldVertices(Body body, PolygonShape shape) {
		return Utils.getVertices(body, shape);
	}
	
	private static float[] getVertices(Body body, PolygonShape shape) {
		Vector2 vertex = new Vector2();
		float[] vertices = new float[shape.getVertexCount() * 2];
		for(int i = 0; i < shape.getVertexCount(); i++) {
			shape.getVertex(i, vertex);
			
			if(body != null) {
				vertex = body.getWorldPoint(vertex);
			}
			
			vertices[i * 2] = vertex.x;
			vertices[(i * 2) + 1] = vertex.y;
		}
		
		return vertices;
	}
}
