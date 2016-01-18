package gravityinc;

import gravityinc.util.OrderedPair;

import java.awt.Graphics;

public abstract class Render {

	protected final Body body;

	public Render(Body body) {
		this.body = body;
	}

	public abstract void render(Graphics graphics,
			OrderedPair captorSystemPosition, OrderedPair centerScreenPosition,
			double zoom, long timeSincePreviousRender);

}
