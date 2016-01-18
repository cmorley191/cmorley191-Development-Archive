package caseengine.cognition.executive;

import java.util.ArrayList;
import java.util.HashMap;

public class ProbableExecutive extends Executive {

	private double nongreedyModifier;
	private final GreedyExecutive greedy = new GreedyExecutive();
	private ArrayList<Double> history = new ArrayList<Double>();
	private final double historyLimit;

	public ProbableExecutive(double nongreedyModifier, int historyLimit) {
		this.nongreedyModifier = nongreedyModifier;
		this.historyLimit = historyLimit;
	}

	public void addHistory(double reinforcement) {
		history.add(reinforcement);
		if (history.size() > historyLimit)
			history.remove(0);
	}

	private double getNonGreedyChance() {
		if (history.size() == 0)
			return 0;
		HashMap<Double, Integer> reinforcementCounts = new HashMap<Double, Integer>();
		for (Double reinforcement : history) {
			if (reinforcementCounts.containsKey(reinforcement))
				reinforcementCounts.put(reinforcement,
						reinforcementCounts.get(reinforcement) + 1);
			else
				reinforcementCounts.put(reinforcement, 1);
		}
		double typeCount = 0;
		double total = 0;
		for (Integer count : reinforcementCounts.values()) {
			typeCount++;
			total += count;
		}
		return (total / typeCount) / historyLimit;
	}

	private static double NON_GREEDY_GIVES_ABSOLUTE_RANDOM_CHANCE = 1;

	@Override
	public int selectAction(double[] reinforcements) {
		if (super.selectAction(reinforcements) == -1)
			return -1;
		nongreedyModifier *= 0.99999999;
		if (Math.random() < getNonGreedyChance() * nongreedyModifier) {
			if (Math.random() < NON_GREEDY_GIVES_ABSOLUTE_RANDOM_CHANCE)
				return (int) (Math.random() * reinforcements.length);
			double total = 0;
			for (double reinforcement : reinforcements)
				total += reinforcement;
			double random = Math.random() * total;
			for (int i = 0; i < reinforcements.length; i++) {
				random -= reinforcements[i];
				if (random < 0)
					return i;
			}
			return reinforcements.length - 1;
		} else
			return greedy.selectAction(reinforcements);
	}

}
