package caseengine.application.mind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import caseengine.application.Actuator;
import caseengine.application.Sensor;
import caseengine.cognition.executive.BoltzmannExecutive;
import caseengine.cognition.executive.BoltzmannExecutive.DynamicImpulseController;
import caseengine.cognition.memory.QNetwork;

/**
 * 
 * @author Charlie Morley
 *
 */
public final class CaseMind {

	private QNetwork memory = new QNetwork(1);
	private DynamicImpulseController impulseController = new DynamicImpulseController(
			1);
	private BoltzmannExecutive executive = new BoltzmannExecutive(
			impulseController);

	/**
	 * Maps tracked sensors to a mapping of their tracked sensations to the
	 * index of their corresponding input nodes.
	 */
	private HashMap<Sensor<? extends Enum<?>>, HashMap<Enum<?>, Integer>> trackedSensors = new HashMap<Sensor<? extends Enum<?>>, HashMap<Enum<?>, Integer>>();

	/**
	 * Maps tracked actuators to a mapping of their tracked actions to the index
	 * of their corresponding output nodes.
	 */
	private HashMap<Actuator<? extends Enum<?>>, HashMap<Enum<?>, Integer>> trackedActuators = new HashMap<Actuator<? extends Enum<?>>, HashMap<Enum<?>, Integer>>();

	private static final double LEARNING_RATE = 0.01;
	private static final double GREEDY_RATE = 0.0001;

	public void addActuator(Actuator<? extends Enum<?>> actuator) {
		if (actuator == null)
			throw new NullPointerException();

		if (trackedActuators.containsKey(actuator))
			return;
		trackedActuators.put(actuator, new HashMap<Enum<?>, Integer>());
	}

	public void addSensor(Sensor<? extends Enum<?>> sensor) {
		if (sensor == null)
			throw new NullPointerException();

		if (trackedSensors.containsKey(sensor))
			return;
		trackedSensors.put(sensor, new HashMap<Enum<?>, Integer>());
	}

	private double[] previousInputs;
	private int[] previouslyPerformedActions;

	public void act() {
		double[] inputs = new double[memory.getInputCount()];
		for (Entry<Sensor<? extends Enum<?>>, HashMap<Enum<?>, Integer>> sensorMap : trackedSensors
				.entrySet()) {
			EnumMap<? extends Enum<?>, Double> sensations = sensorMap.getKey()
					.fetchSensations();
			for (Entry<? extends Enum<?>, Double> sensation : sensations
					.entrySet()) {
				if (!sensorMap.getValue().containsKey(sensation.getKey())) {
					sensorMap.getValue().put(sensation.getKey(),
							memory.addInput());
					memory.addHiddenNode();
					inputs = Arrays.copyOf(inputs, inputs.length + 1);
				}
				inputs[sensorMap.getValue().get(sensation.getKey())] = sensation
						.getValue();
			}
		}
		previousInputs = inputs;

		for (Entry<Actuator<? extends Enum<?>>, HashMap<Enum<?>, Integer>> actuatorMap : trackedActuators
				.entrySet()) {
			ArrayList<Enum<?>> availableActions = new ArrayList<Enum<?>>();
			availableActions.addAll(actuatorMap.getKey().getActionSet());
			if (availableActions.size() == 0)
				continue;
			for (int i = 0; i < availableActions.size(); i++) {
				if (!actuatorMap.getValue()
						.containsKey(availableActions.get(i))) {
					actuatorMap.getValue().put(availableActions.get(i),
							memory.addAction());
					memory.addHiddenNode();
				}
			}

		}

		HashMap<Actuator<? extends Enum<?>>, Enum<?>> selectedActions = new HashMap<Actuator<? extends Enum<?>>, Enum<?>>();
		previouslyPerformedActions = new int[0];
		double[] outputs = memory.predictActionReinforcements(inputs);
		System.out.print("<");
		for (double output : outputs)
			System.out.print(output + ", ");
		System.out.println(">");
		for (Entry<Actuator<? extends Enum<?>>, HashMap<Enum<?>, Integer>> actuatorMap : trackedActuators
				.entrySet()) {
			ArrayList<Enum<?>> availableActions = new ArrayList<Enum<?>>();
			availableActions.addAll(actuatorMap.getKey().getActionSet());
			if (availableActions.size() == 0)
				continue;
			double[] predictedReinforcements = new double[availableActions
					.size()];
			for (int i = 0; i < availableActions.size(); i++) {
				predictedReinforcements[i] = outputs[actuatorMap.getValue()
						.get(availableActions.get(i))];
			}
			Enum<?> selectedAction = availableActions.get(executive
					.selectAction(predictedReinforcements));
			selectedActions.put(actuatorMap.getKey(), selectedAction);
			previouslyPerformedActions = Arrays.copyOf(
					previouslyPerformedActions,
					previouslyPerformedActions.length + 1);
			previouslyPerformedActions[previouslyPerformedActions.length - 1] = actuatorMap
					.getValue().get(selectedAction);
		}

		for (Entry<Actuator<? extends Enum<?>>, Enum<?>> action : selectedActions
				.entrySet())
			action.getKey().performAction(action.getValue());

		impulseController.setImpulseResistance(impulseController
				.getImpulseResistance() * (1.0 - GREEDY_RATE));
		System.out.println(impulseController.getImpulseResistance());
	}

	/**
	 * 
	 * @param reinforcement
	 * @throws IllegalStateException
	 *             if {@code act()} has not yet been called
	 */
	public void reinforce(double reinforcement) throws IllegalStateException {
		if (previousInputs == null)
			throw new IllegalStateException();
		double[] targetReinforcements = new double[previouslyPerformedActions.length];
		Arrays.fill(targetReinforcements, reinforcement);
		memory.trainActionReinforcements(previousInputs,
				previouslyPerformedActions, targetReinforcements, LEARNING_RATE);
	}
}
