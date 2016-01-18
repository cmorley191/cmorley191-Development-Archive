package gravityinc.util;

import gravityinc.util.VersatilePosition.PositionType;

import java.util.HashMap;

public class VersatileAnomaly {

	public final static double TWOPI = 2.0 * Math.PI;

	public enum AnomalyType {
		Mean, Eccentric, True;
	}

	private HashMap<AnomalyType, Double> anomalyMap = new HashMap<AnomalyType, Double>();

	public final Orbit orbit;

	private VersatilePosition position;

	private HashMap<Double, OrderedPair> velocityMap = new HashMap<Double, OrderedPair>();

	public VersatileAnomaly(AnomalyType type, double anomaly, Orbit orbit) {
		putCalculated(type, anomaly);
		this.orbit = orbit;
	}

	public VersatileAnomaly(VersatilePosition position, Orbit orbit) {
		this.orbit = orbit;
		putCalculated(
				AnomalyType.True,
				leastPositiveCoterminalAngle(position
						.getType(PositionType.Polar).y - orbit.rotation));
		this.position = position;
	}

	public VersatileAnomaly(VersatilePosition position, OrderedPair velocity,
			double standardGravitationalParameter) {
		this(position, calculateOrbit(position, velocity,
				standardGravitationalParameter));
		velocityMap.put(standardGravitationalParameter, velocity);
	}

	private static Orbit calculateOrbit(VersatilePosition position,
			OrderedPair velocity, double standardGravitationalParameter) {
		OrderedPair rectPosition = position.getType(PositionType.Rectangular);
		double semimajorAxis = -standardGravitationalParameter
				/ (2.0 * ((Math.pow(velocity.magnitude(), 2) / 2.0) - (standardGravitationalParameter / rectPosition
						.magnitude())));
		OrderedPair eccentricityVector = rectPosition
				.multiply(new OrderedPair(velocity.dot(velocity)))
				.add(velocity.multiply(
						new OrderedPair(rectPosition.dot(velocity))).opposite())
				.divide(new OrderedPair(standardGravitationalParameter))
				.add(rectPosition.divide(
						new OrderedPair(rectPosition.magnitude())).opposite());
		double eccentricity = eccentricityVector.magnitude();
		double rotation = new VersatilePosition(PositionType.Rectangular,
				eccentricityVector).getType(PositionType.Polar).y;
		return new Orbit(semimajorAxis, eccentricity, rotation);
	}

	private void putCalculated(AnomalyType type, double anomaly) {
		anomalyMap.put(type, leastPositiveCoterminalAngle(anomaly));
	}

	public static double leastPositiveCoterminalAngle(double val) {
		while (val < 0.0)
			val += TWOPI;
		return val % (TWOPI);
	}

	public boolean isTypeCalculated(AnomalyType type) {
		return anomalyMap.containsKey(type);
	}

	static final double ECCENTRIC_ANOMALY_ESTIMATION_PRECISION = 1e-12;

