package pacman.finals;

import java.io.Serializable;

public final class LongCoordinate implements Serializable {

	private static final long serialVersionUID = -4658537030547256984L;
	private long x;
	private long y;

	public LongCoordinate(long a, long b) {
		x = a;
		y = b;
	}

	public LongCoordinate(double a, double b) {
		x = (long) a;
		y = (long) b;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public static long combineX(LongCoordinate a, LongCoordinate b) {
		return (a.x + b.x);
	}

	public static long combineY(LongCoordinate a, LongCoordinate b) {
		return (a.y + b.y);
	}

	public LongCoordinate combineCoordinate(LongCoordinate c) {
		return new LongCoordinate(combineX(this, c), combineY(this, c));
	}

	public LongCoordinate subtractCoordinate(LongCoordinate c) {
		return new LongCoordinate(this.getX() - c.getX(), this.getY()
				- c.getY());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LongCoordinate))
			return false;
		LongCoordinate c = (LongCoordinate) o;
		return c.x == x && c.y == y;
	}

	public Coordinate asDoubleCoordinate() {
		return new Coordinate(this.x, this.y);
	}
}
