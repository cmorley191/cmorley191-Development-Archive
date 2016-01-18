package topologui;

/**
 * Represents a connection between two nodes in a {@link NeuralNetwork}.
 * 
 * @author Charlie Morley
 */
class Connection {

	/**
	 * The node from which this connection accepts information.
	 */
	private int inputNode;

	/**
	 * The node to which this connection deploys information.
	 */
	private int outputNode;

	/**
	 * The factor by which information that passes through this connection is
	 * multiplied.
	 */
	private double weight;

	/**
	 * Whether or not this connection is in use.
	 */
	private boolean enabled;

	/**
	 * The historical timestamp for this connection.
	 */
	private int innovation;

	/**
	 * Constructs a connection between the given input and output node with the
	 * given weight, state, and innovation.
	 * 
	 * @param inputNode
	 *            the node from which this connection accepts information
	 * @param outputNode
	 *            he node to which this connection deploys information
	 * @param weight
	 *            the factor by which information that passes through this
	 *            connection is multiplied
	 * @param enabled
	 *            whether or not this connection is in use
	 * @param innovation
	 *            the historical timestamp for this connection
	 */
	Connection(int inputNode, int outputNode, double weight, boolean enabled,
			int innovation) {
		this.inputNode = inputNode;
		this.outputNode = outputNode;
		this.weight = weight;
		this.enabled = enabled;
		this.innovation = innovation;
	}

	/**
	 * Constructs a connection between the given input and output node with the
	 * given state, and innovation, as well as a random weight as supplied by
	 * {@link #randomizeWeight()}.
	 * 
	 * @param inputNode
	 *            the node from which this connection accepts information
	 * @param outputNode
	 *            he node to which this connection deploys information
	 * @param enabled
	 *            whether or not this connection is in use
	 * @param innovation
	 *            the historical timestamp for this connection
	 */
	Connection(int inputNode, int outputNode, boolean enabled, int innovation) {
		this(inputNode, outputNode, 0.0, enabled, innovation);
		randomizeWeight();
	}

	/**
	 * Returns the node from which this connection accepts information.
	 * 
	 * @return the node from which this connection accepts information
	 */
	int getInputNode() {
		return inputNode;
	}

	/**
	 * Returns the node to which this connection deploys information.
	 * 
	 * @return the node to which this connection deploys information
	 */
	int getOutputNode() {
		return outputNode;
	}

	/**
	 * Returns the factor by which information that passes through this
	 * connection is multiplied.
	 * 
	 * @return the factor by which information that passes through this
	 *         connection is multiplied
	 */
	double getWeight() {
		return weight;
	}

	/**
	 * Sets this connection's weight to a random value between {@code -1.0} and
	 * {@code 1.0}, inclusive.
	 */
	void randomizeWeight() {
		weight = (Math.random() * (2.0 + Double.MIN_NORMAL)) - 1.0;
	}

	/**
	 * Uniformly perturbs this connection's weight.
	 * <p>
	 * Pseudo-perturbation by means of choosing a random value between -1.0 and
	 * 1.0, then bringing the value halfway towards the current weight 3 times.
	 */
	void perturbWeight() {
		double originalWeight = weight;
		randomizeWeight();
		for (int i = 0; i < 3; i++)
			weight += ((originalWeight - weight) / 2.0);
	}

	/**
	 * Returns whether or not this connection is in use.
	 * 
	 * @return whether or not this connection is in use
	 */
	boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets whether or not this connection is in use to the specified value.
	 * 
	 * @param enabled
	 *            the new state of this connection - {@code true} if in use,
	 *            {@code false} if not in use
	 */
	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the historical timestamp for this connection.
	 * 
	 * @return the historical timestamp for this connection
	 */
	int getInnovation() {
		return innovation;
	}

	@Override
	public Connection clone() {
		return new Connection(inputNode, outputNode, weight, enabled,
				innovation);
	}
}
