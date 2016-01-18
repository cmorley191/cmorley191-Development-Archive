package stennet.neural;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A neural network used for storing and approximating states. A
 * {@code HopfieldNetwork} is made up of a set of {@link UpdateNode UpdateNodes}
 * . Once the network has been trained, the state of the nodes can be set to
 * reflect a pattern, and continuous updates of the nodes will result in the
 * network converging to one of the trained states.
 * 
 * @author Charlie Morley
 *
 */
public class HopfieldNetwork {

	/**
	 * The set of nodes in this network.
	 */
	private ArrayList<UpdateNode> nodes = new ArrayList<UpdateNode>();

	/**
	 * Constructs a new network with the given number of nodes.
	 * 
	 * @param nodeCount
	 *            the number of nodes in this network
	 * @throws IllegalArgumentException
	 *             if {@code nodeCount} is less than 1
	 */
	public HopfieldNetwork(int nodeCount) throws IllegalArgumentException {
		if (nodeCount < 1)
			throw new IllegalArgumentException();

		/*
		 * Generate the specified number of nodes and connect each node to every
		 * other node in the network
		 */
		for (; nodeCount > 0; nodeCount--) {
			UpdateNode newNode = new UpdateNode();
			for (UpdateNode node : nodes) {
				node.setConnection(newNode, 0);
				newNode.setConnection(node, 0);
			}
			nodes.add(newNode);
		}
	}

	/**
	 * Returns the number of nodes in this network.
	 * 
	 * @return the number of nodes in this network
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	/**
	 * Adjusts the weights of the connections in this network so that the
	 * specified pattern is one that the network may converge to during
	 * recollection.
	 * 
	 * @param pattern
	 *            the set of node values to be trained into the network
	 * @throws NullPointerException
	 *             if {@code pattern} is null
	 * @throws IllegalArgumentException
	 *             if {@code pattern} has a length greater or less than the
	 *             number of nodes in this network
	 */
	public void trainPattern(boolean[] pattern) throws NullPointerException,
			IllegalArgumentException {
		if (pattern == null)
			throw new NullPointerException();
		if (pattern.length != nodes.size())
			throw new IllegalArgumentException();

		/*
		 * Give two nodes, i and j, in the network, the correct weight between
		 * them is the sum (for all trained patterns) of the result of the
		 * following equation, where Vi is the value of node i in a pattern:
		 * 
		 * (2*Vi - 1) * (2*Vj - 1)
		 */
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				int weightChange = ((2 * ((pattern[i]) ? 1 : 0)) - 1)
						* ((2 * ((pattern[j]) ? 1 : 0)) - 1);
				nodes.get(i)
						.setConnection(
								nodes.get(j),
								nodes.get(i).getConnection(nodes.get(j))
										+ weightChange);
				nodes.get(j)
						.setConnection(
								nodes.get(i),
								nodes.get(j).getConnection(nodes.get(i))
										+ weightChange);
			}
		}
	}

	/**
	 * Returns the pattern stored in this network that is nearest to the
	 * specified pattern.
	 * <p>
	 * The method sets the network's state to the specified pattern, and allows
	 * it to iterate until it converges to one of the trained patterns.
	 * 
	 * @param pattern
	 *            the pattern being compared
	 * @return a new array containing the pattern stored in the network that is
	 *         nearest to {@code pattern}
	 * @throws NullPointerException
	 *             if {@code pattern} is null
	 * @throws IllegalArgumentException
	 *             if {@code pattern} has a length greater or less than the
	 *             number of nodes in this network
	 */
	public boolean[] getNearestPattern(boolean[] pattern)
			throws NullPointerException, IllegalArgumentException {
		if (pattern == null)
			throw new NullPointerException();
		if (pattern.length != nodes.size())
			throw new IllegalArgumentException();

		for (int i = 0; i < pattern.length; i++)
			nodes.get(i).setOutput(pattern[i]);

		/*
		 * Iterate and Converge
		 */
		ArrayList<UpdateNode> workingNodeList = new ArrayList<UpdateNode>();
		workingNodeList.addAll(nodes);
		boolean changeOccurred = true;
		while (changeOccurred) {
			changeOccurred = false;
			Collections.shuffle(workingNodeList);
			for (UpdateNode node : workingNodeList) {
				double previousOutput = node.getOutput();
				node.updateOutput();
				if (!changeOccurred && node.getOutput() != previousOutput)
					changeOccurred = true;
			}
		}

		boolean[] resultPattern = new boolean[nodes.size()];
		for (int i = 0; i < nodes.size(); i++)
			resultPattern[i] = nodes.get(i).getOutput() == 1;
		return resultPattern;
	}
}
