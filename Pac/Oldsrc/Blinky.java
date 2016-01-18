package pacman.children;

import java.awt.Graphics;
import java.util.ArrayList;

import pacman.abstracts.Ghost;
import pacman.abstracts.TriggerSpace;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public final class Blinky extends Ghost {

	public Blinky(boolean[][] legalSpaces,
			ArrayList<TriggerSpace> triggerSpaces, Pacman pacman) {
		super(legalSpaces, triggerSpaces, pacman, new LongCoordinate(0, 0));
		targetImageKey = ImageKey.BlinkyTarget;
	}

	@Override
	protected void setTargetTile() {
		// Blinky always targets Pacman's position.
		targetTile = pacman.getGridPosition().asLongCoordinate();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Blinky), 0, 0, null);
	}
}
