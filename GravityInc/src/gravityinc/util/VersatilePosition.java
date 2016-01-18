package gravityinc.util;

import gravityinc.util.VersatileAnomaly.AnomalyType;

import java.util.HashMap;

public final class VersatilePosition {

	public enum PositionType {
		Polar, Rectangular;
	}

	private HashMap<PositionType, OrderedPair> positionMap = new HashMap<PositionType, OrderedPair>();

	private HashMap<Orbit, VersatileAnomaly> anomalyMap = new HashMap<Orbit, VersatileAnomaly>();

	public VersatilePosition(PositionType type, OrderedPair position) {
		if (type == PositionType.Polar)
			position = new OrderedPair(position.x,
					VersatileAnomaly.leastPositiveCoterminalAngle(position.y));
		positionMap.put(type, position);
	}

	public VersatilePosition(VersatileAnomaly anomaly) {
		positionMap.put(PositionType.Polar, anomalyToPolar(anomaly));
		this.anomalyMap.put(anomaly.orbit, anomaly);
	}

	private OrderedPair anomalyToPolar(VersatileAnomaly anomaly) {
		return new OrderedPair(
				anomaly.orbit.semimajorAxis
						* ((1.0 - (anomaly.orbit.eccentricity * anomaly.orbit.eccentricity)) / (1 + (anomaly.orbit.eccentricity * Math.cos(anomaly
								.getType(AnomalyType.True))))),
				anomaly.getType(AnomalyType.True) + anomaly.getOrbit().rotation);
	}

	public boolean isTypeCalculated(PositionType type) {
		return positionMap.containsKey(type);
	}

	public OrderedPair getType(PositionType type) {
		if (positionMap.containsKey(type))
			return positionMap.get(type);
		switch (type) {
		case Polar:
			OrderedPair rectangular = getType(PositionType.Rectangular);
			double angle;
			if (rectangular.x == 0) {
				if (rectangular.y > 0)
					angle = 0.5 * Math.PI;
				else if (rectangular.y < 0)
					angle = 1.5 * Math.PI;
				else
					angle = 0;
			} else {
				angle = Math.atan(rectangular.y / rectangular.x);
				if (rectangular.x < 0.0)
					angle -= Math.PI;
			}
			positionMap.put(
					PositionType.Polar,
					new OrderedPair(rectangular.magnitude(), VersatileAnomaly
							.leastPositiveCoterminalAngle(angle)));
			break;
		case Rectangular:
			OrderedPair polar = getType(PositionType.Polar);
			positionMap.put(PositionType.Rectangular, new OrderedPair(polar.x
					* Math.cos(polar.y), polar.x * Math.sin(polar.y)));
			break;
		default:
			return null;
		}
		return getType(type);
	}

	public VersatileAnomaly asAnomalyOnOrbit(Orbit orbit) {
		if (!anomalyMap.containsKey(orbit))
			anomalyMap.put(orbit, new VersatileAnomaly(this, orbit));
		return anomalyMap.get(orbit);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof VersatilePosition))
			return false;
		return Math.abs(((VersatilePosition) o).positionMap
				.get(PositionType.Rectangular)
				.add(positionMap.get(PositionType.Rectangular).opposite())
				.magnitude()) <= Math
				.sqrt(VersatileAnomaly.ECCENTRIC_ANOMALY_ESTIMATION_PRECISION);
	}

}
