package pacman.children;

import pacman.abstracts.Sprite;
import pacman.abstracts.TriggerSpace;
import pacman.finals.Coordinate;
import pacman.finals.LongCoordinate;
import pacman.finals.Direction;

public class TeleportSpace extends TriggerSpace {

	private LongCoordinate exitPosition;
	private Direction[] enterToExitDirectionKey;
	private TeleportSpace partner = null;

	public TeleportSpace(LongCoordinate gridPosition,
			LongCoordinate exitPosition, Direction[] enterToExitDirectionKey,
			Direction[] exitToEnterDirectionKey) {
		super(gridPosition);
		this.exitPosition = exitPosition;
		this.enterToExitDirectionKey = enterToExitDirectionKey;

		this.partner = new TeleportSpace(exitPosition, gridPosition,
				exitToEnterDirectionKey);
	}

	// For creating teleport spaces without partners
	private TeleportSpace(LongCoordinate gridPosition,
			LongCoordinate exitPosition, Direction[] enterToExitDirectionKey) {
		super(gridPosition);
		this.exitPosition = exitPosition;
		this.enterToExitDirectionKey = enterToExitDirectionKey;
	}

	@Override
	public void triggered(Sprite s) {
		Direction newDirection = Direction.UP;
		switch (s.getDirection()) {
		case UP:
			newDirection = (enterToExitDirectionKey[0]);
			break;
		case LEFT:
			newDirection = (enterToExitDirectionKey[1]);
			break;
		case DOWN:
			newDirection = (enterToExitDirectionKey[2]);
			break;
		case RIGHT:
			newDirection = (enterToExitDirectionKey[3]);
			break;
		}
		switch (newDirection) {
		case UP:
			s.setPosition(exitPosition.asDoubleCoordinate().combineCoordinate(
					new Coordinate(0.5, 0)));
			break;
		case LEFT:
			s.setPosition(exitPosition.asDoubleCoordinate().combineCoordinate(
					new Coordinate(0, 0.5)));
			break;
		case DOWN:
			s.setPosition(exitPosition.asDoubleCoordinate().combineCoordinate(
					new Coordinate(0.5, 0)));
			break;
		case RIGHT:
			s.setPosition(exitPosition.asDoubleCoordinate().combineCoordinate(
					new Coordinate(0, 0.5)));
			break;
		}
		s.setDirection(newDirection);
	}

	public TeleportSpace getPartner() {
		return partner;
	}

}
