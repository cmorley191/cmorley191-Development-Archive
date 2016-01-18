package pacman.children;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

import pacman.abstracts.Dot;
import pacman.abstracts.Ghost;
import pacman.abstracts.Ghost.GhostMode;
import pacman.abstracts.Level;
import pacman.abstracts.TriggerSpace;
import pacman.finals.Coordinate;
import pacman.finals.LongCoordinate;
import pacman.finals.ResourceManager;
import pacman.finals.SpriteDirection;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public class PacmanLevel extends Level {
	private Pacman pacman;
	private ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
	private ArrayList<Dot> dots = new ArrayList<Dot>();
	public boolean showTargetTiles = false;
	private GhostMode mode = GhostMode.CHASE;

	private ArrayList<TriggerSpace> triggerSpaces = new ArrayList<TriggerSpace>();

	public PacmanLevel(JFrame frame) {
		setDefaultGridDetails();
		setUpSprites(frame);
		setUpOtherObjects();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Maze1), 0, 0, null);
		if (showTargetTiles) {
			for (int a = 0; a < ghosts.size(); a++) {
				g.drawImage(
						ResourceManager.get(ghosts.get(a).getTargetImageKey()),
						(int) ghosts.get(a).getTargetTile().getX()
								* ghosts.get(a).getGridSystem().getGridScale(),
						(int) ghosts.get(a).getTargetTile().getY()
								* ghosts.get(a).getGridSystem().getGridScale(),
						null);
			}
		}
	}

	private void setDefaultGridDetails() {
		// Sets out the legal spaces in the standard pacman maze
		// for every row
		for (int y = 0; y < 36; y++) {
			int[] xs = null;
			if (y == 4) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 15, 16,
						17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 5 || y == 6 || y == 7) {
				int[] xsa = { 1, 6, 12, 15, 21, 26 };
				xs = xsa;
			} else if (y == 8) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 9 || y == 10) {
				int[] xsa = { 1, 6, 9, 18, 21, 26 };
				xs = xsa;
			} else if (y == 11) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 15, 16, 17, 18,
						21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 12 || y == 13) {
				int[] xsa = { 6, 12, 15, 21 };
				xs = xsa;
			} else if (y == 14) {
				int[] xsa = { 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 21 };
				xs = xsa;
			} else if (y == 15 || y == 16) {
				int[] xsa = { 6, 9, 18, 21 };
				xs = xsa;
			} else if (y == 17) {
				int[] xsa = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 19, 20, 21, 22,
						23, 24, 25, 26, 27 };
				xs = xsa;
			} else if (y == 18 || y == 19) {
				int[] xsa = { 6, 9, 18, 21 };
				xs = xsa;
			} else if (y == 20) {
				int[] xsa = { 6, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 21 };
				xs = xsa;
			} else if (y == 21 || y == 22) {
				int[] xsa = { 6, 9, 18, 21 };
				xs = xsa;
			} else if (y == 23) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 15, 16,
						17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 24 || y == 25) {
				int[] xsa = { 1, 6, 12, 15, 21, 26 };
				xs = xsa;
			} else if (y == 26) {
				int[] xsa = { 1, 2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
						17, 18, 19, 20, 21, 24, 25, 26 };
				xs = xsa;
			} else if (y == 27 || y == 28) {
				int[] xsa = { 3, 6, 9, 18, 21, 24 };
				xs = xsa;
			} else if (y == 29) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 15, 16, 17, 18,
						21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 30 || y == 31) {
				int[] xsa = { 1, 12, 15, 26 };
				xs = xsa;
			} else if (y == 32) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			}
			if (xs == null)
				continue;
			for (int x = 0; x < xs.length; x++) {
				legalSpaces[xs[x]][y] = true;
			}
		}
		triggerSpaces.add(new TeleportSpace(new LongCoordinate(0, 17),
				new LongCoordinate(27, 17), new SpriteDirection[] {
						SpriteDirection.UP, SpriteDirection.LEFT,
						SpriteDirection.DOWN, SpriteDirection.RIGHT },
				new SpriteDirection[] { SpriteDirection.UP,
						SpriteDirection.LEFT, SpriteDirection.DOWN,
						SpriteDirection.RIGHT }));

		int origSize = triggerSpaces.size();
		for (int a = 0; a < origSize; a++) {
			if (triggerSpaces.get(a) instanceof TeleportSpace) {
				triggerSpaces.add(((TeleportSpace) triggerSpaces.get(a))
						.getPartner());
			}
		}
	}

	private void setUpSprites(JFrame frame) {
		pacman = new Pacman(legalSpaces, triggerSpaces);
		this.add(pacman);
		pacman.setLocation(CoordinateSystem.Grid8, new Coordinate(14, 26.5));
		frame.addKeyListener(pacman);

		Blinky blinky = new Blinky(legalSpaces, triggerSpaces, pacman);
		blinky.setLocation(CoordinateSystem.Grid8, new Coordinate(13.75, 14.5));
		ghosts.add(blinky);

		Pinky pinky = new Pinky(legalSpaces, triggerSpaces, pacman);
		pinky.setLocation(CoordinateSystem.Grid8, new Coordinate(13.25, 14.5));
		ghosts.add(pinky);

		Inky inky = new Inky(legalSpaces, triggerSpaces, pacman, blinky);
		inky.setLocation(CoordinateSystem.Grid8, new Coordinate(14.25, 14.5));
		ghosts.add(inky);

		Clyde clyde = new Clyde(legalSpaces, triggerSpaces, pacman);
		clyde.setLocation(CoordinateSystem.Grid8, new Coordinate(14.75, 14.5));
		ghosts.add(clyde);

		for (int a = 0; a < ghosts.size(); a++) {
			this.add(ghosts.get(a));
		}
	}

	private void setUpOtherObjects() {
		for (int y = 0; y < 36; y++) {
			int[] xs = null;
			if (y == 4) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 15, 16,
						17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 5) {
				int[] xsa = { 1, 6, 12, 15, 21, 26 };
				xs = xsa;
			} else if (y == 6) {
				int[] xsa = { 6, 12, 15, 21 };
				xs = xsa;
			} else if (y == 7) {
				int[] xsa = { 1, 6, 12, 15, 21, 26 };
				xs = xsa;
			} else if (y == 8) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 9 || y == 10) {
				int[] xsa = { 1, 6, 9, 18, 21, 26 };
				xs = xsa;
			} else if (y == 11) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 15, 16, 17, 18,
						21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 12 || y == 13) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 14) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 15 || y == 16) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 17) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 18 || y == 19) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 20) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 21 || y == 22) {
				int[] xsa = { 6, 21 };
				xs = xsa;
			} else if (y == 23) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 15, 16,
						17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 24 || y == 25) {
				int[] xsa = { 1, 6, 12, 15, 21, 26 };
				xs = xsa;
			} else if (y == 26) {
				int[] xsa = { 2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
						18, 19, 20, 21, 24, 25 };
				xs = xsa;
			} else if (y == 27 || y == 28) {
				int[] xsa = { 3, 6, 9, 18, 21, 24 };
				xs = xsa;
			} else if (y == 29) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 15, 16, 17, 18,
						21, 22, 23, 24, 25, 26 };
				xs = xsa;
			} else if (y == 30 || y == 31) {
				int[] xsa = { 1, 12, 15, 26 };
				xs = xsa;
			} else if (y == 32) {
				int[] xsa = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
				xs = xsa;
			}
			if (xs == null)
				continue;
			for (int x = 0; x < xs.length; x++) {
				dots.add(new PointDot(new LongCoordinate(xs[x], y)));
			}
		}
		int[] x = { 1, 26, 1, 26 };
		int[] y = { 6, 6, 26, 26 };
		for (int a = 0; a < x.length; a++) {
			dots.add(new EnergizerDot(new LongCoordinate(x[a], y[a])));
		}
		for (int a = 0; a < dots.size(); a++) {
			this.add(dots.get(a));
		}
	}

	@Override
	protected void switchMode() {
		if (mode == GhostMode.CHASE)
			mode = GhostMode.SCATTER;
		else
			mode = GhostMode.CHASE;
		for (int a = 0; a < ghosts.size(); a++) {
			ghosts.get(a).setMode(mode);
		}
	}

	@Override
	protected long getNextModeSwitchTime() {
		return 7000;
	}

}