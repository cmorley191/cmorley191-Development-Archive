package test;

import topologui.NetworkTrainer;
import topologui.NeuralNetwork;
import topologui.NeuralNetworkProblemInterface;

/**
 * Testing ground for the TopoloGUI API.
 * <p>
 * Trains a set of 150 networks for 600 generations.<br>
 * Each network has 8 input and output nodes.<br>
 * The fitness of the network is evaluated such that high fitness networks
 * return a value greater than 0.5 for corresponding inputs less than 0.5, and
 * vise versa.<br>
 * e.g.<br>
 * Networks are supplied inputs {@code [ 0, 1, 0, 1, 0, 1, 0, 0 ]}.<br>
 * A network that outputs {@code [ 0, 0, 0, 0, 1, 1, 1, 1 ]} would have a
 * fitness of 5 because 5 slots outputted opposite values to their corresponding
 * inputs.<br>
 * A network that outputs {@code [ 0.95, 0.2, 0.6, 0.4, 0.76, 0.0, 1.0, 0.84 ]}
 * would have a fitness of 8 because all slots outputted values that rounded to
 * the opposite value of their inputs.
 * 
 * @author Charlie Morley
 *
 */
public class Test implements NeuralNetworkProblemInterface {

	/*
	 * Outputs into the console "Generation # complete" for each generation as
	 * it is generated and outputs all the networks for the final generation.
	 */
	public static void main(String[] args) {
		int pop = 150;
		int generationCount = 600;
		// see the constructor for what these values mean
		NetworkTrainer trainer = new NetworkTrainer(pop, 8, 8, 1, 1, 0.4, 0.2,
				0.001, 0.75, 0.8, 0.9, 0.03, 0.05);
		for (int i = 0; i < generationCount; i++) {
			trainer.advanceGeneration(new Test());
			System.out.print("Generation " + i + " complete.");
			double maxFitness = -1.0;
			for (NeuralNetwork network : trainer.getGenerations()[i])
				maxFitness = Math.max(maxFitness, network.getFitness());
			System.out.println(" Max Fitness: " + maxFitness);
		}
		NeuralNetwork[][] generations = trainer.getGenerations();
		for (int i = 0; i < pop; i++) {
			System.out.println("Network " + i + " - "
					+ generations[generationCount - 1][i]);
		}
	}

	/**
	 * The test-case network fitness function (non-Javadoc)
	 * 
	 * @see topologui.NeuralNetworkProblemInterface#getNetworkFitness(topologui.
	 * NeuralNetwork)
	 */
	@Override
	public double getNetworkFitness(NeuralNetwork network) {
		double[] inputs = { 0, 1, 0, 1, 0, 1, 0, 0 };
		double[] outputs = network.pullInformation(inputs);
		int fitness = 0;
		for (int i = 0; i < outputs.length; i++)
			if ((inputs[i] < 0.5) != (outputs[i] < 0.5))
				fitness++;
		return fitness;
	}
}
