package caseengine.function;

/**
 * A mathematical function. Implementing classes must override
 * {@link #function(double)}, the method that provides the result when given an
 * input.
 * 
 * @author Charlie Morley
 *
 */
public interface Function {

	/**
	 * Returns the result of this function given the specified input.
	 * 
	 * @param input
	 *            the input for the function
	 * @return the result that corresponds to the specified input
	 */
	public double function(double input);
}
