package caseengine.neural.train;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import caseengine.function.DifferentiableFunction;
import caseengine.neural.network.LayeredNetwork;
import caseengine.neural.node.ConnectableNode;
import caseengine.neural.node.DifferentiableNode;
import caseengine.neural.node.FunctionalNode;
import caseengine.neural.node.Node;

public class ErrorBackpropagator {

	@SuppressWarnings("unused")
	private static boolean isEligible(LayeredNetwork network, int outputLayer) {
		if (outputLayer >= network.getLayerCount())
			return false;
		for (int i = outputLayer; i >= 0; i--) {
			ArrayList<ConnectableNode> layer = network.getLayer(i);
			for (ConnectableNode node : layer)
				if (!(node instanceof DifferentiableNode || (node instanceof FunctionalNode && ((FunctionalNode) node)
						.getActivationFunction() instanceof DifferentiableFunction)))
					return false;
		}
		return true;
	}

	/**
	 * Trains all nodes in the specified layer with the specified target outputs
	 * for each node
	 * 
	 * @param network
	 * @param outputLayer
	 * @param targetOutputs
	 * @param learningRate
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean oldTrain(LayeredNetwork network, int outputLayer,
			double[] targetOutputs, double learningRate) {
		if (outputLayer < 0 || outputLayer >= network.getLayerCount())
			return false;

		HashMap<ConnectableNode, HashMap<Node, Double>> newWeights = new HashMap<ConnectableNode, HashMap<Node, Double>>();

		double[] previousLayerHeadGradients = null;
		// Accessed via [node receiving input][node sending output]
		double[][] previousLayerWeights = null;
		ArrayList<ConnectableNode> nextLayer = null;
		ArrayList<Node> inputs = network.getInputs();

		// Train weights from top to bottom
		for (int layerIndex = outputLayer; layerIndex >= 0; layerIndex--) {
			ArrayList<ConnectableNode> layer = (nextLayer == null) ? network
					.getLayer(layerIndex) : nextLayer;
			nextLayer = (layerIndex > 0) ? network.getLayer(layerIndex - 1)
					: null;

			if (layerIndex == outputLayer)
				if (layer.size() != targetOutputs.length)
					return false;

			double[] layerHeadGradients = new double[layer.size()];
			double[][] layerWeights = new double[layer.size()][];

			for (int nodeIndex = 0; nodeIndex < layer.size(); nodeIndex++) {
				ConnectableNode node = layer.get(nodeIndex);

				// DestinationGradient = (output - target) for output nodes,
				// (sum_of_all_outputs(outputHeadGradient * weightToOutput)) for
				// hidden nodes
				double destinationGradient = 0;
				if (layerIndex == outputLayer)
					destinationGradient = node.getOutput()
							- targetOutputs[nodeIndex];
				else
					for (int i = 0; i < previousLayerHeadGradients.length; i++)
						destinationGradient += previousLayerHeadGradients[i]
								* previousLayerWeights[i][nodeIndex];

				// HeadGradient = DestinationGradient * ActivationGradient
				layerHeadGradients[nodeIndex] = destinationGradient
				// Will throw class cast exception if network contains
				// non-functional nodes or non-differentiable functions
				// in those nodes
						* ((DifferentiableFunction) ((FunctionalNode) node)
						// ActivationGradient = Derivative of activation
						// function
								.getActivationFunction()).differentiate(node
								.getNetInput());

				newWeights.put(node, new HashMap<Node, Double>());
				ArrayList<Entry<Node, Double>> connections = node
						.getConnections();
				layerWeights[nodeIndex] = new double[connections.size()];
				for (Entry<Node, Double> connection : connections) {
					// Don't train connections to nodes outside of the network
					if ((layerIndex > 0 && !nextLayer.contains(connection
							.getKey()))
							|| (layerIndex == 0 && !inputs.contains(connection
									.getKey())))
						continue;
					// NewWeight = CurrentWeight - (learningRate *
					// ErrorGradient)
					newWeights.get(node).put(connection.getKey(),
							connection.getValue() - (learningRate *
							// ErrorGradient = HeadGradient * SourceGradient
									(layerHeadGradients[nodeIndex] *
									// SourceGradient = Output of node
									// feeding
									// into the connection
									connection.getKey().getOutput())));
					if (layerIndex > 0)
						layerWeights[nodeIndex][nextLayer.indexOf(connection
								.getKey())] = connection.getValue();
				}
			}

			previousLayerHeadGradients = layerHeadGradients;
			previousLayerWeights = layerWeights;
		}
		for (ConnectableNode node : newWeights.keySet())
			for (Entry<Node, Double> connection : newWeights.get(node)
					.entrySet())
				node.setConnection(connection.getKey(), connection.getValue());
		return true;
	}

	/**
	 * Trains only the specified nodes in the specified output layer with the
	 * specified target outputs
	 * 
	 * @param network
	 * @param outputLayer
	 * @param outputNodes
	 * @param targetOutputs
	 * @param learningRate
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean oldTrain(LayeredNetwork network, int outputLayer,
			int[] outputNodes, double[] targetOutputs, double learningRate) {
		if ((outputLayer < 0 || outputLayer >= network.getLayerCount())
				|| (outputNodes.length != targetOutputs.length))
			return false;

		HashMap<ConnectableNode, HashMap<Node, Double>> newWeights = new HashMap<ConnectableNode, HashMap<Node, Double>>();

		double[] previousLayerHeadGradients = null;
		// Accessed via [node receiving input][node sending output]
		double[][] previousLayerWeights = null;
		ArrayList<ConnectableNode> nextLayer = null;
		ArrayList<Node> inputs = network.getInputs();

		// Train weights from top to bottom
		for (int layerIndex = outputLayer; layerIndex >= 0; layerIndex--) {
			ArrayList<ConnectableNode> layer = (nextLayer == null) ? network
					.getLayer(layerIndex) : nextLayer;
			nextLayer = (layerIndex > 0) ? network.getLayer(layerIndex - 1)
					: null;

			if (layerIndex == outputLayer)
				if (layer.size() < targetOutputs.length)
					return false;

			double[] layerHeadGradients = new double[layer.size()];
			double[][] layerWeights = new double[layer.size()][];

			for (int nodeIndex = 0; nodeIndex < layer.size(); nodeIndex++) {
				ConnectableNode node = layer.get(nodeIndex);

				// DestinationGradient = (output - target) for output nodes,
				// (sum_of_all_outputs(outputHeadGradient * weightToOutput)) for
				// hidden nodes
				double destinationGradient = 0;
				if (layerIndex == outputLayer) {
					boolean output = false;
					for (int i = 0; i < outputNodes.length; i++)
						if (nodeIndex == outputNodes[i]) {
							destinationGradient = node.getOutput()
									- targetOutputs[i];
							output = true;
							break;
						}
					if (!output)
						continue;
				} else if (layerIndex == outputLayer - 1)
					for (int i = 0; i < outputNodes.length; i++)
						destinationGradient = previousLayerHeadGradients[0]
								* previousLayerWeights[outputNodes[i]][nodeIndex];
				else
					for (int i = 0; i < previousLayerHeadGradients.length; i++)
						destinationGradient += previousLayerHeadGradients[i]
								* previousLayerWeights[i][nodeIndex];

				// HeadGradient = DestinationGradient * ActivationGradient
				layerHeadGradients[nodeIndex] = destinationGradient
				// Will throw class cast exception if network contains
				// non-functional nodes or non-differentiable functions
				// in those nodes
						* ((DifferentiableFunction) ((FunctionalNode) node)
						// ActivationGradient = Derivative of activation
						// function
								.getActivationFunction()).differentiate(node
								.getNetInput());

				newWeights.put(node, new HashMap<Node, Double>());
				ArrayList<Entry<Node, Double>> connections = node
						.getConnections();
				layerWeights[nodeIndex] = new double[connections.size()];
				for (Entry<Node, Double> connection : connections) {
					// Don't train connections to nodes outside of the network
					if ((layerIndex > 0 && !nextLayer.contains(connection
							.getKey()))
							|| (layerIndex == 0 && !inputs.contains(connection
									.getKey())))
						continue;
					// NewWeight = CurrentWeight - (learningRate *
					// ErrorGradient)
					newWeights.get(node).put(connection.getKey(),
							connection.getValue() - (learningRate *
							// ErrorGradient = HeadGradient * SourceGradient
									(layerHeadGradients[nodeIndex] *
									// SourceGradient = Output of node
									// feeding
									// into the connection
									connection.getKey().getOutput())));
					if (layerIndex > 0)
						layerWeights[nodeIndex][nextLayer.indexOf(connection
								.getKey())] = connection.getValue();
				}
			}

			previousLayerHeadGradients = layerHeadGradients;
			previousLayerWeights = layerWeights;
		}
		for (ConnectableNode node : newWeights.keySet())
			for (Entry<Node, Double> connection : newWeights.get(node)
					.entrySet())
				node.setConnection(connection.getKey(), connection.getValue());
		return true;
	}

	/**
	 * Backpropagates the error between the actual output and expected output of
	 * the specified nodes through the preceding structure of the specified
	 * network. Connections from nodes outside the network to those in the
	 * network are not affected.
	 * <p>
	 * Note that the network must contain nodes in and preceding the specified
	 * layer that are {@link caseengine.neural.node.FunctionalNode
	 * FunctionalNodes} whose functions are
	 * {@link caseengine.function.DifferentiableFunction
	 * DifferentiableFunctions}.
	 * 
	 * @param network
	 *            the network being trained
	 * @param inputs
	 *            the values of the specified network's input layer nodes that
	 *            should cause the network to produce the specified outputs
	 * @param trainingLayer
	 *            the index of the general layer of the nodes whose error is
	 *            being backpropagated
	 * @param trainingNodes
	 *            the indices of the nodes nodes in the specified layer whose
	 *            error is being backpropagated
	 * @param targetOutputs
	 *            the expected outputs being trained into the specified nodes
	 * @param learningRate
	 *            the effect of backpropagation on the specified network - lower
	 *            learning rates require more training iterations to cause an
	 *            effect but could cause overall learning over time to be more
	 *            productive
	 * @throws NullPointerException
	 *             if {@code network}, {@code inputs}, {@code trainingNodes}, or
	 *             {@code targetOutputs} are null
	 * @throws IllegalArgumentException
	 *             if not enough or too many input values were specified, if
	 *             {@code trainingNodes} contains duplicates, if not enough or
	 *             too many target output values were specified, if any nodes in
	 *             or preceding the specified layer are not
	 *             {@code FunctionalNodes}, or if any nodes in or preceding the
	 *             specified layer have functions that are not
	 *             {@code DifferentiableFunctions}
	 * @throws IndexOutOfBoundsException
	 *             if {@code trainingLayer} is out of range of the specified
	 *             network's general layers or if a member of
	 *             {@code trainingNodes} is out of range of the training layer's
	 *             nodes
	 */
	@SuppressWarnings("unchecked")
	public void trainNetwork(LayeredNetwork network, double[] inputs,
			int trainingLayer, int[] trainingNodes, double[] targetOutputs,
			double learningRate) throws NullPointerException,
			IllegalArgumentException, IndexOutOfBoundsException {
		/*
		 * Invalid arguments check
		 */
		if (network == null || inputs == null || trainingNodes == null
				|| targetOutputs == null)
			throw new NullPointerException();
		// Method has no effect if no nodes are being trained or learning rate
		// is 0
		if (trainingNodes.length == 0 || learningRate == 0)
			return;

		if (inputs.length != network.getInputs().size())
			throw new IllegalArgumentException();
		if (trainingLayer < 0 || trainingLayer >= network.getLayerCount())
			throw new IndexOutOfBoundsException();
		ArrayList<ConnectableNode> trainingLayerNodes = network
				.getLayer(trainingLayer);
		// Check for duplicates in trainingNodes - two different values can't be
		// trained for one node
		int[] trainingNodesDuplicates = {};
		for (int trainingNode : trainingNodes) {
			if (trainingNode < 0 || trainingNode >= trainingLayerNodes.size())
				throw new IndexOutOfBoundsException();
			for (int trainingNodesDuplicate : trainingNodesDuplicates)
				if (trainingNode == trainingNodesDuplicate)
					throw new IllegalArgumentException();
			trainingNodesDuplicates = Arrays.copyOf(trainingNodesDuplicates,
					trainingNodesDuplicates.length + 1);
			trainingNodesDuplicates[trainingNodesDuplicates.length - 1] = trainingNode;
		}
		trainingNodesDuplicates = null;

		if (trainingNodes.length != targetOutputs.length)
			throw new IllegalArgumentException();

		/*
		 * Training
		 */
		// Cached materials
		ArrayList<? extends Node> nextLayerNodes = trainingLayerNodes;
		double[] nextLayerOutputs = network.pullOutputs(inputs, trainingLayer);
		double[] previousLayerHeadGradients = null;
		// Accessed as previousLayerConnections[receivingNode][sendingNode]
		double[][] previousLayerConnectionWeights = null;

		for (int layerIndex = trainingLayer; layerIndex >= 0; layerIndex--) {
			ArrayList<ConnectableNode> layerNodes = (ArrayList<ConnectableNode>) nextLayerNodes;
			double[] layerOutputs = nextLayerOutputs;
			double[] layerNetInputs = network.pullNetInputs(inputs, layerIndex);

			if (layerIndex > 0) {
				nextLayerNodes = network.getLayer(trainingLayer - 1);
				nextLayerOutputs = network.pullOutputs(inputs, layerIndex - 1);
			} else {
				nextLayerNodes = network.getInputs();
				nextLayerOutputs = inputs;
			}
			double[] layerHeadGradients = new double[layerNodes.size()];
			double[][] layerConnectionWeights = new double[layerNodes.size()][];

			for (int nodeIndex = 0; nodeIndex < layerNodes.size(); nodeIndex++) {
				ArrayList<Entry<Node, Double>> connections = layerNodes.get(
						nodeIndex).getConnections();
				layerConnectionWeights[nodeIndex] = new double[connections
						.size()];

				double destinationGradient;
				if (layerIndex == trainingLayer) {
					// Don't train nodes in the training layer not specified as
					// nodes to be trained
					int targetIndex = -1;
					for (int i = 0; i < trainingNodes.length; i++)
						if (nodeIndex == trainingNodes[i]) {
							targetIndex = i;
							break;
						}
					if (targetIndex == -1)
						continue;

					destinationGradient = layerOutputs[nodeIndex]
							- targetOutputs[targetIndex];
				} else {
					destinationGradient = 0;
					for (int outputNodeIndex = 0; outputNodeIndex < previousLayerHeadGradients.length; outputNodeIndex++)
						destinationGradient += previousLayerHeadGradients[outputNodeIndex]
								* previousLayerConnectionWeights[outputNodeIndex][nodeIndex];
				}

				double activationGradient;
				try {
					activationGradient = ((DifferentiableFunction) ((FunctionalNode) layerNodes
							.get(nodeIndex)).getActivationFunction())
							.differentiate(layerNetInputs[nodeIndex]);
				} catch (ClassCastException e) {
					throw new IllegalArgumentException();
				}

				double headGradient = destinationGradient * activationGradient;
				layerHeadGradients[nodeIndex] = headGradient;

				for (Entry<Node, Double> connection : connections) {
					// Don't train connections to nodes outside of the network
					if (!nextLayerNodes.contains(connection.getKey()))
						continue;

					int connectionNodeIndex = nextLayerNodes.indexOf(connection
							.getKey());
					layerConnectionWeights[nodeIndex][connectionNodeIndex] = connection
							.getValue();

					double sourceGradient = nextLayerOutputs[connectionNodeIndex];
					double errorGradient = headGradient * sourceGradient;
					layerNodes.get(nodeIndex).setConnection(
							connection.getKey(),
							connection.getValue()
									- (learningRate * errorGradient));

				}
			}

			previousLayerHeadGradients = layerHeadGradients;
			previousLayerConnectionWeights = layerConnectionWeights;
		}
	}
}
