package gravityinc.util;

import java.awt.Dimension;
import java.io.Serializable;

@SuppressWarnings("serial")
public final class OrderedPair implements Serializable {

	public final double x;
	public final double y;

	public OrderedPair(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public OrderedPair(OrderedPair pair) {
		x = pair.x;
		y = pair.y;
	}

	public OrderedPair(Dimension dimension) {
		x = dimension.width;
		y = dimension.height;
	}

	public OrderedPair(double a) {
		x = a;
		y = a;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public Dimension asDimension() {
		return new Dimension((int) x, (int) y);
	}

	public OrderedPair removeDecimal() {
		return new OrderedPair((int) x, (int) y);
	}

	public OrderedPair opposite() {
		return new OrderedPair(-x, -y);
	}

	public OrderedPair oppositeX() {
		return new OrderedPair(-x, y);
	}

	public OrderedPair oppositeY() {
		return new OrderedPair(x, -y);
	}

	public OrderedPair abs() {
		return new OrderedPair(Math.abs(x), Math.abs(y));
	}

	public OrderedPair sqrt() {
		return new OrderedPair(Math.sqrt(x), Math.sqrt(y));
	}

	public double magnitude() {
		return Math.sqrt((x * x) + (y * y));
	}

	public OrderedPair add(OrderedPair pair) {
		return new OrderedPair(x + pair.x, y + pair.y);
	}

	public OrderedPair multiply(OrderedPair pair) {
		return new OrderedPair(x * pair.x, y * pair.y);
	}

	public OrderedPair divide(OrderedPair pair) {
		return new OrderedPair(x / pair.x, y / pair.y);
	}

	public OrderedPair pow(OrderedPair pair) {
		return new OrderedPair(Math.pow(x, pair.x), Math.pow(y, pair.y));
	}

	public OrderedPair remainder(OrderedPair pair) {
		return new OrderedPair(x % pair.x, y % pair.y);
	}

	public boolean lessThan(OrderedPair pair) {
		return x < pair.x && y < pair.y;
	}

	public boolean greaterThan(OrderedPair pair) {
		return x > pair.x && y > pair.y;
	}

	public boolean lessThanOrEqualTo(OrderedPair pair) {
		return x <= pair.x && y <= pair.y;
	}

	public boolean greaterThanOrEqualTo(OrderedPair pair) {
		return x >= pair.x && y >= pair.y;
	}

	public boolean equals(OrderedPair pair) {
		return x == pair.x && y == pair.y;
	}

	public boolean notEquals(OrderedPair pair) {
		return x != pair.x && y != pair.y;
	}

	public double dot(OrderedPair pair) {
		return (x * pair.x) + (y * pair.y);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof OrderedPair) {
			return ((OrderedPair) object).x == x
					&& ((OrderedPair) object).y == y;
		}
		return false;
	}

	@Override
	public String toString() {
		return magnitude() + ": <" + x + ", " + y + ">";
	}
}
