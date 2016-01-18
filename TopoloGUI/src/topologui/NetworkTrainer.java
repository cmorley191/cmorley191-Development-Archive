package topologui;

import java.util.Arrays;

/**
 * A {@code NetworkTrainer} oversees the evolution of TopoloGUI
 * {@link NeuralNetwork NeuralNetworks}.
 * 
 * @author Charlie Morley
 */
public class NetworkTrainer {

	/**
	 * The set number of networks in each generation.
	 */
	private final int trainingPopulation;

	/**
	 * The compatibility calculation excess connection count weight.
	 */
	private final double excessConnectionModifier;

	/**
	 * The compatibility calculation disjoint connection count weight.
	 */
	private final double disjointConnectionModifier;

	/**
	 * The compatibility calculation average connection weight difference
	 * weight.
	 */
	private final double averageWeightDifferenceModifier;

	/**
	 * The threshold of compatibility between two networks to be considered of
	 * the same species.
	 */
	private final double compatibilityThreshold;

	/**
	 * The probability that a given offspring is a result of interspecies
	 * crossover instead of crossover within a species.
	 */
	private final double interspeciesMatingRate;

	/**
	 * The rate at which inherited connections in network offspring are disabled
	 * if they were disabled in either parent.
	 */
	private final double connectionDisableRate;

	/**
	 * The rate at which new offspring's connections' weights are mutated by
	 * either randomization or perturbation.
	 */
	private final double connectionMutationRate;

	/**
	 * The rate at which new offspring's connections' weights that are mutated
	 * are mutated by perturbation rather than by randomization.
	 */
	private final double connectionPerturbationRate;

	/**
	 * The rate at which networks mutate to add two new connections through the
	 * splitting of an existing connection with a new node.
	 */
	private final double connectionSegmentationRate;

	/**
	 * The rate at which networks mutate to add a new connection between two
	 * candidate nodes.
	 */
	private final double connectionGenerationRate;

	/**
	 * Constructs a {@code NetworkTrainer} according to the given
	 * specifications.
	 * <p>
	 * TODO NetworkTrainerBuilder class
	 * 
	 * @param trainingPopulation
	 *            the number of {@link NeuralNetwork NeuralNetworks} in each
	 *            generation
	 * @param inputNodes
	 *            the number of input nodes in each {@code NeuralNetwork}
	 * @param outputNodes
	 *            the number of output nodes in each {@code NeuralNetwork}
	 * @param excessConnectionModifier
	 *            the weight of excess connection count in calculating network
	 *            compatibility
	 * @param disjointConnectionModifier
	 *            the weight of disjoint connection count in calculating network
	 *            compatibility
	 * @param averageWeightDifferenceModifier
	 *            the weight of average connection weight difference in
	 *            calculating network compatibility
	 * @param compatibilityThreshold
	 *            the threshold of compatibility between two networks to be
	 *            considered of the same species
	 * @param interspeciesMatingRate
	 *            the probability that a given offspring is a result of
	 *            interspecies crossover instead of crossover within a species
	 * @param connectionDisableRate
	 *            the rate at which inherited connections in network offspring
	 *            are disabled if they were disabled in either parent
	 * @param connectionMutationRate
	 *            the rate at which new offspring's connections' weights are
	 *            mutated by either randomization or perturbation
	 * @param connectionPerturbationRate
	 *            the rate at which new offspring's connections' weights that
	 *            are mutated are mutated by perturbation rather than by
	 *            randomization
	 * @param connectionSegmentationRate
	 *            the rate at which networks mutate to split an existing
	 *            connection with a new node
	 * @param connectionGenerationRate
	 *            the rate at which networks mutate to add a new connection
	 *            between two candidate nodes
	 */
	public NetworkTrainer(int trainingPopulation, int inputNodes,
			int outputNodes, double excessConnectionModifier,
			double disjointConnectionModifier,
			double averageWeightDifferenceModifier,
			double compatibilityThreshold, double interspeciesMatingRate,
			double connectionDisableRate, double connectionMutationRate,
			double connectionPerturbationRate,
			double connectionSegmentationRate, double connectionGenerationRate) {
		this.trainingPopulation = trainingPopulation;
		this.excessConnectionModifier = excessConnectionModifier;
		this.disjointConnectionModifier = disjointConnectionModifier;
		this.averageWeightDifferenceModifier = averageWeightDifferenceModifier;
		this.compatibilityThreshold = compatibilityThreshold;
		this.interspeciesMatingRate = interspeciesMatingRate;
		this.connectionDisableRate = connectionDisableRate;
		this.connectionMutationRate = connectionMutationRate;
		this.connectionPerturbationRate = connectionPerturbationRate;
		this.connectionSegmentationRate = connectionSegmentationRate;
		this.connectionGenerationRate = connectionGenerationRate;

		generations = new NeuralNetwork[1][trainingPopulation];
		for (int i = 0; i < trainingPopulation; i++)
			generations[0][i] = new NeuralNetwork(inputNodes, outputNodes);

		species = new NeuralNetwork[0][1][0];
		speciesRepresentatives = new NeuralNetwork[0][1];

		latestInnovation = (inputNodes * outputNodes) - 1;
	}

