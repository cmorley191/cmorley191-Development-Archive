package caseengine.neural.network;

import java.util.ArrayList;
import java.util.Arrays;

import caseengine.neural.node.ConnectableNode;
import caseengine.neural.node.Node;

/**
 * A feed-forward neural network consisting of ordered "layers" of
 * {@link caseengine.neural.node.ConnectableNode ConnectableNodes}, in which
 * every node in a layer is connected to every node in the following layer and
 * preceding layer. Nodes are not connected to other nodes in the same layer.
 * Other classes may have references to nodes in this network, and therefore
 * other nodes may be connected to the nodes in this network.
 * Infinitely-recursive loops might be formed by external function - the network
 * makes no guarantees on external function in respect to this.
 * <p>
 * Each {@code LayeredNetwork} contains an input layer followed by at least one
 * general layer. Specific nodes may not be added to the input layer - rather
 * the network adds its own "Input Nodes" of which it manages the output. The
 * output of the input nodes are 0 except during a "pull" -
 * {@link #pullOutputs(double[], int)} will set the values specified to the
 * corresponding input nodes, and return the output of the nodes in the
 * specified general layer.
 * <p>
 * Each network contains a minimum of one general layer. Only nodes specifically
 * added appear in each layer, connected to nodes in adjacent layers using
 * {@link ConnectionScheme ConnectionSchemes}.
 * 
 * @author Charlie Morley
 *
 */
public class LayeredNetwork {

	/**
	 * The general layers of this network - contains {@code ConnectableNodes}
	 * that are set externally.
	 */
	private ArrayList<ArrayList<ConnectableNode>> network = new ArrayList<ArrayList<ConnectableNode>>();

	/**
	 * The input layer of this network - contains {@code InputNodes} used to
	 * pull input through the network in {@link #pullOutputs(double[], int)}.
	 */
	private ArrayList<InputNode> inputs = new ArrayList<InputNode>();

	/**
	 * Constructs a {@code LayeredNetwork} with one input layer and one general
	 * layer.
	 */
	public LayeredNetwork() {
		this(1);
	}

	/**
	 * Constructs a {@code LayeredNetwork} with one input layer and the
	 * specified number of general layers. If a number of general layers less
	 * than one is specified, the network is constructed with one general layer.
	 * 
	 * @param layerCount
	 *            the number of general layers in this network - set to one if
	 *            less than one
	 */
	public LayeredNetwork(int layerCount) {
		if (layerCount < 1)
			layerCount = 1;
		for (; layerCount > 0; layerCount--)
			network.add(new ArrayList<ConnectableNode>());
	}

	/**
	 * Returns the current number of general layers in this network.
	 * 
	 * @return the number of general layers in this {@code LayeredNetwork}
	 */
	public int getLayerCount() {
		return network.size();
	}

	/**
	 * Appends a new, empty layer of
	 * {@link caseengine.neural.node.ConnectableNode ConnectableNodes} to this
	 * network's set of general layers.
	 */
	public void addLayer() {
		network.add(new ArrayList<ConnectableNode>());
	}

	/**
	 * Returns a new list containing the nodes currently in the specified
	 * general layer.
	 * 
	 * @param index
	 *            the index of the general layer to be returned - the first
	 *            layer after the input layer is index 0
	 * @return a new list containing the nodes in this network's general layer
	 *         at the specified index
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of the range of general layers
	 *             {@code (index < 0 || index >= getLayerCount())}
	 */
	public ArrayList<ConnectableNode> getLayer(int index)
			throws IndexOutOfBoundsException {
		if (index < 0 || index >= network.size())
			throw new IndexOutOfBoundsException();
		else {
			ArrayList<ConnectableNode> returnList = new ArrayList<ConnectableNode>();
			returnList.addAll(network.get(index));
			return returnList;
		}
	}

	/**
	 * Adds a new node to this network's input layer and connects it to the
	 * nodes currently in general layer of index 0 using the specified
	 * connection scheme.
	 * 
	 * @param scheme
	 *            the scheme with which the new input node should be connected
	 *            to the first general layer's nodes
	 * @throws NullPointerException
	 *             if {@code scheme} is null
	 */
	public void addInput(ConnectionScheme scheme) throws NullPointerException {
		if (scheme == null)
			throw new NullPointerException();
		InputNode newNode = new InputNode();
		for (ConnectableNode output : network.get(0))
			output.setConnection(newNode,
					scheme.generateWeight(newNode, output));
		inputs.add(newNode);
	}

