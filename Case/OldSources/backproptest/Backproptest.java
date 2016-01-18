package casereality.backproptest;
import caseengine.function.SigmoidFunction;
import caseengine.neural.network.LayeredNetwork;
import caseengine.neural.network.LayeredNetwork.ConnectionScheme;
import caseengine.neural.node.ConnectableNode;
import caseengine.neural.node.DifferentiableNode;
import caseengine.neural.node.Node;
import caseengine.neural.train.ErrorBackpropagator;

public class Backproptest {

	public static void main(String[] args) {
		LayeredNetwork network = initNetwork();
		double[] expectedOutputs = { 0.01, 0.99 };
		double out1 = network.getLayer(1).get(0).getOutput();
		double out2 = network.getLayer(1).get(1).getOutput();
		System.out.println("Out 1: " + out1 + ", Out 2: " + out2
				+ ", Error: "
				+ error(new double[] { out1, out2 }, expectedOutputs));
		int count = 0;
		while (count < 10000) {
			ErrorBackpropagator.train(network, 1, expectedOutputs, 0.5);
			out1 = network.getLayer(1).get(0).getOutput();
			out2 = network.getLayer(1).get(1).getOutput();
			System.out.println("Out 1: " + out1 + ", Out 2: " + out2
					+ ", Error: "
					+ error(new double[] { out1, out2 }, expectedOutputs));
			count++;
		}
	}

	private static double error(double[] out, double[] expectedOut) {
		double totalError = 0;
		for (int i = 0; i < out.length; i++)
			totalError += 0.5 * Math.pow(expectedOut[i] - out[i], 2);
		return totalError;
	}

	private static LayeredNetwork initNetwork() {
		LayeredNetwork network = new LayeredNetwork(3);
		for (int i = 0; i < 2; i++)
		network.addNode(new DifferentiableNode(new SigmoidFunction()), 0);
		network.addInput(new Node() {

			@Override
			public double getOutput() {
				return 0.05;
			}

		}, new ConnectionScheme() {
			private boolean a = false;

			@Override
			public double generateWeight(Node input, ConnectableNode output) {
				double out = 0.15;
				if (a)
					out = 0.2;
				else
					a = true;
				return out;
			}
		});
		network.addInput(new Node() {

			@Override
			public double getOutput() {
				return 0.1;
			}

		}, new ConnectionScheme() {
			private boolean a = false;

			@Override
			public double generateWeight(Node input, ConnectableNode output) {
				double out = 0.25;
				if (a)
					out = 0.3;
				else
					a = true;
				return out;
			}
		});
		network.addNode(new DifferentiableNode(new SigmoidFunction()), 1,
				new ConnectionScheme() {
					private boolean a = false;

					@Override
					public double generateWeight(Node input,
							ConnectableNode output) {
						double out = 0.4;
						if (a)
							out = 0.45;
						else
							a = true;
						return out;
					}
				});
		network.addNode(new DifferentiableNode(new SigmoidFunction()), 1,
				new ConnectionScheme() {
					private boolean a = false;

					@Override
					public double generateWeight(Node input,
							ConnectableNode output) {
						double out = 0.5;
						if (a)
							out = 0.55;
						else
							a = true;
						return out;
					}
				});
		Node bias1 = new Node() {
			@Override
			public double getOutput() {
				return 0.35;
			}
		};
		Node bias2 = new Node() {
			@Override
			public double getOutput() {
				return 0.60;
			}
		};
		network.getLayer(0).get(0).setConnection(bias1, 1);
		network.getLayer(0).get(1).setConnection(bias1, 1);
		network.getLayer(1).get(0).setConnection(bias2, 1);
		network.getLayer(1).get(1).setConnection(bias2, 1);
		return network;
	}
}
