package pacman.abstracts;

import java.util.ArrayList;

import pacman.finals.Coordinate;
import pacman.finals.Direction;

@SuppressWarnings("serial")
public abstract class Sprite extends GameObject {
	protected Direction direction = Direction.UP;
	private double speed = 0.0; // in tiles per second
	protected ArrayList<MovementFlag> movementFlags = new ArrayList<MovementFlag>();
	protected boolean[][] legalSpaces;
	protected ArrayList<TriggerSpace> triggerSpaces;

	public final static double SPRITE_MAX_SPEED = 6.0;

	private EnteredNewTileMovementFlag triggerSpaceChecker = new EnteredNewTileMovementFlag();

	public Sprite(boolean[][] legalSpaces, ArrayList<TriggerSpace> triggerSpaces) {
		this.legalSpaces = legalSpaces;
		this.triggerSpaces = triggerSpaces;
		movementFlags.add(new ReachedCenterMovementFlag());
		// This flag isn't handled, it exists so that sprite movement is cut at
		// the center of each tile. That way, if the sprite reaches a wall
		// (which they will at the center of some tiles in some directions) they
		// won't finish their travel distance.
		movementFlags.add(triggerSpaceChecker);
		speed = getSubjectiveSpeed();
	}

	protected boolean positionInLegalBounds(Coordinate gridPosition) {
		return (gridPosition.getX() >= 0
				&& gridPosition.getX() < legalSpaces.length
				&& gridPosition.getY() >= 0 && gridPosition.getY() < legalSpaces[0].length);
	}

	public void processMovement(long processingTime) {
		double travelDistance = speed * processingTime / 1000.0;
		double previousSpeed = speed;

		while (travelDistance > 0) {
			switch (direction) {
			case UP:
				if (positionInLegalBounds(getPosition()
						.subtractCoordinate(
								new Coordinate(0,
										0.5 + MovementFlag.MINISCULEPUSHOVER)))
						&& !legalSpaces[(int) getPosition().getX()][(int) (getPosition()
								.getY() - 0.5 - MovementFlag.MINISCULEPUSHOVER)]) {
					travelDistance = 0;
					setPosition(new Coordinate(getPosition().getX(),
							(int) getPosition().getY() + 0.5
									+ MovementFlag.MINISCULEPUSHOVER));
				}
				break;
			case LEFT:
				if (positionInLegalBounds(getPosition()
						.subtractCoordinate(
								new Coordinate(
										0.5 + MovementFlag.MINISCULEPUSHOVER, 0)))
						&& !legalSpaces[(int) (getPosition().getX() - 0.5 - MovementFlag.MINISCULEPUSHOVER)][(int) getPosition()
								.getY()]) {
					travelDistance = 0;
					setPosition(new Coordinate((int) getPosition().getX() + 0.5
							+ MovementFlag.MINISCULEPUSHOVER, getPosition()
							.getY()));
				}
				break;
			case DOWN:
				if (positionInLegalBounds(getPosition().combineCoordinate(
						new Coordinate(0, 0.5)))
						&& !legalSpaces[(int) getPosition().getX()][(int) (getPosition()
								.getY() + 0.5)]) {
					travelDistance = 0;
					setPosition(new Coordinate(getPosition().getX(),
							(int) getPosition().getY() + 0.5
									- MovementFlag.MINISCULEPUSHOVER));
				}
				break;
			case RIGHT:
				if (positionInLegalBounds(getPosition().combineCoordinate(
						new Coordinate(0.5, 0)))
						&& !legalSpaces[(int) (getPosition().getX() + 0.5)][(int) getPosition()
								.getY()]) {
					travelDistance = 0;
					setPosition(new Coordinate((int) getPosition().getX() + 0.5
							- MovementFlag.MINISCULEPUSHOVER, getPosition()
							.getY()));
				}
				break;
			}
			double minFlagDistance = Double.POSITIVE_INFINITY;
			ArrayList<MovementFlag> activatedFlags = new ArrayList<MovementFlag>();
			for (int a = 0; a < movementFlags.size(); a++) {
				double distance = movementFlags.get(a).findDistanceToFlag(
						travelDistance);
				if (distance > travelDistance || distance < 0)
					continue;
				if (distance < minFlagDistance) {
					activatedFlags.clear();
					activatedFlags.add(movementFlags.get(a));
					minFlagDistance = distance;
				} else if (distance == minFlagDistance)
					activatedFlags.add(movementFlags.get(a));
			}

			if (activatedFlags.size() > 0) {
				switch (direction) {
				case UP:
					setPosition(

					getPosition().subtractCoordinate(
							new Coordinate(0, minFlagDistance
									+ MovementFlag.MINISCULEPUSHOVER)));
					break;
				case LEFT:
					setPosition(

					getPosition().subtractCoordinate(
							new Coordinate(minFlagDistance
									+ MovementFlag.MINISCULEPUSHOVER, 0)));
					break;
				case DOWN:
					setPosition(

					getPosition().combineCoordinate(
							new Coordinate(0, minFlagDistance
									+ MovementFlag.MINISCULEPUSHOVER)));
					break;
				case RIGHT:
					setPosition(

					getPosition().combineCoordinate(
							new Coordinate(minFlagDistance
									+ MovementFlag.MINISCULEPUSHOVER, 0)));
					break;
				}
				travelDistance -= minFlagDistance;
				for (int a = 0; a < activatedFlags.size(); a++)
					handleMovementFlag(activatedFlags.get(a), minFlagDistance);
			} else {
				switch (direction) {
				case UP:
					setPosition(

					getPosition().subtractCoordinate(
							new Coordinate(0, travelDistance)));
					break;
				case LEFT:
					setPosition(

					getPosition().subtractCoordinate(
							new Coordinate(travelDistance, 0)));
					break;
				case DOWN:
					setPosition(

					getPosition().combineCoordinate(
							new Coordinate(0, travelDistance)));
					break;
				case RIGHT:
					setPosition(

					getPosition().combineCoordinate(
							new Coordinate(travelDistance, 0)));
					break;
				}
				travelDistance = 0;
			}
			speed = getSubjectiveSpeed();
			travelDistance = (travelDistance / previousSpeed) * speed;
			previousSpeed = speed;
		}
	}