	/**
	 * All the generations of neural networks this {@code NetworkTrainer} has
	 * managed.
	 * <p>
	 * Accessed by {@code generations[generation][network]}.
	 */
	private NeuralNetwork[][] generations;

	/**
	 * Returns this trainer's population history.
	 * 
	 * @return an array containing all the networks generated by the trainer, by
	 *         generation
	 */
	public NeuralNetwork[][] getGenerations() {
		NeuralNetwork[][] returnList = new NeuralNetwork[generations.length][];
		for (int i = 0; i < generations.length; i++)
			returnList[i] = Arrays
					.copyOf(generations[i], generations[i].length);
		return returnList;
	}

	/**
	 * All the species this {@code NetworkTrainer} manages.
	 * <p>
	 * Accessed by {@code species[species][generation][network]}.
	 */
	private NeuralNetwork[][][] species;

	/**
	 * The list of representative networks for each generation of each species.
	 * <p>
	 * Representatives are from the previous generation, i.e., the
	 * representative for testing in generation 6 is from generation 5, but is
	 * in index 6 of {@code speciesRepresentatives}. This is to account for that
	 * generation 0 cannot pull its representative from generation -1, because
	 * the index does not exist, nor does the representative come from that
	 * generation (the representative of a generation 0 species is a member of
	 * generation 0, as it must be a new species).
	 * <p>
	 * Accessed by {@code speciesRepresentatives[species][generation]}.
	 */
	private NeuralNetwork[][] speciesRepresentatives;

	/**
	 * Records the innovation number of the newest generation-unique structural
	 * mutation.
	 */
	private int latestInnovation;

