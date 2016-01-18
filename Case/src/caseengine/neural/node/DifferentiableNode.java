package caseengine.neural.node;

import caseengine.function.DifferentiableFunction;

/**
 * A {@code FunctionalNode} that requires an activation function that is
 * differentiable.
 * 
 * @author Charlie Morley
 * @see {@link caseengine.function.DifferentiableFunction
 *      DifferentiableFunction}
 */
public class DifferentiableNode extends FunctionalNode {

	/**
	 * Constructs a new node with the specified differentiable activation
	 * function.
	 * 
	 * @param activationFunction
	 *            the differentiable function that determines the output of this
	 *            node
	 */
	public DifferentiableNode(DifferentiableFunction activationFunction) {
		super(activationFunction);
	}

}