	protected abstract class MovementFlag {
		public static final double MINISCULEPUSHOVER = 0.0000000000001;

		public abstract double findDistanceToFlag(double travelDistance);
	}

	public final class ReachedCenterMovementFlag extends MovementFlag {

		@Override
		public double findDistanceToFlag(double travelDistance) {
			switch (direction) {
			case UP:
				// Check if entering new tile
				if (getPosition().getY() - travelDistance < (int) getPosition()
						.getY() + 0.5) {
					return getPosition().getY() - (int) getPosition().getY()
							- 0.5;
				}
				break;
			case LEFT:
				if (getPosition().getX() - travelDistance < (int) getPosition()
						.getX() + 0.5) {
					return getPosition().getX() - (int) getPosition().getX()
							- 0.5;
				}
				break;
			case DOWN:
				if (getPosition().getY() + travelDistance >= (int) getPosition()
						.getY() + 0.5) {
					return (int) getPosition().getY() + 0.5
							- getPosition().getY();
				}
				break;
			case RIGHT:
				if (getPosition().getX() + travelDistance >= (int) getPosition()
						.getX() + 0.5) {
					return (int) getPosition().getX() + 0.5
							- getPosition().getX();
				}
				break;
			}
			return Double.POSITIVE_INFINITY;

		}

	}

	public final class EnteredNewTileMovementFlag extends MovementFlag {

		@Override
		public double findDistanceToFlag(double travelDistance) {
			switch (direction) {
			case UP:
				// Check if entering new tile
				if (getPosition().getY() - travelDistance < (int) getPosition()
						.getY()) {
					return getPosition().getY() - (int) getPosition().getY();
				}
				break;
			case LEFT:
				if (getPosition().getX() - travelDistance < (int) getPosition()
						.getX()) {
					return getPosition().getX() - (int) getPosition().getX();
				}
				break;
			case DOWN:
				if (getPosition().getY() + travelDistance >= (int) getPosition()
						.getY() + 1) {
					return (int) getPosition().getY() + 1
							- getPosition().getY();
				}
				break;
			case RIGHT:
				if (getPosition().getX() + travelDistance >= (int) getPosition()
						.getX() + 1) {
					return (int) getPosition().getX() + 1
							- getPosition().getX();
				}
				break;
			}
			return Double.POSITIVE_INFINITY;
		}

	}

	public class ImmediateMovementFlag extends MovementFlag {

		// This flag executed immediately, so it must remove itself when done
		@Override
		public double findDistanceToFlag(double travelDistance) {
			return 0;
		}

	}

	protected void handleMovementFlag(MovementFlag flag,
			double travelledDistance) {
		if (flag == triggerSpaceChecker) {
			ArrayList<TriggerSpace> toBeTriggered = new ArrayList<TriggerSpace>();
			for (TriggerSpace t : triggerSpaces) {
				if (t.getPosition().equals(getPosition().asLongCoordinate())) {
					toBeTriggered.add(t);
				}
			}
			for (TriggerSpace t : toBeTriggered) {
				t.triggered(this);
			}
		}
	}

	public void setDirection(Direction newDirection) {
		direction = newDirection;
	}

	public Direction getDirection() {
		return direction;
	}

	protected abstract double getSubjectiveSpeed();
}