	/**
	 * Advances through a sequence of network testing, speciation and breeding
	 * to generate a new network generation.
	 * 
	 * @param problemInterface
	 *            the problem interface that supplies the testing for the
	 *            networks of the current generation
	 */
	public void advanceGeneration(NeuralNetworkProblemInterface problemInterface) {
		int generation = generations.length - 1;

		/*
		 * Test all networks in the current generation.
		 */
		for (int n = 0; n < trainingPopulation; n++)
			generations[generation][n].setFitness(problemInterface
					.getNetworkFitness(generations[generation][n]));

		/*
		 * Speciate all networks in the current generation.
		 */
		for (NeuralNetwork network : generations[generation]) {

			/*
			 * Check if the network fits into an existing living species. If so,
			 * place it there.
			 */
			boolean foundSpecies = false;
			for (int s = 0; s < species.length; s++) {
				if (speciesRepresentatives[s][generation] != null
						&& network.getCompatibility(
								speciesRepresentatives[s][generation],
								excessConnectionModifier,
								disjointConnectionModifier,
								averageWeightDifferenceModifier) <= compatibilityThreshold) {
					species[s][generation] = Arrays.copyOf(
							species[s][generation],
							species[s][generation].length + 1);
					species[s][generation][species[s][generation].length - 1] = network;
					network.setSpecies(s);
					foundSpecies = true;
					break;
				}
			}

			/*
			 * If the network matched no existing species, create a new species
			 * with the network as the representative. Fill the previous
			 * generations of the species with an empty list of networks.
			 */
			if (!foundSpecies) {
				species = Arrays.copyOf(species, species.length + 1);
				species[species.length - 1] = new NeuralNetwork[generation + 1][0];
				species[species.length - 1][generation] = new NeuralNetwork[] { network };
				speciesRepresentatives = Arrays.copyOf(speciesRepresentatives,
						speciesRepresentatives.length + 1);
				speciesRepresentatives[speciesRepresentatives.length - 1] = new NeuralNetwork[generation + 1];
				speciesRepresentatives[speciesRepresentatives.length - 1][generation] = network;
				network.setSpecies(species.length - 1);
			}
		}

		/*
		 * Determine how many offspring are a product of interspecies mating.
		 */
		int interspeciesOffspring = 0;
		for (int i = 0; i < trainingPopulation; i++)
			if (Math.random() < interspeciesMatingRate)
				interspeciesOffspring++;

		/*
		 * Assign a number of offspring to each species.
		 */
		double[] averageSpeciesFitnesses = new double[species.length];
		for (int s = 0; s < species.length; s++) {
			if (species[s][generation].length > 0) {
				for (NeuralNetwork network : species[s][generation])
					averageSpeciesFitnesses[s] += network.getFitness();
				averageSpeciesFitnesses[s] /= species[s][generation].length;
			}
		}
		double availableOffspring = trainingPopulation - interspeciesOffspring;
		double totalAverageSpeciesFitness = 0.0;
		for (double s : averageSpeciesFitnesses)
			totalAverageSpeciesFitness += s;
		int[] assignedOffsprings = new int[species.length];
		double[] extraOffspringRates = new double[species.length];
		// Assign offspring to each species equal to the floor of the average
		// species fitness divided by the sum of all species' fitnesses.
		for (int s = 0; s < species.length; s++) {
			double intermediateAssignedOffspring = (availableOffspring * averageSpeciesFitnesses[s])
					/ totalAverageSpeciesFitness;
			// assignedOffsprings[s] = floor of assigned offspring of s
			assignedOffsprings[s] = (int) intermediateAssignedOffspring;
			// extraOffspringRates[s] = decimal remainder of the assigned
			// offspring
			extraOffspringRates[s] = intermediateAssignedOffspring
					- assignedOffsprings[s];
		}
		for (int assignedOffspring : assignedOffsprings)
			availableOffspring -= assignedOffspring;
		// The rest of the available offspring not assigned due to rounding are
		// chosen based on the decimal remainders for each species, stored in
		// extraOffspringRates. See NetworkTrainer#selectRandomWeights(double[],
		// int)
		if (availableOffspring > 0) {
			int[] chosenSpecies = selectRandomWeights(extraOffspringRates,
					(int) availableOffspring);
			for (int i : chosenSpecies)
				assignedOffsprings[i]++;
		}

		/*
		 * Generate the next generation.
		 */
		generations = Arrays.copyOf(generations, generations.length + 1);
		generations[generation + 1] = new NeuralNetwork[trainingPopulation];
		int offspringCounter = 0;
		// Interspecies reproduction
		if (interspeciesOffspring > 0) {
			double[] generationFitnesses = new double[trainingPopulation];
			for (int n = 0; n < trainingPopulation; n++)
				generationFitnesses[n] = generations[generation][n]
						.getFitness();
			// The first partner of each mating pair is selected by fitness
			int[] firstPartners = selectRandomWeights(generationFitnesses,
					interspeciesOffspring);
			// These variables are simply used to store previously calculated
			// fitness totals. Essentially, it's optimization for preventing
			// recalculation of the same values
			double[][] otherSpeciesFitnessesStored = new double[species.length][0];
			NeuralNetwork[][] otherSpeciesNetworksStored = new NeuralNetwork[species.length][0];
			for (int partner1 : firstPartners) {
				// This loop selects potential partners for each first partner.
				// i.e. networks from other species
				int partner1Species = generations[generation][partner1]
						.getSpecies();
				double[] otherSpeciesFitnesses;
				NeuralNetwork[] otherSpeciesNetworks;
				if (otherSpeciesFitnessesStored[partner1Species].length > 0) {
					otherSpeciesFitnesses = otherSpeciesFitnessesStored[partner1Species];
					otherSpeciesNetworks = otherSpeciesNetworksStored[partner1Species];
				} else {
					otherSpeciesFitnesses = new double[0];
					otherSpeciesNetworks = new NeuralNetwork[0];
					for (int s = 0; s < species.length; s++) {
						if (s == partner1Species)
							continue;
						int index = otherSpeciesFitnesses.length;
						otherSpeciesFitnesses = Arrays.copyOf(
								otherSpeciesFitnesses,
								otherSpeciesFitnesses.length
										+ species[s][generation].length);
						otherSpeciesNetworks = Arrays.copyOf(
								otherSpeciesNetworks,
								otherSpeciesFitnesses.length);
						for (NeuralNetwork network : species[s][generation]) {
							otherSpeciesFitnesses[index] = network.getFitness();
							otherSpeciesNetworks[index] = network;
							index++;
						}
					}
					otherSpeciesFitnessesStored[partner1Species] = otherSpeciesFitnesses;
					otherSpeciesNetworksStored[partner1Species] = otherSpeciesNetworks;
				}
				if (otherSpeciesFitnesses.length > 0) {
					// Partner 2 is selected by fitness
					int partner2 = selectRandomWeights(otherSpeciesFitnesses, 1)[0];
					generations[generation + 1][offspringCounter] = generations[generation][partner1]
							.crossover(otherSpeciesNetworks[partner2],
									connectionDisableRate);
					offspringCounter++;
				} else {
					// If there is only one living species, mate within the
					// species
					assignedOffsprings[generations[generation][partner1]
							.getSpecies()]++;
				}
			}
		}
		// Species by species reproduction
		for (int s = 0; s < species.length; s++) {
			if (assignedOffsprings[s] > 0) {
				double[] speciesFitnesses = new double[species[s][generation].length];
				for (int n = 0; n < speciesFitnesses.length; n++)
					speciesFitnesses[n] = species[s][generation][n]
							.getFitness();
				double[][] otherNetworkFitnessesStored = new double[species[s][generation].length][0];
				int[] firstPartners = selectRandomWeights(speciesFitnesses,
						assignedOffsprings[s]);
				for (int partner1 : firstPartners) {
					NeuralNetwork partner2;
					if (species[s][generation].length > 1) {
						double[] otherNetworkFitnesses;
						if (otherNetworkFitnessesStored[partner1].length > 0)
							otherNetworkFitnesses = otherNetworkFitnessesStored[partner1];
						else {
							otherNetworkFitnesses = new double[species[s][generation].length - 1];
							for (int n = 0; n < otherNetworkFitnesses.length; n++)
								if (n == partner1)
									continue;
								else if (n < partner1)
									otherNetworkFitnesses[n] = species[s][generation][n]
											.getFitness();
								else
									otherNetworkFitnesses[n] = species[s][generation][n + 1]
											.getFitness();
						}
						int partner2Index = selectRandomWeights(
								otherNetworkFitnesses, 1)[0];
						if (partner2Index < partner1)
							partner2 = species[s][generation][partner2Index];
						else
							partner2 = species[s][generation][partner2Index + 1];
					} else
						partner2 = species[s][generation][partner1];
					generations[generation + 1][offspringCounter] = species[s][generation][partner1]
							.crossover(partner2, connectionDisableRate);
					offspringCounter++;
				}
			}
		}

		/*
		 * Mutate all offspring.
		 */
		Connection[] generationInnovations = new Connection[0];
		for (int n = 0; n < generations[generation + 1].length; n++) {

			/*
			 * Mutate existing connections' weights.
			 */
			for (int c = 0; c < generations[generation + 1][n].connections.length; c++) {
				if (Math.random() < connectionMutationRate) {
					if (Math.random() < connectionPerturbationRate)
						generations[generation + 1][n].connections[c]
								.perturbWeight();
					else
						generations[generation + 1][n].connections[c]
								.randomizeWeight();
				}
			}

			/*
			 * Segment an enabled connection.
			 */
			if (Math.random() < connectionSegmentationRate) {
				int newestNode = -1;
				Connection[] enabledConnections = new Connection[0];
				for (Connection c : generations[generation + 1][n].connections) {
					newestNode = Math.max(newestNode,
							Math.max(c.getInputNode(), c.getOutputNode()));
					if (c.getEnabled()) {
						enabledConnections = Arrays.copyOf(enabledConnections,
								enabledConnections.length + 1);
						enabledConnections[enabledConnections.length - 1] = c;
					}
				}
				if (enabledConnections.length > 0) {
					Connection segmentedConnection = enabledConnections[(int) (Math
							.random() * enabledConnections.length)];
					segmentedConnection.setEnabled(false);
					generations[generation + 1][n].connections = Arrays
							.copyOf(generations[generation + 1][n].connections,
									generations[generation + 1][n].connections.length + 2);
					int inputInnovation = -1;
					Connection inputConnection;
					for (Connection c : generationInnovations) {
						if (c.getInputNode() == segmentedConnection
								.getInputNode()
								&& c.getOutputNode() == newestNode + 1) {
							inputInnovation = c.getInnovation();
							break;
						}
					}
					if (inputInnovation == -1) {
						latestInnovation++;
						inputInnovation = latestInnovation;
						inputConnection = new Connection(
								segmentedConnection.getInputNode(),
								newestNode + 1, 1.0, true, inputInnovation);
						generationInnovations = Arrays.copyOf(
								generationInnovations,
								generationInnovations.length + 1);
						generationInnovations[generationInnovations.length - 1] = inputConnection;
					} else {
						inputConnection = new Connection(
								segmentedConnection.getInputNode(),
								newestNode + 1, 1.0, true, inputInnovation);
					}
					int outputInnovation = -1;
					Connection outputConnection;
					for (Connection c : generationInnovations) {
						if (c.getInputNode() == newestNode + 1
								&& c.getOutputNode() == segmentedConnection
										.getOutputNode()) {
							outputInnovation = c.getInnovation();
							break;
						}
					}
					if (outputInnovation == -1) {
						latestInnovation++;
						outputInnovation = latestInnovation;
						outputConnection = new Connection(newestNode + 1,
								segmentedConnection.getOutputNode(),
								segmentedConnection.getWeight(), true,
								outputInnovation);
						generationInnovations = Arrays.copyOf(
								generationInnovations,
								generationInnovations.length + 1);
						generationInnovations[generationInnovations.length - 1] = outputConnection;
					} else {
						outputConnection = new Connection(newestNode + 1,
								segmentedConnection.getOutputNode(),
								segmentedConnection.getWeight(), true,
								outputInnovation);
					}
					generations[generation + 1][n].connections[generations[generation + 1][n].connections.length - 2] = inputConnection;
					generations[generation + 1][n].connections[generations[generation + 1][n].connections.length - 1] = outputConnection;
				}
			}

			/*
			 * Add a connection.
			 */
			if (Math.random() < connectionGenerationRate) {
				Connection[] candidateConnections = generations[generation + 1][n]
						.getValidCandidateConnections();
				if (candidateConnections.length > 0) {
					Connection selectedConnection = candidateConnections[(int) (Math
							.random() * candidateConnections.length)];

					/*
					 * Assign innovation number
					 */
					int innovation = -1;
					for (Connection c : generationInnovations) {
						if (c.getInputNode() == selectedConnection
								.getInputNode()
								&& c.getOutputNode() == selectedConnection
										.getOutputNode()) {
							innovation = c.getInnovation();
							break;
						}
					}
					if (innovation == -1) {
						latestInnovation++;
						innovation = latestInnovation;
					}
					generations[generation + 1][n].connections = Arrays
							.copyOf(generations[generation + 1][n].connections,
									generations[generation + 1][n].connections.length + 1);
					generations[generation + 1][n].connections[generations[generation + 1][n].connections.length - 1] = new Connection(
							selectedConnection.getInputNode(),
							selectedConnection.getOutputNode(),
							selectedConnection.getWeight(),
							selectedConnection.getEnabled(), innovation);
				}
			}
		}

		/*
		 * Select the species representatives for the offspring. Open a new
		 * generation in the species list for the offspring.
		 */
		for (int s = 0; s < species.length; s++) {
			speciesRepresentatives[s] = Arrays.copyOf(
					speciesRepresentatives[s],
					speciesRepresentatives[s].length + 1);
			if (species[s][generation].length > 0) {
				speciesRepresentatives[s][generation + 1] = species[s][generation][(int) (Math
						.random() * species[s][generation].length)];
			}
			species[s] = Arrays.copyOf(species[s], species[s].length + 1);
			species[s][generation + 1] = new NeuralNetwork[0];
		}
	}

