package casereality.botfield;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import caseengine.application.Actuator;
import caseengine.application.Sensor;
import caseengine.application.mind.CaseMind;
import casereality.util.Math2D;
import casereality.util.UpdateThread;
import casereality.util.UpdateThread.UpdateFunction;

@SuppressWarnings("serial")
public class BotField extends JPanel {

	private static final Dimension WINDOW_SIZE = new Dimension(800, 600);
	private static final long UPDATE_INTERVAL = 10;
	private static final long UPDATE_COUNT_HALT = Long.MAX_VALUE;

	private static ObstacleSensor[] sensors = {
			new ObstacleSensor(80, Math2D.degreeToRadian(20)),
			new ObstacleSensor(80, Math2D.degreeToRadian(40)),
			new ObstacleSensor(80, Math2D.degreeToRadian(60)),
			new ObstacleSensor(80, Math2D.degreeToRadian(-20)),
			new ObstacleSensor(80, Math2D.degreeToRadian(-40)),
			new ObstacleSensor(80, Math2D.degreeToRadian(-60)),
			new ObstacleSensor(80, Math2D.degreeToRadian(160)),
			new ObstacleSensor(80, Math2D.degreeToRadian(-160)) };
	@SuppressWarnings("rawtypes")
	private static Actuator[] actuators = { new Motor() };
	private static CaseMind mind = new CaseMind();

	private static Rectangle2D.Double bot = new Rectangle2D.Double(50, 400, 20,
			20);
	private static ObstacleSensor botDirection = new ObstacleSensor(100, 0);
	private static Rectangle2D.Double[] obstacles = {
			new Rectangle2D.Double(0, 0, 30, WINDOW_SIZE.getHeight()),
			new Rectangle2D.Double(0, 0, WINDOW_SIZE.getWidth(), 30),
			new Rectangle2D.Double(0, WINDOW_SIZE.getHeight() - 70,
					WINDOW_SIZE.getWidth(), 30),
			new Rectangle2D.Double(WINDOW_SIZE.getWidth() - 40, 0, 30,
					WINDOW_SIZE.getHeight()),
			 new Rectangle2D.Double(100, 100, 60, 360),
			new Rectangle2D.Double(100, 100, 580, 60),
			 new Rectangle2D.Double(620, 100, 60, 360),
			new Rectangle2D.Double(100, 400, 580, 60) };
	private static double linearBotSpeed = 5;
	private static double angularBotSpeed = Math.PI / 30.0;
	private static double botRotation = 5.0 * Math.PI / 3.0;

	private static ArrayList<Boolean> collisionHistory = new ArrayList<Boolean>();

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Bot Field");
		frame.setSize(WINDOW_SIZE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(new BotField());

		for (Sensor<?> sensor : sensors)
			mind.addSensor(sensor);
		for (Actuator<?> actuator : actuators)
			mind.addActuator(actuator);

		new UpdateThread(new UpdateFunction() {
			@Override
			public void update() {
				mind.act();
				frame.repaint();
				int collisions = 0;
				for (Boolean collision : collisionHistory)
					if (collision)
						collisions++;
				frame.setTitle("Bot Field - Collisions/second: " + collisions);
			}

			@Override
			public boolean enabled() {
				return frame.isEnabled();
			}
		}, UPDATE_INTERVAL, UPDATE_COUNT_HALT).start();
	}

