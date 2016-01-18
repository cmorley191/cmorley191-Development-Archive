package caseengine.function;

import java.awt.geom.Point2D;

/**
 * A linear function - a function with a constant slope and offset.
 * 
 * @author Charlie Morley
 *
 */
public final class LinearFunction implements DifferentiableFunction {

	/**
	 * The constant slope of this linear function.
	 */
	private final double slope;

	/**
	 * The constant offset of this linear function.
	 */
	private final double offset;

	/**
	 * Constructs a function with the specified slope and offset, per the
	 * equation {@code y = m*x + b}.
	 * 
	 * @param slope
	 *            the slope of this function, {@code m}
	 * @param offset
	 *            the offset of this function, {@code b}
	 */
	public LinearFunction(double slope, double offset) {
		this.slope = slope;
		this.offset = offset;
	}

	/**
	 * Constructs a function from the specified slope and point, per the
	 * equation {@code y - y0 = m*(x - x0)}.
	 * <p>
	 * The function details are converted to slope-intercept form before being
	 * stored. See {@link #LinearFunction(double, double)}.
	 * 
	 * @param slope
	 *            the slope of this function, {@code m}
	 * @param point
	 *            a solution point to this function, {@code (x0, y0)}
	 */
	public LinearFunction(double slope, Point2D point) {
		this(slope, point.getY() - (slope * point.getX()));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The output of a {@code LinearFunction} is the expression {@code m*x + b}
	 * where <br>
	 * <ul>
	 * <li> {@code m} is the slope</li>
	 * <li> {@code x} is the specified input
	 * <li> {@code b} is the offset of the function
	 * </ul>
	 */
	@Override
	public double function(double input) {
		return (slope * input) + offset;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The derivative of a {@code LinearFunction} is always equal to the slope of
	 * the function.
	 */
	@Override
	public double differentiate(double input) {
		return slope;
	}

}
