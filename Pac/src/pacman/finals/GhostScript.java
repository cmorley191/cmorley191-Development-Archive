package pacman.finals;

import java.util.ArrayList;

public class GhostScript {

	private LongCoordinate scatterTarget;

	public GhostScript(LongCoordinate scatterTarget) {
		this.scatterTarget = scatterTarget;
	}

	public enum GhostMode {
		CHASE, SCATTER, FRIGHTENED;
	}

	// Intended to be overridden
	public LongCoordinate getChaseTarget(ArrayList<String> names,
			ArrayList<LongCoordinate> positions, ArrayList<Direction> directions) {
		return scatterTarget;
	}

	public LongCoordinate getScatterTarget() {
		return scatterTarget;
	}
}
