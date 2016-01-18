package gravityinc;

import gravityinc.util.OrderedPair;
import gravityinc.util.VersatilePosition;
import gravityinc.util.VersatilePosition.PositionType;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Panel specialized to display a spatial universe, represented by a universal
 * body (See {@link #loadUniverse(Body)}).
 * <p>
 * {@code UniversalDisplay} is not designed to be disposed along with its
 * universe. That is, a new {@code UniversalDisplay} does not need to be created
 * to display a new universe. Rather, universes can be unloaded and loaded at
 * the will of the implementation via loadUniverse(Body) and
 * {@link #unloadUniverse()} instead of creating new displays.
 * 
 * @author Charlie Morley
 *
 */
public class UniversalDisplay extends JPanel {

	/**
	 * Unused field.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The state of a {@code UniversalDisplay} in reference to its interaction
	 * with its displayed universe. Each display holds its state in the private
	 * field {@link UniversalDisplay#gameState}.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public enum GameState {
		/**
		 * Indicates that a display is free of any connection to a universe.
		 */
		EMPTY,
		/**
		 * Indicates that a display is in the process of loading a universe.
		 */
		INITIALIZING,
		/**
		 * Indicates that a display is connected to a universe and the
		 * universe's simulation time is paused.
		 */
		PAUSED,
		/**
		 * Indicates that
		 */
		STARTING, RUNNING, TERMINATING;
	}

	/**
	 * The state of this display in reference to its interaction with the
	 * universe it is displaying. Initialized with {@link GameState#EMPTY} in
	 * {@link #UniversalDisplay() the default constructor}.
	 */
	private GameState gameState;

	/**
	 * The representative body of the universe being displayed.
	 * {@code universalBody} is the "center" of its universe, with the other
	 * bodies in the universe being captive of it or captive of its captives,
	 * etc.
	 */
	private Body universalBody;

	/**
	 * The position (in spatial units) of the center of the camera relative to
	 * the {@link #universalBody}. This position in the universe is displayed in
	 * the center of the panel.
	 * <p>
	 * Automatically changes to the position of the {@link #focus}, if
	 * {@code focus} is not null.
	 */
	private OrderedPair cameraPosition;

	/**
	 * The body that the camera centers on. {@code null} if camera is
	 * free-moving. Determines the position stored in {@link #cameraPosition} if
	 * not {@code null}.
	 */
	private Body focus;

	/**
	 * Ratio of spatial units per panel pixel.
	 */
	private double zoom;

	/**
	 * Ratio of spatial simulation seconds per system second.
	 */
	private double timeMultiplier = 1;

	/**
	 * The {@link Runnable} that manages updating the universe's simulated
	 * physics.
	 */
	private GameClock gameClock;

	/**
	 * The thread that contains the {@link #gameClock}.
	 */
	private Thread gameThread;

	/**
	 * The coordinator of mapping external input to simulation actions for this
	 * display.
	 */
	private ActionManager actionManager;

	/**
	 * Constructs a new display without any connection to a universe.
	 */
	public UniversalDisplay() {
		super();
		gameState = GameState.EMPTY;
		universalBody = null;
		setBackground(Color.black);
	}

	/**
	 * Returns the state of this display with respect to its interaction with
	 * its universe.
	 * 
	 * @return this display's {@link #gameState}
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Returns the coordinator
	 * 
	 * @return
	 */
	public ActionManager getActionManager() {
		return actionManager;
	}

	public void setCameraPosition(OrderedPair cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setTimeMultiplier(double timeMultiplier) {
		this.timeMultiplier = timeMultiplier;
	}

	public void setFocus(Body focus) {
		this.focus = focus;
	}

	public boolean loadUniverse(Body universalBody) {
		if (gameState != GameState.EMPTY)
			return false;
		gameState = GameState.INITIALIZING;

		this.universalBody = universalBody;
		cameraPosition = new OrderedPair(0);
		zoom = 1;
		addSystem(universalBody);

		gameState = GameState.PAUSED;
		return true;
	}

	private ArrayList<Body> bodies = new ArrayList<Body>();

	private void addSystem(Body body) {
		bodies.add(body);
		ArrayList<Body> peers = body.getPeers();
		for (int i = 0; i < peers.size(); i++)
			addSystem(peers.get(i));
	}

	public boolean startUniverse(JFrame frame) {
		if (gameState != GameState.PAUSED)
			return false;
		gameState = GameState.STARTING;
		previousPaintTime = System.currentTimeMillis();
		actionManager = new ActionManager(frame);
		gameClock = new GameClock(this, frame);
		gameThread = new Thread(gameClock);
		gameThread.start();
		return true;
	}

	public boolean unloadUniverse() {
		if (gameState == GameState.PAUSED || gameState == GameState.RUNNING) {
			gameState = GameState.TERMINATING;
			return true;
		}
		return false;
	}

	private boolean finalizeTermination() {
		gameThread = null;
		gameClock = null;
		actionManager = null;
		bodies.clear();
		universalBody = null;
		gameState = GameState.EMPTY;
		return true;
	}

	private long previousPaintTime;

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (gameState == GameState.PAUSED || gameState == GameState.RUNNING) {
			long currentTime = System.currentTimeMillis();
			universalBody.getRender().render(
					graphics,
					new OrderedPair(0, 0),
					new OrderedPair(getSize()).divide(new OrderedPair(2)).add(
							cameraPosition.opposite().divide(
									new OrderedPair(zoom))), zoom,
					currentTime - previousPaintTime);
			previousPaintTime = currentTime;
		}
	}

	class GameClock implements Runnable {

		private UniversalDisplay instance;
		private JFrame frame;

		GameClock(UniversalDisplay instance, JFrame frame) {
			this.instance = instance;
			this.frame = frame;
		}

		private static final long REFRESH_TIME = 20;

		@Override
		public void run() {
			instance.gameState = GameState.RUNNING;
			long lastSystemTime = System.currentTimeMillis();
			while (instance.gameState == GameState.RUNNING
					|| instance.gameState == GameState.PAUSED) {
				long currentTime;
				do {
					currentTime = System.currentTimeMillis();
				} while (currentTime - lastSystemTime < REFRESH_TIME);
				if (instance.gameState == GameState.RUNNING)
					for (int i = 0; i < instance.bodies.size(); i++)
						instance.bodies.get(i).advance(
								(currentTime - lastSystemTime)
										* instance.timeMultiplier / 1000.0);

				if (focus != null) {
					OrderedPair newCameraPosition = new OrderedPair(0);
					Body currentFocus = focus;
					while (true) {
						if (currentFocus.getAnomaly() == null)
							break;
						newCameraPosition = newCameraPosition
								.add(new VersatilePosition(
										PositionType.Polar,
										currentFocus
												.getAnomaly()
												.asPosition()
												.getType(PositionType.Polar)
												.add(new OrderedPair(
														0,
														currentFocus
																.getAnomaly()
																.getOrbit().rotation)))
										.getType(PositionType.Rectangular));
						currentFocus = currentFocus.getCaptor();
					}
					cameraPosition = newCameraPosition;
				}
				frame.repaint();
				frame.setTitle("Gravity Inc. " + zoom);
				lastSystemTime = currentTime;
			}
			if (instance.gameState == GameState.TERMINATING)
				finalizeTermination();
		}
	}

	public final class ActionManager {

		private final ArrayList<InputAdapter> availableInputs = new ArrayList<InputAdapter>();
		private final ArrayList<ActionAdapter> availableActions = new ArrayList<ActionAdapter>();
		private final JFrame frame;
		private final HashMap<InputAdapter, ArrayList<ActionAdapter>> bindings = new HashMap<InputAdapter, ArrayList<ActionAdapter>>();

		private ActionManager(JFrame frame) {
			this.frame = frame;
		}

		public void bind(InputAdapter input, ActionAdapter action) {
			if (bindings.containsKey(input)) {
				ArrayList<ActionAdapter> actions = bindings.get(input);
				if (!actions.contains(action))
					actions.add(action);
			} else {
				ArrayList<ActionAdapter> actions = new ArrayList<ActionAdapter>();
				actions.add(action);
				bindings.put(input, actions);
			}
		}

		private void inputTriggered(InputAdapter input) {
			if (bindings.containsKey(input))
				for (ActionAdapter action : bindings.get(input))
					if (action != null)
						action.performAction();
		}

		public abstract class InputAdapter implements KeyListener,
				MouseWheelListener {

			public InputAdapter() {
				if (!ActionManager.this.availableInputs.contains(this)) {
					ActionManager.this.availableInputs.add(this);
					frame.addKeyListener(this);
					frame.addMouseWheelListener(this);
				}
			}

			protected void inputTriggered() {
				ActionManager.this.inputTriggered(this);
			}

			@Override
			public abstract boolean equals(Object o);

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			}
		}

		public abstract class InputAdapterBindable extends InputAdapter {

			public InputAdapterBindable(ActionAdapter action) {
				bind(action);
			}

			public void bind(ActionAdapter action) {
				if (action != null)
					ActionManager.this.bind(this, action);
			}
		}

		public final class InputKeyPress extends InputAdapterBindable {

			private int keyCode;
			private int modifiers;

			public InputKeyPress(int keyCode, int modifiers,
					ActionAdapter action) {
				super(action);
				this.keyCode = keyCode;
				this.modifiers = modifiers;
			}

			public InputKeyPress(int keyCode, int modifiers) {
				this(keyCode, modifiers, null);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == keyCode && e.getModifiers() == modifiers)
					inputTriggered();
			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof InputKeyPress)
						&& (((InputKeyPress) o).keyCode == keyCode)
						&& (((InputKeyPress) o).modifiers == modifiers);
			}
		}

		public final class InputMouseWheelUp extends InputAdapterBindable {

			public InputMouseWheelUp() {
				this(null);
			}

			public InputMouseWheelUp(ActionAdapter action) {
				super(action);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
					inputTriggered();
			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof InputMouseWheelUp);
			}

		}

		public final class InputMouseWheelDown extends InputAdapterBindable {

			public InputMouseWheelDown() {
				this(null);
			}

			public InputMouseWheelDown(ActionAdapter action) {
				super(action);
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() > 0)
					inputTriggered();

			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof InputMouseWheelDown);
			}

		}

		public abstract class ActionAdapter {

			protected ActionAdapter() {
				if (!ActionManager.this.availableActions.contains(this))
					availableActions.add(this);
			}

			public abstract void performAction();

			@Override
			public abstract boolean equals(Object o);
		}

		public final class ActionToggleRunning extends ActionAdapter {

			@Override
			public void performAction() {
				if (UniversalDisplay.this.gameState == GameState.PAUSED)
					UniversalDisplay.this.gameState = GameState.RUNNING;
				else if (UniversalDisplay.this.gameState == GameState.RUNNING)
					UniversalDisplay.this.gameState = GameState.PAUSED;
			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof ActionToggleRunning);
			}

		}

		public abstract class ActionZoom extends ActionAdapter {
			protected final static double ZOOM_RATIO = 0.08;
		}

		public final class ActionZoomIn extends ActionZoom {

			@Override
			public void performAction() {
				UniversalDisplay.this.zoom *= (1.0 - ZOOM_RATIO);
			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof ActionZoomIn);
			}

		}

		public final class ActionZoomOut extends ActionZoom {

			@Override
			public void performAction() {
				UniversalDisplay.this.zoom /= (1.0 - ZOOM_RATIO);
			}

			@Override
			public boolean equals(Object o) {
				return (o != null) && (o instanceof ActionZoomOut);
			}

		}
	}
}