	public double getType(AnomalyType type) {
		if (anomalyMap.containsKey(type))
			return anomalyMap.get(type);
		else {
			double eccentric;
			switch (type) {
			case Mean:
				eccentric = getType(AnomalyType.Eccentric);
				putCalculated(AnomalyType.Mean, eccentric
						- (orbit.eccentricity * Math.sin(eccentric)));
				break;
			case Eccentric:
				if (anomalyMap.containsKey(AnomalyType.Mean)) {
					// Newtons method of M = E - esinE to find E
					double mean = getType(AnomalyType.Mean);
					double guess = mean;
					double previousGuess;
					do {
						previousGuess = guess;
						guess = previousGuess
								- ((previousGuess
										- (orbit.eccentricity * Math
												.sin(previousGuess)) - mean) / (1.0 - (orbit.eccentricity * Math
										.cos(previousGuess))));
					} while (Math.abs(guess - previousGuess) > ECCENTRIC_ANOMALY_ESTIMATION_PRECISION);
					putCalculated(AnomalyType.Eccentric, guess);
					return guess;
				} else {
					double trueAnomaly = getType(AnomalyType.True);
					double trueAnomalyCos = Math.cos(trueAnomaly);
					double eccentricAnomaly = Math
							.acos((orbit.eccentricity + trueAnomalyCos)
									/ (1.0 + (orbit.eccentricity * trueAnomalyCos)));
					if (trueAnomaly > Math.PI) {
						eccentricAnomaly = -eccentricAnomaly;
					}
					putCalculated(AnomalyType.Eccentric, eccentricAnomaly);
				}
				break;
			case True:
				eccentric = getType(AnomalyType.Eccentric);
				double trueAnomaly = Math
						.acos((Math.cos(eccentric) - orbit.eccentricity)
								/ (1.0 - (orbit.eccentricity * Math
										.cos(eccentric))));
				if (eccentric > Math.PI)
					trueAnomaly = -trueAnomaly;
				putCalculated(AnomalyType.True, trueAnomaly);
				break;
			default:
				return Double.NaN;
			}
			return getType(type);
		}
	}

	public Orbit getOrbit() {
		return orbit;
	}

	public VersatilePosition asPosition() {
		if (position == null)
			position = new VersatilePosition(this);
		return position;
	}

	public OrderedPair getVelocity(double standardGravitationalParameter) {
		if (!velocityMap.containsKey(standardGravitationalParameter)) {
			double velocityX = Double.NaN;
			double velocityY = Double.NaN;
			OrderedPair positionVector = asPosition().getType(
					PositionType.Rectangular);
			OrderedPair eccentricityVector = new VersatilePosition(
					PositionType.Polar, new OrderedPair(orbit.eccentricity,
							orbit.rotation)).getType(PositionType.Rectangular);
			eccentricityVector = eccentricityVector.add(
					positionVector.divide(new OrderedPair(positionVector
							.magnitude()))).multiply(
					new OrderedPair(standardGravitationalParameter));
			// System.out.println("Position Vector: " + positionVector);
			// System.out.println("Eccentricity Vector " + eccentricityVector);
			if (eccentricityVector.y == 0) {
				velocityX = 0;
				velocityY = Math.sqrt(eccentricityVector.x)
						/ Math.sqrt(positionVector.x);
			} else {
				if (positionVector.x == 0) {
					velocityX = Math.sqrt(eccentricityVector.y)
							/ Math.sqrt(positionVector.y);
					velocityY = -eccentricityVector.x
							/ (Math.sqrt(positionVector.y) * Math
									.sqrt(eccentricityVector.y));
				} else {
					double sqrDot = Math.sqrt(positionVector
							.dot(eccentricityVector));
					velocityX = eccentricityVector.y / sqrDot;
					velocityY = -eccentricityVector.x / sqrDot;
				}
			}
			velocityMap.put(standardGravitationalParameter, new OrderedPair(
					velocityX, velocityY));
		}
		return velocityMap.get(standardGravitationalParameter);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VersatileAnomaly))
			return false;
		VersatileAnomaly other = (VersatileAnomaly) o;
		for (AnomalyType type : anomalyMap.keySet())
			if (other.anomalyMap.containsKey(type)
					&& Math.abs(other.anomalyMap.get(type).doubleValue()
							- anomalyMap.get(type).doubleValue()) > ECCENTRIC_ANOMALY_ESTIMATION_PRECISION)
				return false;
		return Math.abs(other.getType(AnomalyType.Eccentric)
				- getType(AnomalyType.Eccentric)) <= ECCENTRIC_ANOMALY_ESTIMATION_PRECISION;
	}

	public VersatileAnomaly add(AnomalyType type, double anomaly) {
		return new VersatileAnomaly(type, getType(type) + anomaly, orbit);
	}
}
