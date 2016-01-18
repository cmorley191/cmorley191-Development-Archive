package caseengine.cognition.executive;

/**
 * A decision-making protocol to be used in conjunction with reinforcement-based
 * cognition systems.
 * <p>
 * An executive makes choices based on sets of predicted reinforcement outcomes.
 * <p>
 * Executives can make choices in a wide variety of ways. A few examples:
 * <ul>
 * <li>The {@link GreedyExecutive} chooses the action with the highest
 * reinforcement, every time.</li>
 * <li>The {@link BoltzmannExecutive} makes choices coinciding with innovation:
 * sometimes it will concede to raw impulse, selecting the action with the
 * highest reinforcement, but other times it may choose an action randomly,
 * exploring new, good options that may not be the best as to find new paths to
 * higher reinforcement levels (where greedy action selection may only lead to
 * local maxima).</li>
 * </ul>
 * 
 * @author Charlie Morley
 *
 */
public abstract class Executive {

	/**
	 * Selects an index (usually corresponding to an action) from the specified
	 * set of predicted reinforcements based on internal protocols.
	 * <p>
	 * The default selection protocol returns 0, corresponding to the first
	 * reinforcement of the specified set. <br>
	 * An illegal set of reinforcements (for example, a set of length 0) results
	 * in a return value of -1.
	 * 
	 * @param reinforcements
	 *            the set of reinforcements corresponding to actions from which
	 *            the executive chooses
	 * @return the selected index corresponding to a reinforcement in the
	 *         specified set - if the specified set is illegal (for example, a
	 *         set of length 0), returns -1
	 */
	public int selectAction(double[] reinforcements) {
		return (reinforcements.length == 0) ? -1 : 0;
	}
}
