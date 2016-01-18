package pacman.finals;

import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * A campaign represents a playing experience for a player. It is made up of a
 * collection of levels and coordinates the ordering of these levels as
 * presented to the player.
 * 
 * @author Charlie Morley
 *
 */
public class Campaign {

	private ArrayList<LevelParameters> blueprints;
	private int[] order;

	public Campaign(CampaignParameters campaignParameters) {
		blueprints = campaignParameters.getBlueprints();
		order = campaignParameters.getOrder();
	}

	public void startCampaign(JFrame window) {
		int levelNumber = 0;
		while (levelNumber < order.length) {
			Level level = new Level(window, blueprints.get(order[levelNumber]));
			window.add(level);
			window.pack();
			Thread thread = new Thread(level);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.remove(level);
			levelNumber++;
		}
	}
}
