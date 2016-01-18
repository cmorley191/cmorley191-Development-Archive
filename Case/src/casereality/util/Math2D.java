package casereality.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public class Math2D {

	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180.0;
	}

	public static Point2D.Double polarToRect(double radius, double angle) {
		return new Point2D.Double(radius * Math.cos(angle), radius
				* Math.sin(angle));
	}

	public static double distance(Point2D.Double a, Point2D.Double b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static double getOverlap(Rectangle2D.Double a, Rectangle2D.Double b) {
		Rectangle2D.Double overlap = (Rectangle2D.Double) b
				.createIntersection(a);
		double area = overlap.width * overlap.height;
		double totalArea = a.width * a.height;
		return area / totalArea;
	}

	public static Point2D.Double nearestPoint(Point2D.Double point,
			Point2D.Double[] points) {
		if (points.length == 0)
			return null;
		Point2D.Double nearest = points[0];
		double nearestDistance = distance(point, nearest);
		for (int i = 1; i < points.length; i++) {
			double distance = distance(point, points[i]);
			if (distance < nearestDistance) {
				nearest = points[i];
				nearestDistance = distance;
			}
		}
		return nearest;
	}

	public static Point2D.Double getIntersection(Line2D.Double a,
			Line2D.Double b) {
		double slopeA = slope(a);
		double slopeB = slope(b);
		if (slopeA == slopeB
				&& evaluateY(slopeA, a.getP1(), 0) == evaluateY(slopeB,
						b.getP1(), 0))
			return null;
		Point2D.Double intersect = null;
		if (Double.isNaN(slopeA)) {
			double xIntersect = a.x1;
			intersect = new Point2D.Double(xIntersect, evaluateY(slopeB,
					b.getP1(), xIntersect));
		} else if (Double.isNaN(slopeB)) {
			double xIntersect = b.x1;
			intersect = new Point2D.Double(xIntersect, evaluateY(slopeA,
					a.getP1(), xIntersect));
		} else {
			double xIntersect = (-(slopeA * a.x1) + a.y1 + (slopeB * b.x1) - b.y1)
					/ (-slopeA + slopeB);
			intersect = new Point2D.Double(xIntersect, evaluateY(slopeA,
					a.getP1(), xIntersect));
		}
		return (pointOnSegment(intersect, a) && pointOnSegment(intersect, b)) ? intersect
				: null;
	}

	private static double slope(Line2D.Double line) {
		if (line.x1 == line.x2)
			return Double.NaN;
		return ((line.y1 - line.y2) / (line.x1 - line.x2));
	}

	private static double evaluateY(double slope, Point2D point, double x) {
		return (slope * (x - point.getX())) + point.getY();
	}

	private static boolean pointOnSegment(Point2D.Double point,
			Line2D.Double line) {
		return pointInBounds(point.x, point.y, line.x1, line.x2, line.y1,
				line.y2);
	}

	private static boolean pointInBounds(double x, double y, double x1,
			double x2, double y1, double y2) {
		if (x2 < x1) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y2 < y1) {
			double temp = y1;
			y1 = y2;
			y2 = temp;
		}
		return (x >= x1) && (x <= x2) && (y >= y1) && (y <= y2);
	}

	public static Point2D.Double[] getIntersections(Line2D.Double line,
			Rectangle2D.Double box) {
		Line2D.Double[] lines = boxToLines(box);
		if (lines.length == 0)
			return null;
		Point2D.Double[] intersects = new Point2D.Double[0];
		for (Line2D.Double l : lines) {
			Point2D.Double intersect = getIntersection(line, l);
			if (intersect != null) {
				intersects = Arrays.copyOf(intersects, intersects.length + 1);
				intersects[intersects.length - 1] = intersect;
			}
		}
		return intersects;
	}

	private static Line2D.Double[] boxToLines(Rectangle2D.Double box) {
		if (box.height == 0 || box.width == 0)
			return new Line2D.Double[] {};
		return new Line2D.Double[] {
				new Line2D.Double(box.getMinX(), box.getMinY(), box.getMaxX(),
						box.getMinY()),
				new Line2D.Double(box.getMaxX(), box.getMinY(), box.getMaxX(),
						box.getMaxY()),
				new Line2D.Double(box.getMaxX(), box.getMaxY(), box.getMinX(),
						box.getMaxY()),
				new Line2D.Double(box.getMinX(), box.getMaxY(), box.getMinX(),
						box.getMinY()) };
	}
}
