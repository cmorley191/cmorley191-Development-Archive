package casereality.numberclimb;

import caseengine.realityinterface.Action;
import caseengine.realityinterface.CognitionSequencer;
import caseengine.realityinterface.Sensor;
import casereality.util.ControlThread;
import casereality.util.UpdateThread;
import casereality.util.UpdateThread.UpdateFunction;

public class NumberClimb {

	private static int position;
	private static double[] reinforcements;
	private static CognitionSequencer mind;

	private static final long UPDATE_INTERVAL = 1000;
	private static final long UPDATE_COUNT_HALT = Long.MAX_VALUE;
	private static final int AREA_SIZE = 50;

	public static void main(String[] args) {
		int reinforceSize = (AREA_SIZE % 2 == 0) ? AREA_SIZE + 1 : AREA_SIZE;
		reinforcements = new double[reinforceSize];
		for (int i = 0; i < reinforceSize; i++)
			reinforcements[i] = (i < (reinforceSize / 2)) ? i : reinforceSize - i - 1.0;
		position = 0;
		
		mind = new CognitionSequencer(new Sensor[] { new PositionSensor(),
				new SpaceSensor() }, new Action[] { new Up(), new Down(),
				new Idle() });

		UpdateFunction updateFunction = new UpdateFunction() {
			@Override
			public void update() {
				System.out.println();
				System.out.println("Original Position: " + position);
				mind.act();
			}

			@Override
			public boolean enabled() {
				return true;
			}
		};

		new ControlThread(updateFunction).start();
	}

	private static class PositionSensor implements Sensor {

		@Override
		public double getData() {
			return position;
		}

	}

	private static class SpaceSensor implements Sensor {

		@Override
		public double getData() {
			return reinforcements.length;
		}

	}

	private static final double MOVEMENT_REINFORCEMENT = -0.2;
	private static final double BOUND_COLLISION_REINFORCEMENT = -1.0;
	private static final double IDLE_REINFORCEMENT = -0.1;

	private static class Up implements Action {

		@Override
		public double getActionReinforcement() {
			return MOVEMENT_REINFORCEMENT;
		}

		@Override
		public void perform() {
			System.out.print("Up ");
			if (position == reinforcements.length - 1) {
				System.out.println("Collision");
				mind.reinforce(BOUND_COLLISION_REINFORCEMENT);
			} else {
				position++;
				System.out.println("Successful - " + position);
				mind.reinforce(reinforcements[position]);
			}
		}
	}

	private static class Down implements Action {

		@Override
		public double getActionReinforcement() {
			return MOVEMENT_REINFORCEMENT;
		}

		@Override
		public void perform() {
			System.out.print("Down ");
			if (position == 0) {
				System.out.println("Collision");
				mind.reinforce(BOUND_COLLISION_REINFORCEMENT);
			} else {
				position--;
				System.out.println("Successful - " + position);
				mind.reinforce(reinforcements[position]);
			}
		}
	}

	private static class Idle implements Action {

		@Override
		public double getActionReinforcement() {
			return IDLE_REINFORCEMENT;
		}

		@Override
		public void perform() {
			System.out.println("Idle - " + position);
			mind.reinforce(reinforcements[position]);
		}
	}
}