	/**
	 * Returns a new list containing the nodes in this network's input layer.
	 * <p>
	 * Note that the only use for this method is for specifically setting a
	 * connection weight from a node in this layer to another
	 * {@link caseengine.neural.node.ConnectableNode ConnectableNode} in this
	 * network, in which a reference to the input node is required. The nodes in
	 * the input layer are of an internal type and only output 0 except during a
	 * "pull" (See {@link #pullOutputs(double[], int)} ).
	 * 
	 * @return a new list containing the nodes in this network's input layer
	 */
	public ArrayList<Node> getInputs() {
		ArrayList<Node> returnList = new ArrayList<Node>();
		returnList.addAll(inputs);
		return returnList;
	}

	/**
	 * Adds the specified node to the end of the specified general layer and
	 * connects it to the nodes in adjacent layers using the specified
	 * connection schemes.
	 * 
	 * @param node
	 *            the node to be added to the specified general layer of this
	 *            network
	 * @param layerIndex
	 *            the index of the general layer in this network to which the
	 *            specified node should be added
	 * @param inputScheme
	 *            the scheme with which the new node should be connected to
	 *            nodes in the layer preceding the specified layer
	 * @param outputScheme
	 *            the scheme with which the new node should be connected to
	 *            nodes in the layer following the specified layer - unused if
	 *            the specified layer is the last layer currently in this
	 *            network (layerIndex == getLayerCount() - 1)
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of the range of general layers
	 *             {@code (index < 0 || index >= getLayerCount())}
	 * @throws NullPointerException
	 *             if {@code node}, {@code inputScheme}, or {@code outputScheme}
	 *             are null
	 */
	public void addNode(ConnectableNode node, int layerIndex,
			ConnectionScheme inputScheme, ConnectionScheme outputScheme)
			throws IndexOutOfBoundsException, NullPointerException {
		if (node == null || inputScheme == null || outputScheme == null)
			throw new NullPointerException();
		if (layerIndex < 0 || layerIndex >= network.size())
			throw new IndexOutOfBoundsException();
		for (Node input : (layerIndex == 0) ? inputs : network
				.get(layerIndex - 1))
			node.setConnection(input, inputScheme.generateWeight(input, node));
		if (layerIndex < network.size() - 1)
			for (ConnectableNode output : network.get(layerIndex + 1))
				output.setConnection(node,
						outputScheme.generateWeight(node, output));
		network.get(layerIndex).add(node);
	}

	/**
	 * Adds the specified node to the end of the specified general layer and
	 * connects it to the nodes in adjacent layers using the specified
	 * connection scheme.
	 * 
	 * @param node
	 *            the node to be added to the specified general layer of this
	 *            network
	 * @param layerIndex
	 *            the index of the general layer in this network to which the
	 *            specified node should be added
	 * @param connectionScheme
	 *            the scheme with which the new node should be connected to
	 *            nodes in the layer preceding and the layer following the
	 *            specified layer
	 * 
	 * @see #addNode(ConnectableNode, int, ConnectionScheme, ConnectionScheme)
	 */
	public void addNode(ConnectableNode node, int layerIndex,
			ConnectionScheme connectionScheme) {
		addNode(node, layerIndex, connectionScheme, connectionScheme);
	}

	/**
	 * The default connection scheme assigned to new connections formed by the
	 * default functions {@link #addNode(ConnectableNode, int)} and
	 * {@link #addInput()}. Contains a {@link ConstantWeightScheme} that always
	 * sets a weight of 0 to new connections.
	 */
	public static final ConnectionScheme DEFAULT_CONNECTION_SCHEME = new ConstantWeightScheme(
			0);

	/**
	 * Adds a new node to this network's input layer and connects it to the
	 * nodes currently in general layer of index 0 using the default connection
	 * scheme: {@link #DEFAULT_CONNECTION_SCHEME}.
	 * 
	 * @see #addInput(ConnectionScheme)
	 */
	public void addInput() {
		addInput(DEFAULT_CONNECTION_SCHEME);
	}

	/**
	 * Adds the specified node to the end of the specified general layer and
	 * connects it to the nodes in adjacent layers using the default connection
	 * scheme: {@link #DEFAULT_CONNECTION_SCHEME}.
	 * 
	 * @param node
	 *            the node to be added to the specified general layer of this
	 *            network
	 * @param layerIndex
	 *            the index of the general layer in this network to which the
	 *            specified node should be added
	 * @see #addNode(ConnectableNode, int, ConnectionScheme)
	 */
	public void addNode(ConnectableNode node, int layerIndex) {
		addNode(node, layerIndex, DEFAULT_CONNECTION_SCHEME);
	}

