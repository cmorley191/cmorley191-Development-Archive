package pacman.children;

import java.awt.Graphics;
import java.util.ArrayList;

import pacman.abstracts.Ghost;
import pacman.abstracts.TriggerSpace;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public final class Pinky extends Ghost {

	public Pinky(boolean[][] legalSpaces,
			ArrayList<TriggerSpace> triggerSpaces, Pacman pacman) {
		super(legalSpaces, triggerSpaces, pacman, new LongCoordinate(27, 0));
		targetImageKey = ImageKey.PinkyTarget;
	}

	@Override
	protected void setTargetTile() {
		// Pinky targets 4 tiles ahead of pacman's position.
		switch (pacman.getDirection()) {
		case UP:
			// A bug in the original game made it so when pacman faces up,
			// blinky targets 4 tiles ahead and 4 tiles to the left.
			targetTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-4, -4));
			break;
		case LEFT:
			targetTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-4, 0));
			break;
		case DOWN:
			targetTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(0, 4));
			break;
		case RIGHT:
			targetTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(4, 0));
			break;
		default:
			return;
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Pinky), 0, 0, null);
	}
}