	/**
	 * Utility function that, provided a list of weights, selects a random index
	 * of the list weighted by the weights in the list.
	 * <p>
	 * e.g. If the list of weights is {@code [ 0.2, 0.5, 0.3 ]}, there is a 20%
	 * chance of 0 being selected, 50% of 1, and 30% chance of 2.
	 * <p>
	 * The weights are weights of the sum of the list. e.g. if the list of
	 * weights is {@code [ 0.4, 0.5, 0.6 ]}, index 1 has a 33% chance of being
	 * selected because the total is {@code 1.5} and {@code 0.5 / 1.5} is
	 * {@code 0.33}.
	 * 
	 * @param list
	 *            the list of weights
	 * @param count
	 *            how many selections to make
	 * @return an array of selections with length equal to the {@code count}
	 *         parameter
	 */
	private static int[] selectRandomWeights(double[] list, int count) {
		if (list.length == 0)
			return new int[0];
		double total = 0.0;
		for (double d : list)
			total += d;
		int[] indicies = new int[count];
		for (int c = 0; c < count; c++) {
			double value = Math.random() * total;
			for (int i = 0;; i++) {
				value -= list[i];
				if (value <= 0 || i == list.length - 1) {
					indicies[c] = i;
					break;
				}
			}
		}
		return indicies;
	}

}
