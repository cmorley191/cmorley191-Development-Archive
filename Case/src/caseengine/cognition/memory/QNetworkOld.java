package caseengine.cognition.memory;

import caseengine.function.DifferentiableFunction;
import caseengine.neural.network.LayeredNetwork;
import caseengine.neural.node.DifferentiableNode;

public class QNetworkOld {

	private LayeredNetwork network = new LayeredNetwork(3);

	private static final LayeredNetwork.ConnectionScheme NEW_NODE_SCHEME = new LayeredNetwork.RandomWeightScheme(
			0, 1.0);
	private static final DifferentiableFunction OUTPUT_ACTIVATION_FUNCTION = new caseengine.function.LinearFunction(
			1, 0);
	private static final DifferentiableFunction HIDDEN_ACTIVATION_FUNCTION = new caseengine.function.SigmoidFunction();

	public QNetworkOld(int hiddenNodeCount) {
		if (hiddenNodeCount < 1)
			hiddenNodeCount = 1;
		for (; hiddenNodeCount > 0; hiddenNodeCount--)
			network.addNode(new DifferentiableNode(HIDDEN_ACTIVATION_FUNCTION),
					0);
	}

	public LayeredNetwork getNetwork() {
		return network;
	}

	public void addInput() {
		network.addInput(NEW_NODE_SCHEME);
	}

	public void addAction() {
		network.addNode(new DifferentiableNode(OUTPUT_ACTIVATION_FUNCTION), 1,
				NEW_NODE_SCHEME);
	}

	public void addActions(int numActions) {
		for (; numActions > 0; numActions--)
			addAction();
	}

	public double[] pullActionReinforcements(double[] inputs) {
		return network.pullOutputs(inputs, 1);
	}
}
