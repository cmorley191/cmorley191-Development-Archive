package topologui;

import java.util.Arrays;

/**
 * A {@code NeuralNetwork} contains a set of nodes and connections for
 * extracting a set of outputs from a set of inputs.
 * 
 * @author Charlie Morley
 */
public class NeuralNetwork {

	/**
	 * The number of input nodes in this network.
	 */
	private final int inputNodes;

	/**
	 * The number of output nodes in this network.
	 */
	private final int outputNodes;

	/**
	 * The list of connections in this network.
	 */
	Connection[] connections;

	/**
	 * Constructs a {@code NeuralNetwork} with the given number of input nodes
	 * and output nodes. The {@code NeuralNetwork} also has connections from
	 * each of its input nodes to each of its output nodes with randomly
	 * generated weights between -1.0 and 1.0.
	 * 
	 * @param inputNodes
	 *            number of input nodes in this network
	 * @param outputNodes
	 *            number of output nodes in this network
	 */
	NeuralNetwork(int inputNodes, int outputNodes) {
		this.inputNodes = inputNodes;
		this.outputNodes = outputNodes;
		connections = new Connection[inputNodes * outputNodes];
		for (int i = 0; i < inputNodes; i++)
			for (int o = 0; o < outputNodes; o++)
				connections[(i * outputNodes) + o] = new Connection(i,
						inputNodes + o, true, (i * outputNodes) + o);
	}

	/**
	 * The tested fitness of this network. Contains {@code NaN} until tested.
	 */
	private double fitness = Double.NaN;

	/**
	 * Returns the tested fitness of this network. If the network has not been
	 * tested, returns {@code NaN}.
	 * 
	 * @return the fitness of this network - {@code NaN} if the network has not
	 *         been tested
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Sets this network's fitness to the supplied value.
	 * <p>
	 * Does not function if this network's fitness has already been set. <br>
	 * If the supplied value is {@code NaN}, sets this network's fitness to
	 * {@code 0.0}.
	 * 
	 * @param fitness
	 *            this network's new fitness
	 */
	public void setFitness(double fitness) {
		if (Double.isNaN(this.fitness))
			if (Double.isNaN(fitness))
				this.fitness = 0.0;
			else
				this.fitness = fitness;
	}

	/**
	 * Returns the compatibility between this network and the specified network,
	 * using the supplied weights.
	 * <p>
	 * Compatibility is determined with the sum of: <br>
	 * 1. the weighted and normalized number of excess connections <br>
	 * 2. the weighted and normalized number of disjoint connections <br>
	 * 3. the weighted average difference of connection weights between all
	 * matching connections
	 * 
	 * @param network
	 *            the network to be measured against
	 * @param excessConnectionModifier
	 *            the weight of the number of excess genes between the two
	 *            networks in determining compatibility
	 * @param disjointConnectionModifier
	 *            the weight of the number of excess genes between the two
	 *            networks in determining compatibility
	 * @param averageWeightDifferenceModifier
	 *            the weight of the average weight differences of matching genes
	 *            between the two networks in determining compatibility
	 * @param normalizingFactor
	 *            the normalizing factor for network size
	 * @return the compatibility between this network and the specified network
	 */
	double getCompatibility(NeuralNetwork network,
			double excessConnectionModifier, double disjointConnectionModifier,
			double averageWeightDifferenceModifier, double normalizingFactor) {
		int excessConnections = 0;
		int disjointConnections = 0;
		int matchingConnections = 0;
		double totalWeightDifference = 0.0;
		int thisCurrentConnection = 0;
		int networkCurrentConnection = 0;
		while (true) {
			if (thisCurrentConnection >= connections.length) {
				excessConnections = network.connections.length
						- networkCurrentConnection;
				break;
			} else if (networkCurrentConnection >= network.connections.length) {
				excessConnections = connections.length - thisCurrentConnection;
				break;
			}
			if (connections[thisCurrentConnection].getInnovation() == network.connections[networkCurrentConnection]
					.getInnovation()) {
				matchingConnections++;
				totalWeightDifference += Math
						.abs(connections[thisCurrentConnection].getWeight()
								- network.connections[networkCurrentConnection]
										.getWeight());
				thisCurrentConnection++;
				networkCurrentConnection++;
			} else if (connections[thisCurrentConnection].getInnovation() < network.connections[networkCurrentConnection]
					.getInnovation()) {
				disjointConnections++;
				thisCurrentConnection++;
			} else {
				disjointConnections++;
				networkCurrentConnection++;
			}
		}

		return ((disjointConnectionModifier * disjointConnections) / normalizingFactor)
				+ ((excessConnectionModifier * excessConnections) / normalizingFactor)
				+ (averageWeightDifferenceModifier * (totalWeightDifference / matchingConnections));
	}

