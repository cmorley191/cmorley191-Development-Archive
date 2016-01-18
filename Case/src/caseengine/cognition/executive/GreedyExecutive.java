package caseengine.cognition.executive;

/**
 * An executive that chooses the action with the highest predicted
 * reinforcement.
 * 
 * @author Charlie Morley
 *
 */
public class GreedyExecutive extends Executive {

	/**
	 * {@inheritDoc}
	 * <p>
	 * The selection of a {@code GreedyExecutive} is the action with the highest
	 * predicted reinforcement. If multiple actions match the maximum predicted
	 * reinforcement, the selection is the first action appearing in the set
	 * with the maximum predicted reinforcement.
	 */
	@Override
	public int selectAction(double[] reinforcements) {
		if (super.selectAction(reinforcements) == -1)
			return -1;
		int selection = 0;
		for (int i = 1; i < reinforcements.length; i++)
			if (reinforcements[i] > reinforcements[selection])
				selection = i;
		return selection;
	}

}
