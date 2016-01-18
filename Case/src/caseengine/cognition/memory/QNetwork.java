package caseengine.cognition.memory;

import caseengine.function.DifferentiableFunction;
import caseengine.neural.network.LayeredNetwork;
import caseengine.neural.network.LayeredNetwork.ConnectionScheme;
import caseengine.neural.node.DifferentiableNode;
import caseengine.neural.train.ErrorBackpropagator;

/**
 * A Q-Learning memory system that uses a
 * {@link caseengine.neural.network.LayeredNetwork Layered Feed-Forward Neural
 * Network} to predict the reinforcement outcome of taking certain actions in
 * certain environmental states.
 * <p>
 * A {@code QNetwork} contains a feed-forward neural network that represents a
 * function mapping a set of inputs (usually sensor inputs representing an
 * environmental state) to a set of reinforcement outputs (each output is a
 * prediction of the reinforcement result of taking its corresponding action in
 * the current state). The network can be trained over time such that the
 * predicted reinforcement outcomes more closely represent the actual outcomes
 * of taking certain actions in certain environmental states.
 * <p>
 * Traditionally, Q-Learning (the artificial intelligence design methodology of
 * improving a memory map of state-action pairs to reinforcements) has used a
 * lookup table to store its mapped data. The advantages of using a neural
 * network to store the data revolve around the nature of neural networks:
 * neural networks are compact representations of complicated mathematical
 * functions. A function's advantages over a lookup table include both the low
 * definition size requirement (lookup tables require a large amount of data
 * memory compared to a function) and the fact that functions can interpolate
 * outputs when given inputs that were not used in its creation or development,
 * while a lookup table can only return values for states it has encountered
 * previously (therefore an agent using a function memory system can make
 * educated guesses in environmental states it has not encountered before).
 * 
 * @author Charlie Morley
 *
 */
public class QNetwork {

	/**
	 * The neural network for storing state-action-reinforcement data.
	 */
	private LayeredNetwork reinforcementNetwork;

	/**
	 * The instance used for training the network.
	 */
	private ErrorBackpropagator trainer = new ErrorBackpropagator();

	/**
	 * The activation function used in the hidden nodes of this network.
	 */
	private static final DifferentiableFunction HIDDEN_ACTIVATION_FUNCTION = new caseengine.function.SigmoidFunction();

	/**
	 * The activation function used in the output nodes of this network.
	 */
	private static final DifferentiableFunction OUTPUT_ACTIVATION_FUNCTION = new caseengine.function.LinearFunction(
			1, 0);

	/**
	 * The connection scheme used to generate new connections between inputs and
	 * hidden nodes in this network. (Ideally one that generates relatively low
	 * weights as an existing network needs to learn how to use the new data
	 * stream before strongly integrating it)
	 */
	private static final ConnectionScheme INPUT_CONNECTION_SCHEME = new LayeredNetwork.RandomWeightScheme(
			0.1, 1.0);

	/**
	 * The connection scheme used to generate new connections between outputs
	 * and hidden nodes in this network. (Ideally one that generates relatively
	 * high weights so this network's policy is more quickly evident through the
	 * output data to a learning algorithm)
	 */
	private static final ConnectionScheme OUTPUT_CONNECTION_SCHEME = new LayeredNetwork.RandomWeightScheme(
			0.2, 0.8);

	/**
	 * Constructs a new {@code QNetwork} with the given number of nodes (minimum
	 * of 1) in the layer separating network inputs and outputs.
	 * 
	 * @param hiddenNodeCount
	 *            the number of nodes separating this network's input nodes and
	 *            output nodes - if less than 1, this parameter is set to 1
	 */
	public QNetwork(int hiddenNodeCount) {
		if (hiddenNodeCount < 1)
			hiddenNodeCount = 1;
		reinforcementNetwork = new LayeredNetwork(2);
		for (; hiddenNodeCount > 0; hiddenNodeCount--)
			reinforcementNetwork.addNode(new DifferentiableNode(
					HIDDEN_ACTIVATION_FUNCTION), 0);
	}

	/**
	 * Returns the number of inputs currently in this network.
	 * 
	 * @return the current number of input nodes in this network
	 */
	public int getInputCount() {
		return reinforcementNetwork.getInputs().size();
	}

	/**
	 * Adds a new input node to this network, freeing a new slot for feeding
	 * data into this network.
	 * 
	 * @return the index of the new input
	 */
	public int addInput() {
		reinforcementNetwork.addInput(INPUT_CONNECTION_SCHEME);
		return getInputCount() - 1;
	}

	/**
	 * Returns the number of hidden nodes currently in this network.
	 * 
	 * @return the number of nodes in between the inputs and outputs of the
	 *         network
	 */
	public int getHiddenNodeCount() {
		return reinforcementNetwork.getLayer(0).size();
	}

	/**
	 * Adds a new node to this network's hidden layer.
	 */
	public void addHiddenNode() {
		reinforcementNetwork.addNode(new DifferentiableNode(
				HIDDEN_ACTIVATION_FUNCTION), 0, INPUT_CONNECTION_SCHEME,
				OUTPUT_CONNECTION_SCHEME);
	}

	/**
	 * Returns the number of actions currently being tracked by this network.
	 * 
	 * @return the current number of output nodes in this network, each
	 *         corresponding to an action
	 */
	public int getActionCount() {
		return reinforcementNetwork.getLayer(1).size();
	}

	/**
	 * Adds a new output node to this network, incrementing the number of
	 * actions whose reinforcements this network is able to predict.
	 * 
	 * @return the index of the new action
	 */
	public int addAction() {
		reinforcementNetwork.addNode(new DifferentiableNode(
				OUTPUT_ACTIVATION_FUNCTION), 1, OUTPUT_CONNECTION_SCHEME);
		return getActionCount() - 1;
	}

	/**
	 * Uses the current state of this neural network to predict the outcome
	 * reinforcements of taking each action that this network is tracking given
	 * the current environmental state as provided by the specified input data.
	 * 
	 * @param stateInputs
	 *            the data of the cognitive inputs that represent the current
	 *            state of this network's environment
	 * @return the predicted reinforcements of taking each corresponding action
	 *         in the current environmental state
	 */
	public double[] predictActionReinforcements(double[] stateInputs) {
		return reinforcementNetwork.pullOutputs(stateInputs, 1);
	}

	/**
	 * Uses error backpropagation to train this network towards providing action
	 * reinforcements nearer to those specified when given the specified inputs.
	 * 
	 * @param stateInputs
	 *            the data of the cognitive inputs that represent a state of
	 *            this network's environment
	 * @param actions
	 *            the indices of the actions whose reinforcements are being
	 *            trained
	 * @param targetReinforcements
	 *            the target reinforcement corresponding to each action in
	 *            {@code actions}
	 * @param learningRate
	 *            a modifier for error backpropagation, usually less than or
	 *            equal to 1 - smaller values make learning over time more
	 *            gradual
	 * @see caseengine.neural.train.ErrorBackpropagator#trainNetwork(LayeredNetwork,
	 *      double[], int, int[], double[], double)
	 *      ErrorBackpropagator.trainNetwork(LayeredNetwork, double[], int,
	 *      int[], double[], double)
	 */
	public void trainActionReinforcements(double[] stateInputs, int[] actions,
			double[] targetReinforcements, double learningRate) {
		trainer.trainNetwork(reinforcementNetwork, stateInputs, 1, actions,
				targetReinforcements, learningRate);
	}
}
