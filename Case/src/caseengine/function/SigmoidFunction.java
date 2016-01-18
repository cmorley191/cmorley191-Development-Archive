package caseengine.function;

/**
 * The sigmoid function - equal to the expression {@code 1 / (1 + }<span
 * style="font-style:italic;">e</span>{@code ^-x)}.
 * 
 * @author Charlie Morley
 *
 */
public final class SigmoidFunction implements DifferentiableFunction {

	/**
	 * {@inheritDoc}
	 * <p>
	 * The output of the {@code SigmoidFunction} is equal to the expression
	 * {@code 1 / (1 + }<span style="font-style:italic;">e</span>{@code ^-x)},
	 * where
	 * <ul>
	 * <li><span style="font-style:italic;">e</span> is the mathematical
	 * constant, Euler's Number</li>
	 * <li>{@code x} is the specified input</li>
	 * </ul>
	 */
	@Override
	public double function(double input) {
		return 1.0 / (1.0 + (Math.pow(Math.E, -input)));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The derivative of the {@code SigmoidFunction} is equal to the expression
	 * {@code y * (1 - y)}, where {@code y} is the result of the function at the
	 * specified input.
	 */
	@Override
	public double differentiate(double input) {
		double function = function(input);
		return function * (1.0 - function);
	}

}