	/**
	 * Returns the compatibility between this network and the specified network,
	 * using the supplied constants and a normalizing factor equal to the number
	 * of connections in the larger network.
	 * 
	 * @param network
	 *            the network to be measured against
	 * @param excessConnectionModifier
	 *            the weight of the number of excess genes between the two
	 *            networks in determining compatibility
	 * @param disjointConnectionModifier
	 *            the weight of the number of excess genes between the two
	 *            networks in determining compatibility
	 * @param averageWeightDifferenceModifier
	 *            the weight of the average weight differences of matching genes
	 *            between the two networks in determining compatibility
	 * @return the compatibility between this network and the specified network
	 * 
	 * @see #getCompatibility(NeuralNetwork, double, double, double, double)
	 */
	double getCompatibility(NeuralNetwork network,
			double excessConnectionModifier, double disjointConnectionModifier,
			double averageWeightDifferenceModifier) {
		return getCompatibility(network, excessConnectionModifier,
				disjointConnectionModifier, averageWeightDifferenceModifier,
				Math.max(connections.length, network.connections.length));
	}

	/**
	 * The assigned species of this network. Contains {@code -1} until assigned.
	 */
	private int species = -1;

	/**
	 * Returns the assigned species of this network. If the network has not been
	 * assigned a species, returns {@code -1}.
	 * 
	 * @return the species of this network - {@code -1} if the network has not
	 *         been assigned a species
	 */
	public int getSpecies() {
		return species;
	}

	/**
	 * Sets this network's species to the supplied value.
	 * <p>
	 * Does not function if this network's species has already been set.
	 * 
	 * @param species
	 *            this network's new species
	 */
	void setSpecies(int species) {
		if (this.species == -1)
			this.species = species;
	}

	/**
	 * Constructs a {@code NeuralNetwork} with the given number of input nodes
	 * and output nodes and the given array of connections.
	 * 
	 * @param inputNodes
	 *            number of input nodes in this network
	 * @param outputNodes
	 *            number of output nodes in this network
	 * @param connections
	 *            the list of connections in this network
	 */
	private NeuralNetwork(int inputNodes, int outputNodes,
			Connection[] connections) {
		this.inputNodes = inputNodes;
		this.outputNodes = outputNodes;
		this.connections = connections;
	}

