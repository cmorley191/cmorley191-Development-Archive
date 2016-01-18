package pacman.children;

import java.awt.Graphics;

import pacman.abstracts.Dot;
import pacman.finals.Coordinate;
import pacman.finals.Level;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public class PointDot extends Dot {

	public PointDot() {
		this.setSize(2, 2);
	}

	public PointDot(LongCoordinate position) {
		this();
		this.setPosition(position.asDoubleCoordinate().combineCoordinate(
				new Coordinate(0.5, 0.5)));
	}

	@Override
	public void eaten(Level level) {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.PointDot), 0, 0, null);
	}

}
