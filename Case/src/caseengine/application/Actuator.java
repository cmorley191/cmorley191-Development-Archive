package caseengine.application;

import java.util.EnumSet;

/**
 * An {@code Actuator} provides an output link from intelligence systems to
 * their environments, whereby the intelligence system may cause effects in the
 * environment by sending an action to an actuator to be performed.
 * <p>
 * An {@code Actuator}'s type parameter is the {@code enum} that defines the set
 * of distinguishable actions able to be performed by the actuator.
 * <p>
 * An actuator to be integrated in an intelligence system must be able to list
 * the set of actions currently able to be performed by the actuator, as well as
 * perform specific actions when prompted by the intelligence system.
 * 
 * @author Charlie Morley
 *
 */
public interface Actuator<ActionType extends Enum<ActionType>> {

	/**
	 * Returns the current set of actions available to this actuator.
	 * 
	 * @return a set containing the actions currently available to this actuator
	 */
	public EnumSet<ActionType> getActionSet();

	/**
	 * Performs the specified action, if the action is able to be performed by
	 * this actuator (that is, if the action appears in the set returned by
	 * {@link #getActionSet()}). Returns whether or not the action was
	 * successfully performed.
	 * 
	 * @param action
	 *            the action to be performed by this actuator
	 * @return {@code true} if the specified action was successfully performed,
	 *         {@code false} if the specified action was not successfully
	 *         performed (possibly due to it not being available to this
	 *         actuator)
	 */
	public boolean performAction(Enum<?> action);
}