	/**
	 * Returns an offspring of this network and the specified network by means
	 * of crossover. Requires both parents to have set fitnesses.
	 * 
	 * @param network
	 *            the other parent of this crossover
	 * @param connectionDisableRate
	 *            the rate at which inherited connections in the offspring are
	 *            disabled if they were disabled in either parent
	 * @return an offspring of this network and the specified network by means
	 *         of crossover
	 */
	NeuralNetwork crossover(NeuralNetwork network, double connectionDisableRate) {

		/*
		 * Generate a crossed-over list of connections.
		 */
		Connection[] offspringConnections = new Connection[0];
		int thisCurrentConnection = 0;
		int networkCurrentConnection = 0;
		while (thisCurrentConnection < connections.length
				&& networkCurrentConnection < network.connections.length) {
			if (networkCurrentConnection >= network.connections.length
					|| connections[thisCurrentConnection].getInnovation() < network.connections[networkCurrentConnection]
							.getInnovation()) {
				if (fitness > network.fitness
						|| (fitness == network.fitness && Math.random() < 0.5)) {
					offspringConnections = Arrays.copyOf(offspringConnections,
							offspringConnections.length + 1);
					offspringConnections[offspringConnections.length - 1] = connections[thisCurrentConnection]
							.clone();
					if (!connections[thisCurrentConnection].getEnabled())
						if (Math.random() > connectionDisableRate)
							offspringConnections[offspringConnections.length - 1]
									.setEnabled(true);
				}
				thisCurrentConnection++;
			} else if (thisCurrentConnection >= connections.length
					|| network.connections[networkCurrentConnection]
							.getInnovation() > connections[thisCurrentConnection]
							.getInnovation()) {
				if (network.fitness > fitness
						|| (network.fitness == fitness && Math.random() < 0.5)) {
					offspringConnections = Arrays.copyOf(offspringConnections,
							offspringConnections.length + 1);
					offspringConnections[offspringConnections.length - 1] = network.connections[networkCurrentConnection]
							.clone();
					if (!network.connections[networkCurrentConnection]
							.getEnabled())
						if (Math.random() > connectionDisableRate)
							offspringConnections[offspringConnections.length - 1]
									.setEnabled(true);
				}
				networkCurrentConnection++;
			} else {
				offspringConnections = Arrays.copyOf(offspringConnections,
						offspringConnections.length + 1);
				if (Math.random() < 0.5)
					offspringConnections[offspringConnections.length - 1] = connections[thisCurrentConnection]
							.clone();
				else
					offspringConnections[offspringConnections.length - 1] = network.connections[networkCurrentConnection]
							.clone();
				if (!connections[thisCurrentConnection].getEnabled()
						|| !network.connections[networkCurrentConnection]
								.getEnabled())
					if (Math.random() < connectionDisableRate)
						offspringConnections[offspringConnections.length - 1]
								.setEnabled(false);
					else
						offspringConnections[offspringConnections.length - 1]
								.setEnabled(true);
				thisCurrentConnection++;
				networkCurrentConnection++;
			}
		}

		/*
		 * Create the network.
		 */
		return new NeuralNetwork(inputNodes, outputNodes, connections);
	}

	/**
	 * Generates a list of candidates for new connections in this network.
	 * <p>
	 * Connections are candidates if:<br>
	 * 1. they connect two previously unconnected nodes<br>
	 * 2. the connection does not result in a feedback loop through enabled
	 * nodes
	 * 
	 * @return a list of candidates for new connections in this network
	 */
	Connection[] getValidCandidateConnections() {

		/*
		 * Generate list of candidates based on
		 * "two previously unconnected nodes" requirement
		 */
		int newestNode = -1;
		for (Connection c : connections)
			newestNode = Math.max(newestNode,
					Math.max(c.getInputNode(), c.getOutputNode()));
		boolean[][] candidates = new boolean[newestNode + 1][newestNode + 1];
		for (int i = 0; i < candidates.length; i++)
			Arrays.fill(candidates[i], Boolean.TRUE);
		// Disable connections between the same node
		for (int n = 0; n < newestNode + 1; n++)
			candidates[n][n] = false;
		// Disable existing connections
		for (Connection c : connections)
			candidates[c.getInputNode()][c.getOutputNode()] = false;
		// Disable connections between two input nodes
		for (int n = 0; n < inputNodes; n++)
			for (int n2 = 0; n2 < inputNodes; n2++)
				candidates[n][n2] = false;
		// Disable connections between two output nodes
		for (int n = inputNodes; n < inputNodes + outputNodes; n++)
			for (int n2 = inputNodes; n2 < inputNodes + outputNodes; n2++)
				candidates[n][n2] = false;
		// Disable connections with outputs to input nodes
		for (int n = 0; n < newestNode + 1; n++)
			for (int n2 = 0; n2 < inputNodes; n2++)
				candidates[n][n2] = false;
		// Disable connections with inputs from output nodes
		for (int n = inputNodes; n < inputNodes + outputNodes; n++)
			for (int n2 = 0; n2 < newestNode + 1; n2++)
				candidates[n][n2] = false;

		/*
		 * Remove connections that generate feedback loops
		 */
		Connection[] candidateConnections = new Connection[0];
		Connection[] testConnections = Arrays.copyOf(connections,
				connections.length + 1);
		for (int in = 0; in < newestNode + 1; in++) {
			for (int out = 0; out < newestNode + 1; out++) {
				if (!candidates[in][out])
					continue;
				Connection connection = new Connection(in, out, true,
						Integer.MAX_VALUE);
				testConnections[testConnections.length - 1] = connection;
				boolean feedbackFound = false;
				for (int p = inputNodes; p < inputNodes + outputNodes; p++) {
					if (pullGeneratesFeedback(testConnections, p, new int[0])) {
						feedbackFound = true;
						break;
					}
				}
				if (!feedbackFound) {
					candidateConnections = Arrays.copyOf(candidateConnections,
							candidateConnections.length + 1);
					candidateConnections[candidateConnections.length - 1] = connection;
				}
			}
		}
		return candidateConnections;
	}

