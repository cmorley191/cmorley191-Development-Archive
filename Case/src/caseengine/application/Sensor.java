package caseengine.application;

import java.util.EnumMap;

/**
 * A {@code Sensor} provides an input link to intelligence systems from their
 * environments, whereby the intelligence system may acquire information about
 * the environment by fetching {@link Sensation Sensations} from a sensor.
 * <p>
 * A sensor to be integrated in an intelligence system must be able to return an
 * ordered set of all or some of the sensor's available sensations when prompted
 * by the intelligence system.
 * 
 * @author Charlie Morley
 *
 */
public interface Sensor<InputChannelType extends Enum<InputChannelType>> {

	/**
	 * Returns an ordered set of all or some of this sensor's available
	 * sensations. The index of corresponding sensations between different
	 * fetches is consistent - sensation slots without available sensations to
	 * the sensor at the time of fetching are null (and represent a sensation
	 * value of 0 in an intelligence system).
	 * 
	 * @return the current ordered inputs of this sensor
	 */
	public EnumMap<InputChannelType, Double> fetchSensations();
}