	private static class ObstacleSensor implements
			Sensor<ObstacleSensor.ObstacleSensorChannel> {

		private enum ObstacleSensorChannel {
			channel;
		}

		private double angle;
		private double range;

		public ObstacleSensor(double range, double angle) {
			this.angle = angle;
			this.range = range;
		}

		public Line2D.Double getBeam() {
			double angle = this.angle + botRotation;
			double botx = bot.x + (bot.width / 2.0);
			double boty = bot.y + (bot.height / 2.0);
			Point2D.Double rangePoint = Math2D.polarToRect(range, angle);
			return new Line2D.Double(botx, boty, botx + rangePoint.x, boty
					+ rangePoint.y);
		}

		private double getData() {
			Point2D.Double[] intersects = new Point2D.Double[0];
			for (Rectangle2D.Double obstacle : obstacles) {
				Point2D.Double[] touches = Math2D.getIntersections(getBeam(),
						obstacle);
				if (touches.length > 0) {
					int origLength = intersects.length;
					intersects = Arrays.copyOf(intersects, intersects.length
							+ touches.length);
					for (int i = 0; i < touches.length; i++)
						intersects[i + origLength] = touches[i];
				}
			}
			Point2D.Double botPosition = new Point2D.Double(bot.x, bot.y);
			if (intersects.length == 0)
				return range;
			else
				return Math2D.distance(botPosition,
						Math2D.nearestPoint(botPosition, intersects));
		}

		@Override
		public EnumMap<ObstacleSensorChannel, Double> fetchSensations() {
			// System.out.println(angle + " - " + getData());
			EnumMap<ObstacleSensorChannel, Double> sensations = new EnumMap<ObstacleSensorChannel, Double>(
					ObstacleSensorChannel.class);
			sensations.put(ObstacleSensorChannel.channel, getData() / range);
			return sensations;
		}

	}

	private static final double REINFORCEMENT_FORWARD = 0.02;
	private static final double REINFORCEMENT_REVERSE = -0.005;
	private static final double REINFORCEMENT_COLLISION = // -bot.width
	// / linearBotSpeed;
	-10.0;
	@SuppressWarnings("unused")
	private static final double REINFORCEMENT_IDLE = 0.0;
	private static final double REINFORCEMENT_ROTATION = -0.01;

	private static class Motor implements Actuator<Motor.MotorActions> {

		private enum MotorActions {
			Forward, Reverse, RotateRight, RotateLeft, ForwardRight, ForwardLeft;
		}

		@Override
		public EnumSet<MotorActions> getActionSet() {
			EnumSet<MotorActions> returnSet = EnumSet
					.noneOf(MotorActions.class);
			returnSet.add(MotorActions.Forward);
			returnSet.add(MotorActions.Reverse);
			// returnSet.add(MotorActions.RotateRight);
			// returnSet.add(MotorActions.RotateLeft);
			returnSet.add(MotorActions.ForwardRight);
			returnSet.add(MotorActions.ForwardLeft);
			return returnSet;
		}

		private static int historyLength = (int) (1000 / UPDATE_INTERVAL);

		private void setCollisionHistory(boolean collision) {
			collisionHistory.add(collision);
			if (collisionHistory.size() > historyLength)
				collisionHistory.remove(0);
		}

