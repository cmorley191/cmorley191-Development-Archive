package caseengine.neural.node;

/**
 * A node for a neural network that outputs a {@code double} value. Subclasses
 * must override {@link #getOutput()}, the method for determining the output.
 * 
 * @author Charlie Morley
 *
 */
public abstract class Node {

	/**
	 * Returns the current output of this node in {@code double} precision.
	 * 
	 * @return this node's current output value
	 */
	public abstract double getOutput();
}
