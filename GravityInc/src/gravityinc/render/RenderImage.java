package gravityinc.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import gravityinc.Body;
import gravityinc.Render;
import gravityinc.util.BetterEllipse;
import gravityinc.util.OrderedPair;
import gravityinc.util.VersatilePosition.PositionType;

public class RenderImage extends Render {

	private BufferedImage image;
	private OrderedPair semiSize;

	public RenderImage(Body body, String imagePath, Component panel) {
		super(body);
		try {
			image = ImageIO.read(RenderImage.class
					.getResourceAsStream(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
		semiSize = new OrderedPair(image.getWidth() / 2, image.getHeight() / 2);
	}

	@Override
	public void render(Graphics graphics, OrderedPair captorSystemPosition,
			OrderedPair centerScreenPosition, double zoom,
			long timeSincePreviousRender) {
		OrderedPair systemPosition;
		OrderedPair systemOffset;
		OrderedPair screenPosition;
		OrderedPair captorScreenPosition;
		if (body.getAnomaly() != null) {
			systemOffset = body.getAnomaly().asPosition()
					.getType(PositionType.Rectangular);
			systemPosition = captorSystemPosition.add(systemOffset);
			screenPosition = centerScreenPosition.add(systemOffset
					.divide(new OrderedPair(zoom)));
			screenPosition = centerScreenPosition.add(systemPosition
					.divide(new OrderedPair(zoom)));
			captorScreenPosition = centerScreenPosition
					.add(captorSystemPosition.divide(new OrderedPair(zoom)));
		} else {
			systemPosition = captorSystemPosition;
			systemOffset = new OrderedPair(0);
			screenPosition = centerScreenPosition;
			captorScreenPosition = new OrderedPair(0);
		}

		graphics.setColor(Color.black);
		((Graphics2D) graphics).setStroke(new BasicStroke(5));

		// Draw this body
		graphics.drawImage(image, screenPosition.getX() - semiSize.getX(),
				screenPosition.getY() - semiSize.getY(), null);

		// Draw this body's body.getAnomaly().getOrbit()
		if (body.getAnomaly() != null) {
			graphics.setColor(Color.blue);
			((Graphics2D) graphics).setStroke(new BasicStroke(1));
			double semiminorAxis = (body.getAnomaly().getOrbit().semimajorAxis * Math
					.sqrt(1.0 - (body.getAnomaly().getOrbit().eccentricity * body
							.getAnomaly().getOrbit().eccentricity)));
			double apoapsis = (body.getAnomaly().getOrbit().semimajorAxis * (1.0 + body
					.getAnomaly().getOrbit().eccentricity));
			((Graphics2D) graphics)
					.draw(AffineTransform
							.getRotateInstance(
									body.getAnomaly().getOrbit().rotation,
									captorScreenPosition.x,
									captorScreenPosition.y)
							.createTransformedShape(
									new BetterEllipse(
											RenderBasic.ORBIT_APPROXIMATION_SEGMENTS,
											centerScreenPosition.x
													+ ((captorSystemPosition.x - apoapsis) / zoom),
											centerScreenPosition.y
													+ ((captorSystemPosition.y - semiminorAxis) / zoom),
											body.getAnomaly().getOrbit().semimajorAxis
													* 2.0 / zoom, semiminorAxis
													* 2.0 / zoom)));
		}
		// Draw peers
		for (Object o : body.getPeers().toArray()) {
			Body peer = (Body) o;
			peer.getRender().render(graphics, systemPosition,
					centerScreenPosition, zoom, timeSincePreviousRender);
		}
	}
}
