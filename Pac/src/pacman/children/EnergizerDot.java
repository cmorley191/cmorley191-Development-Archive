package pacman.children;

import java.awt.Graphics;

import pacman.abstracts.Dot;
import pacman.finals.Coordinate;
import pacman.finals.Level;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public class EnergizerDot extends Dot {

	public EnergizerDot() {
		this.setSize(8, 8);
	}

	public EnergizerDot(LongCoordinate position) {
		this();
		this.setPosition(position.asDoubleCoordinate().combineCoordinate(
				new Coordinate(0.5, 0.5)));
	}

	@Override
	public void eaten(Level level) {
		level.setFrightened();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.EnergizerDot), 0, 0, null);
	}

}
