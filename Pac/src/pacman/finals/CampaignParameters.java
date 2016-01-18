package pacman.finals;

import java.util.ArrayList;

public class CampaignParameters {

	private ArrayList<LevelParameters> blueprints;
	private int[] order;

	public CampaignParameters(ArrayList<LevelParameters> blueprints, int[] order) {
		this.blueprints = new ArrayList<LevelParameters>();
		this.blueprints.addAll(blueprints);

		this.order = new int[order.length];
		for (int i = 0; i < order.length; i++)
			this.order[i] = order[i];
	}

	public ArrayList<LevelParameters> getBlueprints() {
		ArrayList<LevelParameters> returnList = new ArrayList<LevelParameters>();
		returnList.addAll(blueprints);
		return returnList;
	}

	public int[] getOrder() {
		int[] returnArray = new int[order.length];
		for (int i = 0; i < order.length; i++)
			returnArray[i] = order[i];
		return returnArray;
	}

}
