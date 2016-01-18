package pacman.finals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import pacman.abstracts.TriggerSpace;
import pacman.children.TeleportSpace;
import pacman.finals.Ghost.HouseStatus;
import pacman.finals.GhostScript.GhostMode;
import pacman.finals.ResourceManager.ImageKey;

import com.sun.glass.events.KeyEvent;

/**
 * Main class for the Pacman API.
 * 
 * @author Charlie Morley
 *
 */
public final class Pac {

	/**
	 * The window containing displaying all game functions and accepting user
	 * input
	 */
	private static JFrame gameWindow;

	/**
	 * Initializes the game.
	 * 
	 * @param args
	 *            required; unused
	 */
	public static void main(String[] args) {
		ResourceManager.loadAllKeys();
		initializeGraphics();

		// Initialize the level
		ArrayList<TriggerSpace> triggerSpaces = new ArrayList<TriggerSpace>();
		ArrayList<GhostParameters> ghosts = new ArrayList<GhostParameters>();
		ArrayList<GhostScript> ghostScripts = new ArrayList<GhostScript>();
		ArrayList<Coordinate> startingPositions = new ArrayList<Coordinate>();
		ArrayList<boolean[][]> idlingLegalSpaces = new ArrayList<boolean[][]>();
		ArrayList<boolean[][]> leavingLegalSpaces = new ArrayList<boolean[][]>();
		ArrayList<LongCoordinate> exitPositions = new ArrayList<LongCoordinate>();
		ArrayList<HouseStatus> initialHouseStatuses = new ArrayList<HouseStatus>();
		ArrayList<Direction> initialDirections = new ArrayList<Direction>();

		triggerSpaces.add(new TeleportSpace(new LongCoordinate(0, 17),
				new LongCoordinate(27, 17), new Direction[] { Direction.UP,
						Direction.LEFT, Direction.DOWN, Direction.RIGHT },
				new Direction[] { Direction.UP, Direction.LEFT, Direction.DOWN,
						Direction.RIGHT }));
		triggerSpaces.add(((TeleportSpace) triggerSpaces.get(0)).getPartner());

		ghosts.add(new GhostParameters("Blinky", ImageKey.Blinky,
				new Dimension(14, 14), ImageKey.BlinkyTarget, 0, 0));
		ghostScripts.add(new GhostScript(new LongCoordinate(0, 0)) {
			@Override
			public LongCoordinate getChaseTarget(ArrayList<String> names,
					ArrayList<LongCoordinate> positions,
					ArrayList<Direction> directions) {
				for (int i = 0; i < names.size(); i++)
					if (names.get(i).equals("Pacman"))
						return positions.get(i);
				return new LongCoordinate(0, 0);
			}
		});
		startingPositions.add(new Coordinate(14, 14.5));
		idlingLegalSpaces.add(new boolean[28][36]);
		leavingLegalSpaces.add(new boolean[28][36]);
		exitPositions.add(new LongCoordinate(14, 14));
		initialHouseStatuses.add(HouseStatus.Active);
		initialDirections.add(Direction.LEFT);

		ghosts.add(new GhostParameters("Pinky", ImageKey.Pinky, new Dimension(
				14, 14), ImageKey.PinkyTarget, 0, 7));
		ghostScripts.add(new GhostScript(new LongCoordinate(27, 0)) {
			@Override
			public LongCoordinate getChaseTarget(ArrayList<String> names,
					ArrayList<LongCoordinate> positions,
					ArrayList<Direction> directions) {
				for (int i = 0; i < names.size(); i++) {
					if (names.get(i).equals("Pacman")) {
						switch (directions.get(i)) {
						case UP:
							return positions.get(i).combineCoordinate(
									new LongCoordinate(-4, -4));
						case LEFT:
							return positions.get(i).combineCoordinate(
									new LongCoordinate(-4, 0));
						case DOWN:
							return positions.get(i).combineCoordinate(
									new LongCoordinate(0, 4));
						case RIGHT:
							return positions.get(i).combineCoordinate(
									new LongCoordinate(4, 0));
						}
					}
				}
				return new LongCoordinate(27, 0);
			}
		});
		startingPositions.add(new Coordinate(13.5, 17));
		boolean[][] pinkyIdle = new boolean[28][36];
		pinkyIdle[13][16] = true;
		pinkyIdle[13][17] = true;
		idlingLegalSpaces.add(pinkyIdle);
		boolean[][] pinkyLeaving = new boolean[28][36];
		pinkyLeaving[13][16] = true;
		pinkyLeaving[13][17] = true;
		pinkyLeaving[13][15] = true;
		leavingLegalSpaces.add(pinkyLeaving);
		exitPositions.add(new LongCoordinate(13, 14));
		initialHouseStatuses.add(HouseStatus.Idling);
		initialDirections.add(Direction.DOWN);

		ghosts.add(new GhostParameters("Inky", ImageKey.Inky, new Dimension(14,
				14), ImageKey.InkyTarget, 17, 30));
		ghostScripts.add(new GhostScript(new LongCoordinate(27, 35)) {
			@Override
			public LongCoordinate getChaseTarget(ArrayList<String> names,
					ArrayList<LongCoordinate> positions,
					ArrayList<Direction> directions) {
				LongCoordinate blinkyPosition = null;
				LongCoordinate pacmanAhead = null;
				for (int i = 0; i < names.size()
						&& (blinkyPosition == null || pacmanAhead == null); i++) {
					if (names.get(i).equals("Blinky"))
						blinkyPosition = positions.get(i);
					else if (names.get(i).equals("Pacman")) {
						switch (directions.get(i)) {
						case UP:
							pacmanAhead = positions.get(i).combineCoordinate(
									new LongCoordinate(-2, -2));
							break;
						case LEFT:
							pacmanAhead = positions.get(i).combineCoordinate(
									new LongCoordinate(-2, 0));
							break;
						case DOWN:
							pacmanAhead = positions.get(i).combineCoordinate(
									new LongCoordinate(0, 2));
							break;
						case RIGHT:
							pacmanAhead = positions.get(i).combineCoordinate(
									new LongCoordinate(2, 0));
							break;
						}
					}
				}
				if (blinkyPosition == null || pacmanAhead == null)
					return new LongCoordinate(27, 35);
				return pacmanAhead.combineCoordinate(pacmanAhead
						.subtractCoordinate(blinkyPosition));
			}
		});
		startingPositions.add(new Coordinate(11.5, 17));
		boolean[][] inkyIdle = new boolean[28][36];
		inkyIdle[11][16] = true;
		inkyIdle[11][17] = true;
		idlingLegalSpaces.add(inkyIdle);
		boolean[][] inkyLeaving = new boolean[28][36];
		inkyLeaving[11][16] = true;
		inkyLeaving[11][17] = true;
		inkyLeaving[12][17] = true;
		inkyLeaving[13][17] = true;
		inkyLeaving[13][16] = true;
		inkyLeaving[13][15] = true;
		leavingLegalSpaces.add(inkyLeaving);
		exitPositions.add(new LongCoordinate(13, 14));
		initialHouseStatuses.add(HouseStatus.Idling);
		initialDirections.add(Direction.UP);

		ghosts.add(new GhostParameters("Clyde", ImageKey.Clyde, new Dimension(
				14, 14), ImageKey.ClydeTarget, 32, 60));
		ghostScripts.add(new GhostScript(new LongCoordinate(0, 35)) {
			@Override
			public LongCoordinate getChaseTarget(ArrayList<String> names,
					ArrayList<LongCoordinate> positions,
					ArrayList<Direction> directions) {
				LongCoordinate pacmanPosition = null;
				LongCoordinate clydePosition = null;
				for (int i = 0; i < names.size()
						&& (pacmanPosition == null || clydePosition == null); i++) {
					if (names.get(i).equals("Pacman")) {
						pacmanPosition = positions.get(i);
					} else if (names.get(i).equals("Clyde")) {
						clydePosition = positions.get(i);
					}
				}
				if (pacmanPosition == null || clydePosition == null)
					return new LongCoordinate(0, 35);

				double x = pacmanPosition.getX() - clydePosition.getX();
				double y = pacmanPosition.getY() - clydePosition.getY();
				if (Math.sqrt((x * x) + (y * y)) < 8)
					return new LongCoordinate(0, 35);
				else
					return pacmanPosition;
			}
		});
		startingPositions.add(new Coordinate(15.5, 17));
		boolean[][] clydeIdle = new boolean[28][36];
		clydeIdle[15][16] = true;
		clydeIdle[15][17] = true;
		idlingLegalSpaces.add(clydeIdle);
		boolean[][] clydeLeaving = new boolean[28][36];
		clydeLeaving[15][16] = true;
		clydeLeaving[15][17] = true;
		clydeLeaving[14][17] = true;
		clydeLeaving[13][17] = true;
		clydeLeaving[13][16] = true;
		clydeLeaving[13][15] = true;
		leavingLegalSpaces.add(clydeLeaving);
		exitPositions.add(new LongCoordinate(13, 14));
		initialHouseStatuses.add(HouseStatus.Idling);
		initialDirections.add(Direction.UP);

		LevelParameters levelParameters = new LevelParameters(ImageKey.Maze1,
				new Dimension(28, 36), getClassicLegalSpaces(), ghosts,
				ghostScripts, new long[] { 7000, 20000, 7000, 20000, 5000,
						20000, 5000 }, GhostMode.SCATTER, 6000, new Coordinate(
						14, 26.5), triggerSpaces,
				getClassicPointDotPositions(),
				getClassicEnergizerDotPositions(), startingPositions,
				idlingLegalSpaces, leavingLegalSpaces, exitPositions,
				initialHouseStatuses, initialDirections);

		ArrayList<LevelParameters> blueprints = new ArrayList<LevelParameters>();
		blueprints.add(levelParameters);
		CampaignParameters campaignParameters = new CampaignParameters(
				blueprints, new int[] { 0, 0, 0 });
		new Campaign(campaignParameters).startCampaign(gameWindow);
		gameWindow.dispatchEvent(new WindowEvent(gameWindow,
				WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Sets up {@link #gameWindow} with a title, size, and other settings.
	 */
	private static void initializeGraphics() {
		// Initialize the window, title of "Pac-Man"
		gameWindow = new JFrame("Pac-Man");

		// Window size set to default Pac-Man resolution
		gameWindow.getContentPane().setPreferredSize(new Dimension(224, 288));
		gameWindow.setResizable(false);

		// Define that program exits when window is closed
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Background
		gameWindow.setBackground(Color.BLACK);

		// Show the window
		gameWindow.setVisible(true);
	}

	/**
	 * Returns the legality of the classic Pac-Man game board.
	 * 
	 * @return an array containing the legality of each space on the classic
	 *         Pac-Man game board
	 */
	private static boolean[][] getClassicLegalSpaces() {
		boolean[][] legalSpaces = new boolean[28][36];
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
		return legalSpaces;
	}

	/**
	 * Returns the positions of the {@link pacman.children.PointDot PointDots}
	 * on the classic Pac-Man game board.
	 * 
	 * @return the positions of the PointDots on the classic Pac-Man game board
	 */
	private static ArrayList<LongCoordinate> getClassicPointDotPositions() {
		ArrayList<LongCoordinate> positions = new ArrayList<LongCoordinate>();
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
				positions.add(new LongCoordinate(xs[x], y));
			}
		}
		return positions;
	}

	/**
	 * Returns the positions of the {@link pacman.children.EnergizerDot
	 * EnergizerDots} on the classic Pac-Man game board.
	 * 
	 * @return the positions of the EnergizerDots on the classic Pac-Man game
	 *         board
	 */
	private static ArrayList<LongCoordinate> getClassicEnergizerDotPositions() {
		ArrayList<LongCoordinate> positions = new ArrayList<LongCoordinate>();
		positions.add(new LongCoordinate(1, 6));
		positions.add(new LongCoordinate(26, 6));
		positions.add(new LongCoordinate(1, 26));
		positions.add(new LongCoordinate(26, 26));
		return positions;
	}

	/**
	 * The binding of a key code to a program function.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public enum KeyBinding {

		PacMoveUp(KeyEvent.VK_UP), PacMoveLeft(KeyEvent.VK_LEFT), PacMoveDown(
				KeyEvent.VK_DOWN), PacMoveRight(KeyEvent.VK_RIGHT);

		private int keycode;

		KeyBinding(int keycode) {
			this.keycode = keycode;
		}

		public int getKeycode() {
			return keycode;
		}

		void setKeycode(int keycode) {
			this.keycode = keycode;
		}
	}
}
