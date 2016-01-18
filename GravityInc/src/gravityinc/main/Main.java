package gravityinc.main;

import gravityinc.Body;
import gravityinc.UniversalDisplay;
import gravityinc.UniversalDisplay.ActionManager;
import gravityinc.UniversalDisplay.ActionManager.ActionAdapter;
import gravityinc.render.RenderBasic;
import gravityinc.render.RenderImage;
import gravityinc.util.Orbit;
import gravityinc.util.OrderedPair;
import gravityinc.util.VersatileAnomaly;
import gravityinc.util.VersatileAnomaly.AnomalyType;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class Main {

	// Available systems: "alphaCentauri", "universal", "simple
	private static String systemToUse = "alphaCentauri";

	
	public static void main(String[] args) {
		UniversalDisplay game = new UniversalDisplay();
		JFrame frame = new JFrame("Gravity, Inc.");
		frame.setSize(new Dimension(1280, 720));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(game);

		Body universal = new Body(250);
		Body sun1 = new Body(90);
		sun1.setCaptor(universal, new Orbit(200, 0, 0), AnomalyType.Mean, 3.14);
		new Body(40)
				.setCaptor(sun1, new Orbit(60, 0.8, 0), AnomalyType.Mean, 4);
		new Body(30)
				.setCaptor(sun1, new Orbit(70, 0.4, 4), AnomalyType.Mean, 2);
		new Body(20)
				.setCaptor(sun1, new Orbit(70, 0.1, 2), AnomalyType.Mean, 0);
		new Body(80).setCaptor(universal, new Orbit(250, 0.3, -0.7),
				AnomalyType.Mean, -0.5);
		new Body(70).setCaptor(universal, new Orbit(400, 0.6, 0.5),
				AnomalyType.Mean, 5.5);
		setSystemRender(universal);

		Body simple = new Body(100);
		new Body(50).setCaptor(simple, new Orbit(300, 0, 0), AnomalyType.Mean,
				0);
		setSystemRender(simple);

		Body alphaCentauri = getAlphaCentauriSystem(game);

		final Body FFocus = simple.getPeers().get(0);

		if (systemToUse.equals("universal")) {
			game.loadUniverse(universal);
			game.startUniverse(frame);
			game.setTimeMultiplier(universal.getPeers().get(0)
					.getOrbitalPeriod() / 12.0);
		} else if (systemToUse.equals("simple")) {
			game.loadUniverse(simple);
			game.setFocus(simple);
			game.startUniverse(frame);
			game.setTimeMultiplier(simple.getPeers().get(0).getOrbitalPeriod() / 10.0);
		} else if (systemToUse.equals("alphaCentauri")) {
			game.loadUniverse(alphaCentauri);
			game.setFocus(alphaCentauri.getPeers().get(2).getPeers().get(0));
			game.setZoom(588392.296e-2);
			// game.setCameraPosition(new OrderedPair(0, 1.47098074e8));
			// game.setCameraPosition(new OrderedPair(1.04014045625e8));
			game.startUniverse(frame);
			game.setTimeMultiplier(3e3);
		}
		game.getActionManager().new InputKeyPress(KeyEvent.VK_SPACE, 0,
				game.getActionManager().new ActionToggleRunning());
		game.getActionManager().new InputMouseWheelUp(
				game.getActionManager().new ActionZoomIn());
		game.getActionManager().new InputMouseWheelDown(
				game.getActionManager().new ActionZoomOut());
		abstract class BurnAction extends ActionAdapter {
			public BurnAction(ActionManager enclosingInstance) {
				enclosingInstance.super();
			}

			protected void performBurn(double velocityChange) {
				double parameter = FFocus.getStandardGravitationalParameter();
				VersatileAnomaly newAnomaly = new VersatileAnomaly(FFocus
						.getAnomaly().asPosition(), FFocus
						.getAnomaly()
						.getVelocity(parameter)
						.add(new OrderedPair(FFocus
								.getAnomaly()
								.getVelocity(parameter)
								.divide(new OrderedPair(FFocus.getAnomaly()
										.getVelocity(parameter).magnitude()))
								.multiply(new OrderedPair(velocityChange)))),
						parameter);
				FFocus.setAnomaly(newAnomaly);
			}
		}
		game.getActionManager().new InputKeyPress(KeyEvent.VK_F, 0,
				new BurnAction(game.getActionManager()) {

					@Override
					public void performAction() {
						performBurn(1e-12);
					}

					@Override
					public boolean equals(Object o) {
						return (o != null) && (o.getClass().equals(getClass()));
					}
				});
		game.getActionManager().new InputKeyPress(KeyEvent.VK_R, 0,
				new BurnAction(game.getActionManager()) {

					@Override
					public void performAction() {
						performBurn(-1e-12);
					}

					@Override
					public boolean equals(Object o) {
						return (o != null) && (o.getClass().equals(getClass()));
					}
				});
	}

	private static Body getAlphaCentauriSystem(Component component) {
		Body sun = new Body(1.989e30);
		sun.setRender(new RenderImage(sun, "images/sun.png", component));

		Body mercury = new Body(3.301e23);
		mercury.setCaptor(sun, new Orbit(5.791e7, 0.2056, 1.3518700794),
				AnomalyType.Mean, 5.885);
		mercury.setRender(new RenderImage(mercury, "images/mercury.png", component));

		Body venus = new Body(4.8676e24);
		venus.setCaptor(sun, new Orbit(1.0821e8, 0.0067, 2.2956835759542),
				AnomalyType.Mean, 4.798);
		venus.setRender(new RenderImage(venus, "images/venus.png", component));

		Body earth = new Body(5.972e24);
		// earth.setCaptor(sun, new Orbit(1.496e8, 0.0167, 1.7967674211717),
		// AnomalyType.Mean, 4.767);
		earth.setCaptor(sun, new Orbit(1.496e8, 0.0167, 0), AnomalyType.Mean,
				4.767);
		earth.setRender(new RenderImage(earth, "images/earth.png", component));
		Body moon = new Body(7.342e22);
		moon.setCaptor(earth, new Orbit(3.844e5, 0.0549, 0), AnomalyType.Mean,
				2.013);
		moon.setRender(new RenderImage(moon, "images/moon.png", component));

		Body mars = new Body(6.4174e23);
		mars.setCaptor(sun, new Orbit(2.2792e8, 0.0935, 5.86501907915),
				AnomalyType.Mean, 2.737);
		mars.setRender(new RenderImage(mars, "images/mars.png", component));
		Body phobos = new Body(1.06e16);
		phobos.setCaptor(mars, new Orbit(9.378e3, 0.0151, 0), AnomalyType.Mean,
				0);
		phobos.setRender(new RenderImage(phobos, "images/phobos.png", component));
		// Body deimos = new Body(2.4e15);
		// deimos.setCaptor(mars, new Orbit(), 0);

		return sun;
	}

	private static void setSystemRender(Body body) {
		body.setRender(new RenderBasic(body));
		for (Body peer : body.getPeers())
			setSystemRender(peer);
	}
}
