package pacman.children;

import java.awt.Graphics;
import java.util.ArrayList;

import pacman.abstracts.Ghost;
import pacman.abstracts.TriggerSpace;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public final class Inky extends Ghost {
	private Blinky blinky;

	public Inky(boolean[][] legalSpaces, ArrayList<TriggerSpace> triggerSpaces,
			Pacman pacman, Blinky blinky) {
		super(legalSpaces, triggerSpaces, pacman, new LongCoordinate(27, 35));
		this.blinky = blinky;
		targetImageKey = ImageKey.InkyTarget;
	}

	@Override
	protected void setTargetTile() {
		// Inky targets the tile that is mirrors Blinky's position off of the
		// tile 2 tiles ahead of pacman.
		LongCoordinate mirrorTile;
		switch (pacman.getDirection()) {
		case UP:
			// A bug in the original game made it so when pacman faces up,
			// the mirroring point is 2 tiles ahead and 2 tiles left
			mirrorTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-2, -2));
			break;
		case LEFT:
			mirrorTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-2, 0));
			break;
		case DOWN:
			mirrorTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(0, 2));
			break;
		case RIGHT:
			mirrorTile = pacman.getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(2, 0));
			break;
		default:
			return;
		}
		targetTile = mirrorTile.subtractCoordinate(blinky.getGridPosition()
				.asLongCoordinate().subtractCoordinate(mirrorTile));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Inky), 0, 0, null);
	}
}
