package caseengine.cognition.executive;

public class LearnFirstExecutive extends GreedyExecutive {
	
	private int reinforcementCheck = 0;
	private final int eachCount;
	
	public LearnFirstExecutive(int eachCount) {
		this.eachCount = eachCount;
	}
	
	@Override
	public int selectAction(double[] reinforcements) {
		if (reinforcementCheck >= reinforcements.length * eachCount)
			return super.selectAction(reinforcements);
		reinforcementCheck++;
		return (reinforcementCheck - 1) % reinforcements.length;
	}
}
