package pacman.abstracts;

import pacman.finals.LongCoordinate;

public abstract class TriggerSpace {
	private LongCoordinate position;

	public TriggerSpace(LongCoordinate position) {
		this.position = position;
	}

	public LongCoordinate getPosition() {
		return position;
	}

	public abstract void triggered(Sprite s);
}
