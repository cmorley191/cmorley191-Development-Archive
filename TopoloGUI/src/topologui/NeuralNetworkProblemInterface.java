package topologui;

/**
 * Interacts with a {@link NetworkTrainer} by supplying testing for the
 * trainer's {@link NeuralNetwork NeuralNetworks}.
 * 
 * @author Charlie Morley
 */
public interface NeuralNetworkProblemInterface {

	/**
	 * Returns the fitness of the supplied network based on internal testing
	 * protocols.
	 * 
	 * @param network
	 *            the network being tested
	 * @return the fitness of the network being tested
	 */
	public double getNetworkFitness(NeuralNetwork network);
}
