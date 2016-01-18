package gravityinc.util;

public final class Orbit {

	/**
	 * Half the distance from apoapsis to periapsis, measured in kilometers
	 */
	public final double semimajorAxis;

	/**
	 * Eccentricity of the ellipse, between 0 and 1
	 */
	public final double eccentricity;

	/**
	 * Counter-clockwise angular position in reference to the captor's vernal
	 * point, measured in radians
	 */
	public final double rotation;

	public Orbit(double semimajorAxis, double eccentricity, double rotation) {
		this.semimajorAxis = semimajorAxis;
		this.eccentricity = eccentricity;
		this.rotation = rotation;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Orbit))
			return false;
		return (semimajorAxis == ((Orbit) o).semimajorAxis)
				&& (eccentricity == ((Orbit) o).eccentricity)
				&& (rotation == ((Orbit) o).rotation);
	}

	@Override
	public String toString() {
		return semimajorAxis + ", " + eccentricity + ", " + rotation;
	}
}
