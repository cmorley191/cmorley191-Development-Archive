package stennet.neural;

import caseengine.neural.node.ConnectableNode;

/**
 * A neural network node that holds a state. Nodes that do not hold state (such
 * as {@link caseengine.neural.node.FunctionalNode FunctionalNodes}) give output
 * that is directly dependent on the current state of their inputs. An
 * {@code UpdateNode} retrieves and uses the state of its inputs when it is
 * called to {@link #updateOutput()} and saves that output to be returned until
 * it is updated once more.
 * <p>
 * {@code UpdateNodes} are particularly useful in recurrent neural networks
 * where the collective state of a network is not constant over time, and
 * therefore will need to be recalculated many times to reach a "balanced"
 * state.
 * 
 * @author Charlie Morley
 *
 */
public class UpdateNode extends ConnectableNode {

	/**
	 * The current output of this node.
	 */
	private boolean output;

	/**
	 * Sets the output of this node.
	 * 
	 * @param output
	 *            this node's new output
	 */
	public void setOutput(boolean output) {
		this.output = output;
	}

	/**
	 * Updates the output of this node.<br>
	 * See {@link UpdateNode#getOutput()} for details on the different possible
	 * output values.
	 */
	public void updateOutput() {
		output = getNetInput() >= 0;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The output of an {@code UpdateNode} is constant until the node is
	 * {@link #updateOutput() updated}. Returns 1 if the net input (as supplied
	 * by {@link ConnectableNode#getNetInput()}) was greater than or equal to 0
	 * at the time of update, and 0 otherwise.
	 */
	@Override
	public double getOutput() {
		return (output) ? 1 : 0;
	}

}