	/**
	 * Recursive method that checks for feedback in the supplied
	 * {@code Connection} tree from the specified node.
	 * <p>
	 * To check a network for feedback, call this method from each output node
	 * with a {@code pullHistory} of an empty {@code int} array (i.e.
	 * {@code new int[0]} ).<br>
	 * This method functions by checking if any of the connections below the
	 * specified node connect back to the specified node or any nodes in the
	 * {@code pullHistory}.
	 * 
	 * @param connections
	 *            the connection tree / network
	 * @param pullNode
	 *            the node to pull information from
	 * @param pullHistory
	 *            nodes that have already been accessed and should not be looped
	 *            back to
	 * @return whether or not the nodes in the tree from the specified node down
	 *         to the input nodes are present in {@code pullHistory}
	 */
	private boolean pullGeneratesFeedback(Connection[] connections,
			int pullNode, int[] pullHistory) {
		if (pullNode < inputNodes)
			return false;
		for (int i : pullHistory)
			if (pullNode == i)
				return true;
		pullHistory = Arrays.copyOf(pullHistory, pullHistory.length + 1);
		pullHistory[pullHistory.length - 1] = pullNode;
		for (Connection c : connections)
			if (c.getOutputNode() == pullNode
					&& pullGeneratesFeedback(connections, c.getInputNode(),
							pullHistory))
				return true;
		return false;
	}

	/**
	 * Returns the values pulled through the output nodes of the network given
	 * the specified input node values.
	 * <p>
	 * List of inputs is truncated if longer than the number of inputs and
	 * padded with {@code int} values of {@code 0.0} if shorter than the number
	 * of inputs.
	 * 
	 * @param inputs
	 *            the values of all input nodes - truncated or padded to the
	 *            correct number of inputs
	 * @return the values of all output nodes
	 */
	public double[] pullInformation(double[] inputs) {
		if (inputs.length != inputNodes)
			inputs = Arrays.copyOf(inputs, inputNodes);
		double[] outputs = new double[outputNodes];
		for (int o = 0; o < outputNodes; o++)
			outputs[o] = pullInformation(o + inputNodes, inputs);
		return outputs;
	}

	/**
	 * Recursive method that returns the pulled output of the specified node
	 * given the specified input node values.
	 * 
	 * @param pullNode
	 *            the node to pull information through the network from
	 * @param inputs
	 *            the values of all input nodes - may throw errors if not the
	 *            correct size
	 * @return the output of the specified {@code pullNode}
	 */
	private double pullInformation(int pullNode, double[] inputs) {
		if (pullNode < inputNodes)
			return inputs[pullNode];
		double inputSum = 0.0;
		for (Connection c : connections)
			if (c.getOutputNode() == pullNode)
				inputSum += pullInformation(c.getInputNode(), inputs);
		return transferInformation(inputSum);
	}

	/**
	 * Returns the output of the transfer function for nodes given the sum of
	 * inputs.
	 * <p>
	 * Method is equivalent to a modified sigmoid function:
	 * {@code 1/(1 - e^(-4.9*x))}
	 * 
	 * @param inputSum
	 *            the sum of inputs of the node to be transferred
	 * @return the output of the transfer function given the sum of inputs
	 */
	private double transferInformation(double inputSum) {
		return 1.0 / (1.0 - Math.pow(Math.E, -4.9 * inputSum));
	}

	@Override
	public String toString() {
		String string = "Fitness " + fitness + " - Species " + species
				+ " - Connections " + connections.length + ":";
		for (Connection c : connections)
			string = string.concat(" <" + c.getInputNode() + ", "
					+ c.getOutputNode() + ", " + c.getWeight() + ", "
					+ c.getEnabled() + ", " + c.getInnovation() + ">");
		return string;
	}
}
