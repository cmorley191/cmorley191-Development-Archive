package caseengine.neural.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A neural network node that accepts weighted input from other nodes to
 * determine its output. No guarantees are made about the use of the net
 * weighted input, especially in determining the output.
 * <p>
 * Connections from other nodes cannot be removed. Rather, the weight of the
 * connection from a node should simply be set to 0 using
 * {@link #setConnection(Node, double) setConnection}.
 * 
 * @author Charlie Morley
 *
 */
public abstract class ConnectableNode extends Node {

	/**
	 * The set of input connections to this node. Maps the node sending input to
	 * a weight by which the input is multiplied.
	 */
	private HashMap<Node, Double> connections = new HashMap<Node, Double>();

	/**
	 * Sets the weight of the connection from the specified node's output to
	 * this node's input. If the connection does not yet exist,
	 * 
	 * @param input
	 *            the node sending input to this node
	 * @param weight
	 *            the weight of the connection from the specified node to this
	 *            node
	 */
	public final void setConnection(Node input, double weight) {
		connections.put(input, weight);
	}

	/**
	 * Returns the weight of the connection from the specified node. If no
	 * connection exists, returns 0.
	 * 
	 * @param input
	 *            the connected input node being retrieved
	 * @return the weight of the connection from the specified node's output to
	 *         this node's input
	 */
	public final double getConnection(Node input) {
		Double weight = connections.get(input);
		if (weight == null)
			return 0;
		return weight;
	}

	/**
	 * Returns a new {@code ArrayList} containing the sets of
	 * {@code <Node, Double>} pairs that represent the input connections of this
	 * node.
	 * 
	 * @return a new {@code ArrayList} containing {@code <Node, Double>} pairs
	 *         where the {@code Nodes} are the inputs and the {@code Doubles}
	 *         are the weights of the connections
	 */
	public ArrayList<Entry<Node, Double>> getConnections() {
		ArrayList<Entry<Node, Double>> returnSet = new ArrayList<Entry<Node, Double>>();
		returnSet.addAll(connections.entrySet());
		return returnSet;
	}

	/**
	 * Retrieves and calculates the sum of all the weighted inputs to this node.
	 * 
	 * @return the sum of all of this node's inputs' current outputs, multiplied
	 *         by their respective weights
	 */
	public double getNetInput() {
		double netInput = 0.0;
		for (Entry<Node, Double> entry : connections.entrySet())
			netInput += entry.getKey().getOutput() * entry.getValue();
		return netInput;
	}
}