	/**
	 * Returns an array containing the
	 * {@link caseengine.neural.node.ConnectableNode#getOutput() outputs} of the
	 * nodes in the specified general layer given that the nodes in this
	 * network's input layer output the specified values.
	 * <p>
	 * Conceptually, this method "pulls" the specified values through this
	 * network's input nodes and returns the output values (making no guarantees
	 * about the effects of externally connected inputs or other node
	 * functions). Mechanically, it sets the input nodes' outputs equal to the
	 * specified values, retrieves the required outputs, then resets the input
	 * nodes' outputs to 0.
	 * 
	 * @param inputs
	 *            the values to be "pulled" through this network's input nodes
	 *            and through the network structure to the specified layer
	 * @param outputLayer
	 *            the index of the general layer of this network whose nodes'
	 *            outputs are returned after the specified inputs are pulled
	 *            through the preceding network structure
	 * @return an array containing the outputs of the specified general layer's
	 *         nodes when the input nodes are set to the specified values
	 * @throws IndexOutOfBoundsException
	 *             if the specified output layer index is out of the range of
	 *             general layers
	 *             {@code (index < 0 || index >= getLayerCount())}
	 * @throws NullPointerException
	 *             if {@code inputs} is null
	 */
	public double[] pullOutputs(double[] inputs, int outputLayer)
			throws IndexOutOfBoundsException, NullPointerException {
		if (inputs == null)
			throw new NullPointerException();
		if (outputLayer >= network.size())
			throw new IndexOutOfBoundsException();
		inputs = Arrays.copyOf(inputs, this.inputs.size());
		for (int i = 0; i < this.inputs.size(); i++)
			this.inputs.get(i).setOutput(inputs[i]);
		ArrayList<ConnectableNode> outputs = network.get(outputLayer);
		double[] values = new double[outputs.size()];
		for (int i = 0; i < outputs.size(); i++)
			values[i] = outputs.get(i).getOutput();
		for (InputNode node : this.inputs)
			node.resetOutput();
		return values;
	}

	/**
	 * Returns an array containing the
	 * {@link caseengine.neural.node.ConnectableNode#getOutput() outputs} of the
	 * nodes in the last general layer given that the nodes in this network's
	 * input layer output the specified values.
	 * 
	 * 
	 * @param inputs
	 *            the values to be "pulled" through this network's input nodes
	 *            and through the network structure to the specified layer
	 * @return an array containing the outputs of the last general layer's nodes
	 *         when the input nodes are set to the specified values - the last
	 *         general layer meaning the general layer at index
	 *         {@code (getLayerCount() - 1)}
	 * @see #pullOutputs(double[], int)
	 */
	public double[] pullOutputs(double[] inputs) {
		return pullOutputs(inputs, network.size() - 1);
	}

	/**
	 * Returns an array containing the
	 * {@link caseengine.neural.node.ConnectableNode#getNetInput() net inputs}
	 * of the nodes in the specified general layer given that the nodes in this
	 * network's input layer output the specified values.
	 * <p>
	 * Conceptually, this method "pulls" the specified values through this
	 * network's input nodes and returns the net input values (making no
	 * guarantees about the effects of externally connected inputs or other node
	 * functions). Mechanically, it sets the input nodes' outputs equal to the
	 * specified values, retrieves the required net inputs, then resets the
	 * input nodes' outputs to 0.
	 * 
	 * @param inputs
	 *            the values to be "pulled" through this network's input nodes
	 *            and through the network structure to the specified layer
	 * @param outputLayer
	 *            the index of the general layer of this network whose nodes'
	 *            net inputs are returned after the specified inputs are pulled
	 *            through the preceding network structure
	 * @return an array containing the net inputs of the specified general
	 *         layer's nodes when the input nodes are set to the specified
	 *         values
	 * @throws IndexOutOfBoundsException
	 *             if the specified output layer index is out of the range of
	 *             general layers
	 *             {@code (index < 0 || index >= getLayerCount())}
	 * @throws NullPointerException
	 *             if {@code inputs} is null
	 */
	public double[] pullNetInputs(double[] inputs, int outputLayer) {
		if (inputs == null)
			throw new NullPointerException();
		if (outputLayer >= network.size())
			throw new IndexOutOfBoundsException();
		inputs = Arrays.copyOf(inputs, this.inputs.size());
		for (int i = 0; i < this.inputs.size(); i++)
			this.inputs.get(i).setOutput(inputs[i]);
		ArrayList<ConnectableNode> outputs = network.get(outputLayer);
		double[] values = new double[outputs.size()];
		for (int i = 0; i < outputs.size(); i++)
			values[i] = outputs.get(i).getNetInput();
		for (InputNode node : this.inputs)
			node.resetOutput();
		return values;
	}

