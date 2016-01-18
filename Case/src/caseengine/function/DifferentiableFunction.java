package caseengine.function;

/**
 * A function that is differentiable - it can provide a resultant derivative for
 * a given input. Implementing classes must override
 * {@link #differentiate(double)}, the method that provides the resultant
 * derivative at a specified input.
 * 
 * @author Charlie Morley
 *
 */
public interface DifferentiableFunction extends Function {

	/**
	 * Returns the derivative of this function at the specified input.
	 * 
	 * @param input
	 *            the input value being differentiated
	 * @return the resultant derivative that corresponds to the specified input
	 */
	public double differentiate(double input);
}