		@Override
		public boolean performAction(Enum<?> a) {
			if (!(a instanceof MotorActions))
				return false;
			MotorActions action = (MotorActions) a;
			Point2D.Double newBotPosition;
			Rectangle2D.Double oldBot;
			boolean collision;
			Rectangle2D.Double intersectingObstacle;

			switch (action) {
			case Forward:
				System.out.print("Forward");
				newBotPosition = Math2D
						.polarToRect(linearBotSpeed, botRotation);
				newBotPosition = new Point2D.Double(bot.x + newBotPosition.x,
						bot.y + newBotPosition.y);
				oldBot = bot;
				bot = new Rectangle2D.Double(newBotPosition.x,
						newBotPosition.y, bot.width, bot.height);
				collision = false;
				intersectingObstacle = null;
				for (Rectangle2D.Double obstacle : obstacles)
					if (obstacle.intersects(bot)) {
						intersectingObstacle = obstacle;
						collision = true;
						break;
					}
				if (collision) {
					System.out.println("Collision");
					mind.reinforce(REINFORCEMENT_COLLISION
							* Math2D.getOverlap(bot, intersectingObstacle));
					bot = oldBot;
					setCollisionHistory(true);
				} else {
					System.out.println();
					mind.reinforce(REINFORCEMENT_FORWARD);
					setCollisionHistory(false);
				}
				return true;
			case Reverse:
				System.out.print("Reverse");
				newBotPosition = Math2D.polarToRect(-linearBotSpeed,
						botRotation);
				newBotPosition = new Point2D.Double(bot.x + newBotPosition.x,
						bot.y + newBotPosition.y);
				oldBot = bot;
				bot = new Rectangle2D.Double(newBotPosition.x,
						newBotPosition.y, bot.width, bot.height);
				collision = false;
				intersectingObstacle = null;
				for (Rectangle2D.Double obstacle : obstacles)
					if (obstacle.intersects(bot)) {
						intersectingObstacle = obstacle;
						collision = true;
						break;
					}
				if (collision) {
					System.out.println("Collision");
					mind.reinforce(REINFORCEMENT_COLLISION);
					bot = oldBot;
					setCollisionHistory(true);
				} else {
					System.out.println();
					mind.reinforce(REINFORCEMENT_REVERSE);
					setCollisionHistory(false);
				}
				return true;
			case RotateRight:
				System.out.println("RotateRight");
				botRotation += angularBotSpeed;
				mind.reinforce(REINFORCEMENT_ROTATION);
				setCollisionHistory(false);
				return true;
			case RotateLeft:
				System.out.println("RotateLeft");
				botRotation -= angularBotSpeed;
				mind.reinforce(REINFORCEMENT_ROTATION);
				setCollisionHistory(false);
				return true;
			case ForwardRight:
				System.out.print("ForwardRight");
				newBotPosition = Math2D.polarToRect(linearBotSpeed, botRotation
						+ (angularBotSpeed));
				newBotPosition = new Point2D.Double(bot.x + newBotPosition.x,
						bot.y + newBotPosition.y);
				oldBot = bot;
				bot = new Rectangle2D.Double(newBotPosition.x,
						newBotPosition.y, bot.width, bot.height);
				collision = false;
				intersectingObstacle = null;
				for (Rectangle2D.Double obstacle : obstacles)
					if (obstacle.intersects(bot)) {
						intersectingObstacle = obstacle;
						collision = true;
						break;
					}
				botRotation += angularBotSpeed;
				if (collision) {
					System.out.println("Collision");
					mind.reinforce(REINFORCEMENT_COLLISION);
					bot = oldBot;
					botRotation -= angularBotSpeed;
					setCollisionHistory(true);
				} else {
					System.out.println();
					mind.reinforce(REINFORCEMENT_FORWARD
							+ REINFORCEMENT_ROTATION);
					setCollisionHistory(false);
				}
				return true;
			case ForwardLeft:
				System.out.print("ForwardLeft");
				newBotPosition = Math2D.polarToRect(linearBotSpeed, botRotation
						- (angularBotSpeed));
				newBotPosition = new Point2D.Double(bot.x + newBotPosition.x,
						bot.y + newBotPosition.y);
				oldBot = bot;
				bot = new Rectangle2D.Double(newBotPosition.x,
						newBotPosition.y, bot.width, bot.height);
				collision = false;
				intersectingObstacle = null;
				for (Rectangle2D.Double obstacle : obstacles)
					if (obstacle.intersects(bot)) {
						intersectingObstacle = obstacle;
						collision = true;
						break;
					}
				botRotation -= angularBotSpeed;
				if (collision) {
					System.out.println("Collision");
					mind.reinforce(REINFORCEMENT_COLLISION);
					bot = oldBot;
					botRotation += angularBotSpeed;
					setCollisionHistory(true);
				} else {
					System.out.println();
					mind.reinforce(REINFORCEMENT_FORWARD
							+ REINFORCEMENT_ROTATION);
					setCollisionHistory(false);
				}
				return true;
			}
			return true;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.draw(bot);
		for (Shape obstacle : obstacles)
			g2d.draw(obstacle);
		for (ObstacleSensor sensor : sensors)
			g2d.draw(sensor.getBeam());
		g2d.setColor(Color.red);
		g2d.draw(botDirection.getBeam());
	}

}