	/**
	 * A utility that assigns a connection's weight based on internal mechanics
	 * and/or the properties of the input and output node of the connection.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public abstract static class ConnectionScheme {

		/**
		 * Returns the weight of the connection from the specified input node to
		 * the specified output node based on this scheme's internal mechanics.
		 * 
		 * @param input
		 *            the node sending data into the connection
		 * @param output
		 *            the node receiving data from the connection
		 * @return the assigned weight of the connection
		 */
		public abstract double generateWeight(Node input, ConnectableNode output);
	}

	/**
	 * A weight scheme that assigns pseudorandomly chosen weights to node
	 * connections according to a specified range.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public static class RandomWeightScheme extends ConnectionScheme {

		/**
		 * The minimum value of the randomly selected weight.
		 */
		private double minimum;

		/**
		 * The length of the range of weights that may be selected.
		 */
		private double difference;

		/**
		 * Constructs a new randomizing scheme with the specified range.
		 * <p>
		 * Generated weights may be any value greater than or equal to the
		 * specified minimum and less than the specified maximum.
		 * 
		 * @param minimum
		 *            the minimum value of a weight generated by this scheme
		 *            (inclusive)
		 * @param maximum
		 *            the maximum value of a weight generated by this scheme
		 *            (exclusive)
		 */
		public RandomWeightScheme(double minimum, double maximum) {
			this.minimum = minimum;
			difference = maximum - minimum;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The weight assigned by a {@code RandomWeightScheme} is a
		 * pseudorandomly chosen number with (approximately) uniform
		 * distribution in the scheme's range (specified in
		 * {@link RandomWeightScheme#RandomWeightScheme(double, double) the
		 * constructor}) corresponding with the distribution of
		 * {@link java.lang.Math#random()}.
		 */
		@Override
		public double generateWeight(Node input, ConnectableNode output) {
			return (Math.random() * difference) + minimum;
		}
	}

	/**
	 * A weight scheme that assigns an unchanging value as the weight for node
	 * connections.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public final static class ConstantWeightScheme extends ConnectionScheme {

		/**
		 * The weight to be assigned to node connections.
		 */
		private final double weight;

		/**
		 * Constructs a new scheme that always assigns the specified weight.
		 * 
		 * @param weight
		 *            the weight to be assigned to node connections
		 */
		public ConstantWeightScheme(double weight) {
			this.weight = weight;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The weight assigned by a {@code ConstantWeightScheme} is always equal
		 * to the weight assigned in
		 * {@link ConstantWeightScheme#ConstantWeightScheme(double) the
		 * constructor}.
		 */
		@Override
		public double generateWeight(Node input, ConnectableNode output) {
			return weight;
		}
	}

	/**
	 * A weight scheme that assigns a modifiable instance value as the weight
	 * for node connections.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public static class DynamicWeightScheme extends ConnectionScheme {

		/**
		 * The weight to be assigned to node connections.
		 */
		private double weight;

		/**
		 * Constructs a scheme with the specified initial weight to be assigned
		 * to node connections.
		 * 
		 * @param initialWeight
		 *            the initial value to be assigned as the weight of node
		 *            connections
		 */
		public DynamicWeightScheme(double initialWeight) {
			weight = initialWeight;
		}

		/**
		 * Sets the value to be assigned as the weight of node connections
		 * 
		 * @param weight
		 *            the new value to be assigned as the weight of node
		 *            connections
		 */
		public void setWeight(double weight) {
			this.weight = weight;
		}

		/**
		 * Returns the current weight to be assigned to node connections.
		 * 
		 * @return the current value to be assigned as the weight of node
		 *         connections
		 */
		public double getWeight() {
			return weight;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The weight returned by a {@code DynamicWeightScheme} is equal to the
		 * current value stored, accessible by {@link #getWeight()} and
		 * modifiable by {@link #setWeight(double)}.
		 */
		@Override
		public double generateWeight(Node input, ConnectableNode output) {
			return weight;
		}

	}

	/**
	 * The type of node that makes up a {@code LayeredNetwork's} input layer.
	 * Outputs a stored value - {@link InputNode#output} - that contains 0 on
	 * construction and is modifiable and can be reset to 0 at any time.
	 * 
	 * @author Charlie Morley
	 *
	 */
	private final static class InputNode extends Node {

		/**
		 * The value to be returned by {@link #getOutput()}.
		 */
		private double output;

		/**
		 * Constructs a new node that outputs 0.
		 */
		private InputNode() {
			output = 0;
		}

		/**
		 * Sets the output of this node to the specified value.
		 * 
		 * @param output
		 *            the new output of this node
		 */
		private void setOutput(double output) {
			this.output = output;
		}

		/**
		 * Resets the output of this node to 0.
		 */
		private void resetOutput() {
			this.output = 0;
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The output of an {@code InputNode} is 0 unless its containing
		 * {@link LayeredNetwork} is executing a "pull" (See
		 * {@link LayeredNetwork#pullOutputs(double[], int)}).
		 */
		@Override
		public double getOutput() {
			return output;
		}

	}
}
