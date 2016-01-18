package pacman.finals;

public enum Direction {
	UP, LEFT, DOWN, RIGHT;

	public Direction getOpposite() {
		switch (this) {
		case UP:
			return DOWN;
		case LEFT:
			return RIGHT;
		case DOWN:
			return UP;
		case RIGHT:
			return LEFT;
		default:
			return UP;
		}
	}
}
