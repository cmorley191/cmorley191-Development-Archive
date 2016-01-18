package pacman.abstracts;

import java.util.ArrayList;

import pacman.children.Pacman;
import pacman.finals.Coordinate;
import pacman.finals.LongCoordinate;
import pacman.finals.SpriteDirection;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public abstract class Ghost extends Sprite {
	private SpriteDirection[] nextDirection = new SpriteDirection[2];
	protected LongCoordinate targetTile = new LongCoordinate(0, 0);
	protected LongCoordinate scatterTargetTile;
	private GhostMode attackMode = GhostMode.CHASE;
	protected ImageKey targetImageKey = ImageKey.BlinkyTarget;
	protected Pacman pacman;

	private ReachedCenterMovementFlag updateDirection = new ReachedCenterMovementFlag();
	private EnteredNewTileMovementFlag calculateNextDirection = new EnteredNewTileMovementFlag();

	public Ghost(boolean[][] legalSpaces,
			ArrayList<TriggerSpace> triggerSpaces, Pacman pacman,
			LongCoordinate scatterTargetTile) {
		super(legalSpaces, triggerSpaces);
		this.pacman = pacman;
		this.scatterTargetTile = scatterTargetTile;
		this.setSize(14, 14);
		direction = SpriteDirection.LEFT;
		nextDirection[0] = direction;
		nextDirection[1] = direction;
		movementFlags.add(updateDirection);
		movementFlags.add(calculateNextDirection);
	}

	@Override
	protected void handleMovementFlag(MovementFlag flag,
			double travelledDistance) {
		super.handleMovementFlag(flag, travelledDistance);
		if (flag == updateDirection) {
			direction = nextDirection[0];
			nextDirection[0] = nextDirection[1];
		} else if (flag == calculateNextDirection) {
			calculateNextDirection();
		}
	}

	protected abstract void setTargetTile();

	public enum GhostMode {
		CHASE, SCATTER;
	}

	@Override
	public void setDirection(SpriteDirection newDirection) {
		super.setDirection(newDirection);
		nextDirection[0] = newDirection;
		Coordinate origLocation = getGridPosition();
		double offset = 0.5;
		switch (direction) {
		case UP:
			setLocation(
					getGridSystem(),
					getGridPosition().combineCoordinate(
							new Coordinate(0, offset)));
			calculateNextDirection();
			break;
		case LEFT:
			setLocation(
					getGridSystem(),
					getGridPosition().combineCoordinate(
							new Coordinate(offset, 0)));
			calculateNextDirection();
			break;
		case DOWN:
			setLocation(
					getGridSystem(),
					getGridPosition().combineCoordinate(
							new Coordinate(0, -offset)));
			calculateNextDirection();
			break;
		case RIGHT:
			setLocation(
					getGridSystem(),
					getGridPosition().combineCoordinate(
							new Coordinate(-offset, 0)));
			calculateNextDirection();
			break;
		}
		nextDirection[0] = nextDirection[1];
		setLocation(getGridSystem(), origLocation);
		calculateNextDirection();
	}

	public void setMode(GhostMode m) {
		GhostMode origMode = attackMode;
		attackMode = m;
		if (m == GhostMode.SCATTER)
			targetTile = scatterTargetTile;
		if (m != origMode) {
			setDirection(direction.getOpposite());
		}
	}

	private void calculateNextDirection() {
		// Setup
		if (attackMode == GhostMode.CHASE)
			setTargetTile();
		ArrayList<SpriteDirection> directionOptions = new ArrayList<SpriteDirection>();
		ArrayList<LongCoordinate> tileOptions = new ArrayList<LongCoordinate>();
		SpriteDirection oppositeDirection;

		directionOptions.add(SpriteDirection.UP);
		directionOptions.add(SpriteDirection.LEFT);
		directionOptions.add(SpriteDirection.DOWN);
		directionOptions.add(SpriteDirection.RIGHT);

		switch (nextDirection[0]) {
		case UP:
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(0, -2)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-1, -1)));
			tileOptions.add(getGridPosition().asLongCoordinate());
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(1, -1)));
			oppositeDirection = SpriteDirection.DOWN;
			break;
		case LEFT:
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-1, -1)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-2, 0)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-1, 1)));
			tileOptions.add(getGridPosition().asLongCoordinate());
			oppositeDirection = SpriteDirection.RIGHT;
			break;
		case DOWN:
			tileOptions.add(getGridPosition().asLongCoordinate());
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(-1, 1)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(0, 2)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(1, 1)));
			oppositeDirection = SpriteDirection.UP;
			break;
		case RIGHT:
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(1, -1)));
			tileOptions.add(getGridPosition().asLongCoordinate());
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(1, 1)));
			tileOptions.add(getGridPosition().asLongCoordinate()
					.combineCoordinate(new LongCoordinate(2, 0)));
			oppositeDirection = SpriteDirection.LEFT;
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

	public LongCoordinate getTargetTile() {
		return targetTile;
	}

	public ImageKey getTargetImageKey() {
		return targetImageKey;
	}

	@Override
	protected double getSubjectiveSpeed() {
		if (getGridPosition().asLongCoordinate().getY() == 17
				&& (getGridPosition().getX() < 5 || getGridPosition().getX() > 23)) {
			return SPRITE_MAX_SPEED * 0.4;
		}
		return SPRITE_MAX_SPEED * 0.75;
	}
}
