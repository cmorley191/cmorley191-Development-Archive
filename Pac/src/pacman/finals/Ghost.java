package pacman.finals;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import pacman.abstracts.Sprite;
import pacman.abstracts.TriggerSpace;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public class Ghost extends Sprite {

	private String name;
	private ImageKey skin;
	private Dimension ghostSize;
	private ImageKey targetImageKey;
	private int personalLeavingDotLimit;
	private int globalLeavingDotLimit;

	private int personalDotCounter;
	private int globalDotCounter;
	private boolean usingPersonalDotCounter;
	private boolean usingGlobalDotCounter;
	private boolean[][] idlingLegalSpaces;
	private boolean[][] leavingLegalSpaces;
	private LongCoordinate exitPosition;

	public enum HouseStatus {
		Idling, Leaving, Active;
	}

	private HouseStatus houseStatus;

	private Random random = new Random();

	// Used in motion
	private Direction[] nextDirection = new Direction[2];
	private LongCoordinate targetTile = new LongCoordinate(0, 0);
	private boolean frightened = false;
	private ReachedCenterMovementFlag updateDirection = new ReachedCenterMovementFlag();
	private EnteredNewTileMovementFlag calculateNextDirection = new EnteredNewTileMovementFlag();

	public Ghost(boolean[][] legalSpaces,
			ArrayList<TriggerSpace> triggerSpaces,
			GhostParameters ghostParameters, boolean[][] idlingLegalSpaces,
			boolean[][] leavingLegalSpaces, LongCoordinate exitPosition,
			HouseStatus initialHouseStatus, Coordinate initialPosition,
			Direction initialDirection) {
		super(legalSpaces, triggerSpaces);
		// Unpack parameters
		this.name = ghostParameters.getName();
		this.skin = ghostParameters.getSkin();
		this.ghostSize = ghostParameters.getGhostSize();
		this.targetImageKey = ghostParameters.getTargetImageKey();
		this.personalLeavingDotLimit = ghostParameters
				.getPersonalLeavingDotLimit();
		this.globalLeavingDotLimit = ghostParameters.getGlobalLeavingDotLimit();

		this.idlingLegalSpaces = idlingLegalSpaces;
		this.leavingLegalSpaces = leavingLegalSpaces;
		this.exitPosition = exitPosition;

		// Initialize
		setSize(ghostSize);
		for (int x = 0; x < idlingLegalSpaces.length; x++) {
			for (int y = 0; y < idlingLegalSpaces[x].length; y++) {
				if (idlingLegalSpaces[x][y]) {
					if (legalSpaces[x][y])
						legalSpaces[x][y] = false;
					else
						legalSpaces[x][y] = true;
				}
			}
		}
		houseStatus = initialHouseStatus;
		personalDotCounter = 0;
		globalDotCounter = 0;
		if (initialHouseStatus == HouseStatus.Idling)
			usingPersonalDotCounter = true;
		else
			usingPersonalDotCounter = false;
		usingGlobalDotCounter = false;

		// Initialize motion
		setPosition(initialPosition);
		setDirection(initialDirection);
		// nextDirection[0] = direction;
		// nextDirection[1] = direction;
		movementFlags.add(updateDirection);
		movementFlags.add(calculateNextDirection);
	}

	public String getSpriteName() {
		return name;
	}

	@Override
	protected void handleMovementFlag(MovementFlag flag,
			double travelledDistance) {
		super.handleMovementFlag(flag, travelledDistance);
		if (flag == updateDirection) {
			// If reversing direction, put it over the center of the tile so
			// this flag doesn't get activated again.
			if (this.getDirection().getOpposite() == nextDirection[0])
				switch (nextDirection[0]) {
				case UP:
					super.setPosition(getPosition().combineCoordinate(
							new Coordinate(0, -2.0
									* MovementFlag.MINISCULEPUSHOVER)));
					break;
				case LEFT:
					super.setPosition(getPosition().combineCoordinate(
							new Coordinate(-2.0
									* MovementFlag.MINISCULEPUSHOVER, 0)));
					break;
				case DOWN:
					super.setPosition(getPosition().combineCoordinate(
							new Coordinate(0,
									2.0 * MovementFlag.MINISCULEPUSHOVER)));
					break;
				case RIGHT:
					super.setPosition(getPosition().combineCoordinate(
							new Coordinate(
									2.0 * MovementFlag.MINISCULEPUSHOVER, 0)));
					break;
				}
			// Change to planned direction once at the center of the tile
			super.setDirection(nextDirection[0]);
			// Advance the next planned direction
			nextDirection[0] = nextDirection[1];
		} else if (flag == calculateNextDirection) {
			// Update house status
			if (houseStatus == HouseStatus.Leaving
					&& this.getPosition().asLongCoordinate()
							.equals(exitPosition)) {
				setPosition(getPosition().combineCoordinate(
						new Coordinate(0.5 - MovementFlag.MINISCULEPUSHOVER,
								0.5)));
				for (int x = 0; x < leavingLegalSpaces.length; x++) {
					for (int y = 0; y < leavingLegalSpaces[x].length; y++) {
						if (leavingLegalSpaces[x][y]) {
							if (legalSpaces[x][y])
								legalSpaces[x][y] = false;
							else
								legalSpaces[x][y] = true;
						}
					}
				}

				houseStatus = HouseStatus.Active;
				setDirection(getDirection());
			} else

				// Plan the next tile's direction once entering a new tile
				calculateNextDirection();
		}
	}

	public void setTargetTile(LongCoordinate targetTile) {
		this.targetTile = targetTile;
	}

	public void setFrightened(boolean frightened) {
		this.frightened = frightened;
	}

	public void setUsingPersonalDotCounter(boolean value) {
		usingPersonalDotCounter = value;
	}

	public void setUsingGlobalDotCounter(boolean value) {
		usingGlobalDotCounter = value;
	}

	public void increasePersonalDotCounter(int value) {
		if (houseStatus == HouseStatus.Idling && usingPersonalDotCounter) {
			personalDotCounter += value;
			if (personalDotCounter >= personalLeavingDotLimit) {
				usingPersonalDotCounter = false;
				houseStatus = HouseStatus.Leaving;
				for (int x = 0; x < idlingLegalSpaces.length; x++) {
					for (int y = 0; y < idlingLegalSpaces[x].length; y++) {
						if (idlingLegalSpaces[x][y]) {
							if (legalSpaces[x][y])
								legalSpaces[x][y] = false;
							else
								legalSpaces[x][y] = true;
						}
						if (leavingLegalSpaces[x][y]) {
							if (legalSpaces[x][y])
								legalSpaces[x][y] = false;
							else
								legalSpaces[x][y] = true;
						}
					}
				}
				setDirection(getDirection());
			}
		}
	}

	public void setGlobalDotCounter(int value) {
		if (houseStatus == HouseStatus.Idling && usingGlobalDotCounter) {
			globalDotCounter = value;
			if (globalDotCounter >= globalLeavingDotLimit) {
				usingGlobalDotCounter = false;
				houseStatus = HouseStatus.Leaving;
				for (int x = 0; x < idlingLegalSpaces.length; x++) {
					for (int y = 0; y < idlingLegalSpaces[x].length; y++) {
						if (idlingLegalSpaces[x][y]) {
							if (legalSpaces[x][y])
								legalSpaces[x][y] = false;
							else
								legalSpaces[x][y] = true;
						}
						if (leavingLegalSpaces[x][y]) {
							if (legalSpaces[x][y])
								legalSpaces[x][y] = false;
							else
								legalSpaces[x][y] = true;
						}
					}
				}
			}
		}
	}

	public HouseStatus getHouseStatus() {
		return houseStatus;
	}

	private void calculateNextDirection() {
		// Setup
		ArrayList<Direction> directionOptions = new ArrayList<Direction>();
		ArrayList<LongCoordinate> tileOptions = new ArrayList<LongCoordinate>();

		directionOptions.add(Direction.UP);
		directionOptions.add(Direction.LEFT);
		directionOptions.add(Direction.DOWN);
		directionOptions.add(Direction.RIGHT);

		switch (nextDirection[0]) {
		case UP:
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(0, -2)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(-1, -1)));
			tileOptions.add(getPosition().asLongCoordinate());
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(1, -1)));
			break;
		case LEFT:
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(-1, -1)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(-2, 0)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(-1, 1)));
			tileOptions.add(getPosition().asLongCoordinate());
			break;
		case DOWN:
			tileOptions.add(getPosition().asLongCoordinate());
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(-1, 1)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(0, 2)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(1, 1)));
			break;
		case RIGHT:
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(1, -1)));
			tileOptions.add(getPosition().asLongCoordinate());
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(1, 1)));
			tileOptions.add(getPosition().asLongCoordinate().combineCoordinate(
					new LongCoordinate(2, 0)));
			break;
		default:
			return;
		}

		// Remove dead spaces
		for (int a = 0; a < tileOptions.size(); a++) {
			try {
				if (!legalSpaces[(int) tileOptions.get(a).getX()][(int) tileOptions
						.get(a).getY()]) {
					directionOptions.remove(a);
					tileOptions.remove(a);
					a--;
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				// System.out.println("Tile Option out of bounds");
				directionOptions.remove(a);
				tileOptions.remove(a);
				a--;
				continue;
			}
		}
		if (directionOptions.size() == 0) {
			// Stop
		} else if (directionOptions.size() == 1) {
			nextDirection[1] = directionOptions.get(0);
			return;
		}

		// Remove reverse direction space
		Direction oppositeDirection = nextDirection[0].getOpposite();
		for (int a = 0; a < directionOptions.size(); a++) {
			if (directionOptions.get(a) == oppositeDirection) {
				directionOptions.remove(a);
				tileOptions.remove(a);
				break;
			}
		}
		if (directionOptions.size() == 1) {
			nextDirection[1] = directionOptions.get(0);
			return;
		}

		// If frightened, random
		if (frightened)
			nextDirection[1] = directionOptions.get(random
					.nextInt(directionOptions.size()));

		// Tile nearest target tile
		int[] nearestIndicies = { 0 };
		Coordinate targetDelta = targetTile.asDoubleCoordinate()
				.subtractCoordinate(tileOptions.get(0).asDoubleCoordinate());
		double nearestDistance = Math.sqrt((targetDelta.getX() * targetDelta
				.getX()) + (targetDelta.getY() * targetDelta.getY()));
		for (int a = 1; a < tileOptions.size(); a++) {
			targetDelta = targetTile.asDoubleCoordinate().subtractCoordinate(
					tileOptions.get(a).asDoubleCoordinate());
			double distance = Math.sqrt((targetDelta.getX() * targetDelta
					.getX()) + (targetDelta.getY() * targetDelta.getY()));
			if (distance > nearestDistance) {
				directionOptions.remove(a);
				tileOptions.remove(a);
				a--;
			} else if (distance == nearestDistance) {
				int[] temp = nearestIndicies;
				nearestIndicies = new int[temp.length + 1];
				for (int b = 0; b < temp.length; b++)
					nearestIndicies[b] = temp[b];
				nearestIndicies[nearestIndicies.length - 1] = a;
			} else if (distance < nearestDistance) {
				for (int b = 0; b < nearestIndicies.length; b++) {
					tileOptions.remove(nearestIndicies[b]);
					directionOptions.remove(nearestIndicies[b]);
					a--;
				}
				nearestIndicies = new int[1];
				nearestIndicies[0] = a;
				nearestDistance = distance;
			}
		}

		// Options are already in order of UP, LEFT, DOWN, RIGHT, so just make
		// the check for both the above section and that order now by choosing
		// the first one
		nextDirection[1] = directionOptions.get(0);
	}

	@Override
	public void setDirection(Direction newDirection) {
		super.setDirection(newDirection);
		Coordinate originalPosition = getPosition();
		switch (newDirection) {
		case UP:
			setPosition(originalPosition.combineCoordinate(new Coordinate(0,
					0.5)));
			break;
		case LEFT:
			setPosition(originalPosition.combineCoordinate(new Coordinate(0.5,
					0)));
			break;
		case DOWN:
			setPosition(originalPosition.combineCoordinate(new Coordinate(0,
					-0.5)));
			break;
		case RIGHT:
			setPosition(originalPosition.combineCoordinate(new Coordinate(-0.5,
					0)));
			break;
		}
		if (getPosition().asLongCoordinate().equals(
				originalPosition.asLongCoordinate())) {
			// Past halfway mark
			nextDirection[0] = newDirection;
			calculateNextDirection();
			setPosition(originalPosition);
			nextDirection[0] = nextDirection[1];
		} else {
			// Not past halfway mark
			Coordinate halfPosition = getPosition();
			switch (newDirection) {
			case UP:
				setPosition(halfPosition.combineCoordinate(new Coordinate(0,
						0.5)));
				break;
			case LEFT:
				setPosition(halfPosition.combineCoordinate(new Coordinate(0.5,
						0)));
				break;
			case DOWN:
				setPosition(halfPosition.combineCoordinate(new Coordinate(0,
						-0.5)));
				break;
			case RIGHT:
				setPosition(halfPosition.combineCoordinate(new Coordinate(-0.5,
						0)));
				break;
			}
			nextDirection[0] = newDirection;
			calculateNextDirection();
			setPosition(originalPosition);
			nextDirection[0] = nextDirection[1];
			calculateNextDirection();

		}
	}

	@Override
	protected double getSubjectiveSpeed() {
		return SPRITE_MAX_SPEED * 0.75;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(skin), 0, 0, null);
	}

	public ImageKey getTargetImageKey() {
		return targetImageKey;
	}
}
