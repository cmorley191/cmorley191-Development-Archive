package caseengine.neural.node;

import caseengine.function.Function;

/**
 * A {@code ConnectableNode} whose output is the sum of the weighted inputs as
 * specified by {@link ConnectableNode#getNetInput()} modified by an activation
 * {@link caseengine.function.Function Function}.
 * 
 * @author Charlie Morley
 */
public class FunctionalNode extends ConnectableNode {

	/**
	 * The activation function that determines the output of this node.
	 */
	private Function activationFunction;

	/**
	 * Constructs a new node with the specified activation function.
	 * 
	 * @param activationFunction
	 *            the function that determines the output of this node
	 */
	public FunctionalNode(Function activationFunction) {
		if (activationFunction == null)
			throw new NullPointerException();
		else
			this.activationFunction = activationFunction;
	}

	/**
	 * Returns the activation function of this node.
	 * 
	 * @return this node's activation function
	 */
	public Function getActivationFunction() {
		return activationFunction;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The output of a {@code FunctionalNode} is the net input of the node as
	 * specified by {@link ConnectableNode#getNetInput()} fed through this
	 * node's activation function.
	 */
	@Override
	public double getOutput() {
		return activationFunction.function(getNetInput());
	}

}
