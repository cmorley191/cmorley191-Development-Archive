package pacman.finals;

import java.io.Serializable;

public final class Coordinate implements Serializable {

	private static final long serialVersionUID = -4658537030547256984L;
	private double x;
	private double y;

	public Coordinate(double a, double b) {
		x = a;
		y = b;
	}

	public Coordinate(long a, long b) {
		x = a;
		y = b;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public static double combineX(Coordinate a, Coordinate b) {
		return (a.x + b.x);
	}

	public static double combineY(Coordinate a, Coordinate b) {
		return (a.y + b.y);
	}

	public Coordinate combineCoordinate(Coordinate c) {
		return new Coordinate(combineX(this, c), combineY(this, c));
	}

	public Coordinate subtractCoordinate(Coordinate c) {
		return new Coordinate(this.getX() - c.getX(), this.getY() - c.getY());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Coordinate))
			return false;
		Coordinate c = (Coordinate) o;
		return c.x == x && c.y == y;
	}

	public LongCoordinate asLongCoordinate() {
		return new LongCoordinate(this.x, this.y);
	}
}
