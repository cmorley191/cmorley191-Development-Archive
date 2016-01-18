package caseengine.realityinterface;

import java.util.ArrayList;

import caseengine.cognition.executive.Executive;
import caseengine.cognition.executive.InnovatorExecutive;
import caseengine.cognition.executive.LearnFirstExecutive;
import caseengine.cognition.executive.ProbableExecutive;
import caseengine.cognition.memory.QNetworkOld;
import caseengine.neural.node.Node;
import caseengine.neural.train.ErrorBackpropagator;

public class CognitionSequencer {

	private ArrayList<Sensor> sensors = new ArrayList<Sensor>();
	private ArrayList<Action> actions = new ArrayList<Action>();
	private QNetworkOld memory;
	private Executive executive;

	// Trainer is the ErrorBackpropagator class

	private ArrayList<InputNode> inputNodes = new ArrayList<InputNode>();

	private static final int HIDDEN_NODE_COUNT = 5;

	public CognitionSequencer(Sensor[] sensors, Action[] actions) {
		for (Sensor sensor : sensors)
			this.sensors.add(sensor);
		for (Action action : actions)
			this.actions.add(action);
		memory = new QNetworkOld(HIDDEN_NODE_COUNT);
		executive = new InnovatorExecutive(0.0);
		for (Sensor sensor : sensors) {
			InputNode node = new InputNode(sensor);
			inputNodes.add(node);
			memory.addInput();
		}
		memory.addActions(actions.length);
	}

	private class InputNode extends Node {

		private Sensor sensor;
		private double output = 0;

		private InputNode(Sensor sensor) {
			this.sensor = sensor;
		}

		private void update() {
			output = sensor.getData();
		}

		@Override
		public double getOutput() {
			return output;
		}
	}

	private int lastSelectedAction = -1;

	public void act() {
		double[] inputs = new double[inputNodes.size()];
		for (int i = 0; i < inputs.length; i++) {
			InputNode node = inputNodes.get(i);
			node.update();
			inputs[i] = node.getOutput();
		}
		double[] actionReinforcements = memory.pullActionReinforcements(inputs);
		for (double r : actionReinforcements)
			System.out.print(r + " ");
		System.out.println();
		lastSelectedAction = executive.selectAction(actionReinforcements);
		if (lastSelectedAction != -1)
			actions.get(lastSelectedAction).perform();
	}

	private static final double LEARNING_RATE = 0.1;

	public void reinforce(double reinforcement) {
		if (lastSelectedAction == -1)
			return;
		System.out.println("Reinforcing "
				+ (reinforcement + actions.get(lastSelectedAction)
						.getActionReinforcement()));
		ErrorBackpropagator.train(memory.getNetwork(), 1,
				new int[] { lastSelectedAction }, new double[] { reinforcement
						+ actions.get(lastSelectedAction)
								.getActionReinforcement() }, LEARNING_RATE);
		lastSelectedAction = -1;
	}
}
