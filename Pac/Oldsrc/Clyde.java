package pacman.children;

import java.awt.Graphics;
import java.util.ArrayList;

import pacman.abstracts.Ghost;
import pacman.abstracts.TriggerSpace;
import pacman.finals.Coordinate;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public final class Clyde extends Ghost {

	public Clyde(boolean[][] legalSpaces,
			ArrayList<TriggerSpace> triggerSpaces, Pacman pacman) {
		super(legalSpaces, triggerSpaces, pacman, new LongCoordinate(0, 35));
		targetImageKey = ImageKey.ClydeTarget;
	}

	@Override
	protected void setTargetTile() {
		// When pacman is within 8 tiles, clyde targets his scatter target. When
		// pacman is farther than 8 tiles, clyde targets pacman.
		Coordinate delta = pacman.getGridPosition().subtractCoordinate(
				getGridPosition());
		double distance = Math.sqrt((delta.getX() * delta.getX())
				+ (delta.getY() * delta.getY()));
		if (distance > 8) {
			targetTile = pacman.getGridPosition().asLongCoordinate();
		} else {
			targetTile = scatterTargetTile;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Clyde), 0, 0, null);
	}
}
