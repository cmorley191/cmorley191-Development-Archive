package caseengine.cognition.executive;

public class InnovatorExecutive extends GreedyExecutive {
	
	private double nongreedyChance;
	
	public InnovatorExecutive(double nongreedyChance) {
		this.nongreedyChance = nongreedyChance;
	}
	
	@Override
	public int selectAction(double[] reinforcements) {
		int superSelection = super.selectAction(reinforcements);
		if (superSelection == -1)
			return -1;
		if (Math.random() < nongreedyChance)
			return (int) (Math.random() * reinforcements.length);
		else
			return superSelection;
	}
}
