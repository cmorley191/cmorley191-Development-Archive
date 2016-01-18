package gravityinc;

import gravityinc.util.Orbit;
import gravityinc.util.OrderedPair;
import gravityinc.util.VersatileAnomaly;
import gravityinc.util.VersatileAnomaly.AnomalyType;
import gravityinc.util.VersatilePosition.PositionType;

import java.util.ArrayList;

public class Body {

	/**
	 * The mass of the body in kilograms
	 */
	private double mass;

	/**
	 * Used in calculating the orbital period of a body orbiting this body
	 */
	private double standardGravitationalParameter;

	/**
	 * The universal gravitational constant, measured in
	 * (kilometers^3)/(kilograms * seconds^2)
	 */
	public static final double UNIVERSAL_GRAVITATIONAL_CONSTANT = 6.67408e-20;

	/**
	 * The radius of this body's sphere of influence, measured in kilometers
	 */
	private double soiRadius;

	/**
	 * The list of bodies orbiting this body
	 */
	private ArrayList<Body> peers;

	/**
	 * The body this body is orbiting
	 */
	private Body captor;

	/**
	 * The position of this body along its orbit
	 */
	private VersatileAnomaly anomaly;

	/**
	 * The orbital period of this body on its current path, in seconds.
	 * 
	 * The angular speed in radians per second of this body is (2*PI /
	 * orbitalPeriod)
	 */
	private double orbitalPeriod;

	private Render render;

	public Body(double mass) {
		this.mass = mass;
		this.standardGravitationalParameter = UNIVERSAL_GRAVITATIONAL_CONSTANT
				* mass;
		soiRadius = Double.POSITIVE_INFINITY;
		peers = new ArrayList<Body>();
	}

	public double getMass() {
		return mass;
	}

	public double getStandardGravitationalParameter() {
		return standardGravitationalParameter;
	}

	public double getSOIRadius() {
		return soiRadius;
	}

	public double getEscapeVelocity() {
		return Math.sqrt(2.0 * UNIVERSAL_GRAVITATIONAL_CONSTANT * captor.mass
				/ anomaly.asPosition().getType(PositionType.Polar).x);
	}

	public ArrayList<Body> getPeers() {
		ArrayList<Body> returnList = new ArrayList<Body>();
		returnList.addAll(peers);
		return returnList;
	}

	public Body getCaptor() {
		return captor;
	}

	public void setCaptor(Body captor, VersatileAnomaly anomaly) {
		if (this.captor != null)
			this.captor.peers.remove(this);
		this.captor = captor;
		if (captor != null) {
			captor.peers.add(this);
			this.standardGravitationalParameter = UNIVERSAL_GRAVITATIONAL_CONSTANT
					* (this.mass + captor.mass);
			soiRadius = anomaly.getOrbit().semimajorAxis
					* Math.pow(mass / captor.mass, 0.4);
			setAnomaly(anomaly);
			orbitalPeriod = 2.0
					* Math.PI
					* Math.sqrt(Math.pow(anomaly.getOrbit().semimajorAxis, 3)
							/ captor.standardGravitationalParameter);
		} else {
			soiRadius = Double.POSITIVE_INFINITY;
			this.anomaly = null;
			this.orbitalPeriod = 0;
		}
	}

	public void setCaptor(Body captor, Orbit orbit, AnomalyType anomalyType,
			double anomaly) {
		setCaptor(captor, new VersatileAnomaly(anomalyType, anomaly, orbit));
	}

	public VersatileAnomaly getAnomaly() {
		return anomaly;
	}

	public void setAnomaly(VersatileAnomaly anomaly) {
		this.anomaly = anomaly;
	}

	public double getOrbitalPeriod() {
		return orbitalPeriod;
	}

	public void setRender(Render render) {
		this.render = render;
	}

	public Render getRender() {
		return render;
	}

	public OrderedPair getSpatialSize() {
		return new OrderedPair(1e5);
	}

	/**
	 * Advances the body along its trajectory over the given amount of time
	 * 
	 * @param time
	 *            the time to advance, in seconds
	 */
	void advance(double time) {
		if (anomaly != null) {
			double advance = ((VersatileAnomaly.TWOPI / orbitalPeriod) * time);
			// System.out.println("Advancing mean anomaly " + advance +
			// " radians");
			setAnomaly(getAnomaly().add(AnomalyType.Mean, advance));
		}
	}

}
