package pacman.finals;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import pacman.abstracts.Dot;
import pacman.abstracts.Sprite;
import pacman.abstracts.TriggerSpace;
import pacman.children.EnergizerDot;
import pacman.children.Pacman;
import pacman.children.PointDot;
import pacman.finals.Ghost.HouseStatus;
import pacman.finals.GhostScript.GhostMode;
import pacman.finals.ResourceManager.ImageKey;

@SuppressWarnings("serial")
public final class Level extends JPanel implements Runnable {

	private ImageKey backgroundImage;
	private boolean[][] legalSpaces;
	private ArrayList<TriggerSpace> triggerSpaces;

	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	private Pacman pacman;
	private ArrayList<Ghost> ghosts = new ArrayList<Ghost>();
	private ArrayList<Dot> dots = new ArrayList<Dot>();
	private int currentGhostModePeriod;
	private GhostMode currentGhostMode;

	private Coordinate pacmanStartingLocation;
	private ArrayList<GhostParameters> ghostParameters;
	private ArrayList<GhostScript> ghostScripts;
	private long[] ghostModePeriods;
	private boolean reachedFinalGhostModePeriod = false;
	private GhostMode initialGhostMode;
	private long frightenedPeriod;
	private long frightenedTimeRemaining;
	private int globalDotCounter = -1;

	private ArrayList<Coordinate> startingPositions;
	private ArrayList<boolean[][]> idlingLegalSpaces;
	private ArrayList<boolean[][]> leavingLegalSpaces;
	private ArrayList<LongCoordinate> exitPositions;
	private ArrayList<HouseStatus> initialHouseStatuses;
	private ArrayList<Direction> initialDirections;

	public Level(JFrame window, LevelParameters levelParameters) {
		// Initialize Panel
		super();
		setLayout(null);

		// Unpack parameters
		this.backgroundImage = levelParameters.getBackgroundImage();
		this.legalSpaces = levelParameters.getLegalSpaces();
		this.ghostParameters = levelParameters.getGhostParameters();
		this.ghostScripts = levelParameters.getGhostScripts();
		this.ghostModePeriods = levelParameters.getGhostModePeriods();
		this.initialGhostMode = levelParameters.getInitialGhostMode();
		this.frightenedPeriod = levelParameters.getFrightenedPeriod();
		this.pacmanStartingLocation = levelParameters
				.getPacmanStartingLocation();
		this.triggerSpaces = levelParameters.getTriggerSpaces();
		ArrayList<LongCoordinate> pointDotPositions = levelParameters
				.getPointDotPositions();
		ArrayList<LongCoordinate> energizerDotPositions = levelParameters
				.getEnergizerDotPositions();
		this.startingPositions = levelParameters.getStartingPositions();
		this.idlingLegalSpaces = levelParameters.getIdlingLegalSpaces();
		this.leavingLegalSpaces = levelParameters.getLeavingLegalSpaces();
		this.exitPositions = levelParameters.getExitPositions();
		this.initialHouseStatuses = levelParameters.getInitialHouseStatuses();
		this.initialDirections = levelParameters.getInitialDirections();

		// Initialize Pacman
		pacman = new Pacman(legalSpaces, triggerSpaces);
		addSprite(pacman);
		window.addKeyListener(pacman);
		pacman.setPosition(pacmanStartingLocation);

		// Initialize Ghosts
		int houseBundle = 0;
		for (int i = 0; i < ghostParameters.size(); i++) {
			if (houseBundle == this.idlingLegalSpaces.size())
				houseBundle = 0;
			Ghost g = new Ghost(legalSpaces, triggerSpaces,
					ghostParameters.get(i),
					this.idlingLegalSpaces.get(houseBundle),
					this.leavingLegalSpaces.get(houseBundle),
					this.exitPositions.get(houseBundle),
					this.initialHouseStatuses.get(houseBundle),
					this.startingPositions.get(houseBundle),
					this.initialDirections.get(houseBundle));
			g.setUsingPersonalDotCounter(false);
			g.setUsingGlobalDotCounter(false);
			g.setGlobalDotCounter(0);
			ghosts.add(g);
			addSprite(g);
			houseBundle++;
		}

		currentGhostMode = initialGhostMode;
		currentGhostModePeriod = 0;
		frightenedTimeRemaining = frightenedPeriod;

		// Initialize Dots
		for (LongCoordinate position : pointDotPositions) {
			PointDot dot = new PointDot(position);
			dots.add(dot);
			this.add(dot);
		}
		for (LongCoordinate position : energizerDotPositions) {
			EnergizerDot dot = new EnergizerDot(position);
			dots.add(dot);
			this.add(dot);
		}
	}

	private void addSprite(Sprite s) {
		sprites.add(s);
		this.add(s);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(backgroundImage), 0, 0, null);
		/*
		 * if (showTargetTiles) { for (int a = 0; a < ghosts.size(); a++) {
		 * g.drawImage( ResourceManager.get(ghosts.get(a).getTargetImageKey()),
		 * (int) ghosts.get(a).getTargetTile().getX()
		 * ghosts.get(a).getGridSystem().getGridScale(), (int)
		 * ghosts.get(a).getTargetTile().getY()
		 * ghosts.get(a).getGridSystem().getGridScale(), null); } }
		 */
	}

	// Time measured in milliseconds
	private final long repaintPeriod = 20; // minimum time before repainting
	private final long processPeriod = 5; // minimum time before reprocessing
	private final long maxProcessPeriod = 50; // maximum time to process
	private final boolean enableFPSCounter = false;
	private final long FPSPeriod = 5000; // minimum time before outputting fps

	@SuppressWarnings("unused")
	@Override
	public final void run() {
		long lastRepaintTime = System.currentTimeMillis();
		long lastModeSwitchTime = lastRepaintTime;
		long lastProcessTime = lastRepaintTime;
		long lastFPSTime = lastRepaintTime;
		long processCount = 0;
		long repaintCount = 0;
		int lastDotCount = dots.size();

		while (dots.size() > 0) {
			long currentTime = System.currentTimeMillis();

			// process sprites
			if (currentTime - lastProcessTime >= processPeriod) {
				processCount++;

				long processingTime = currentTime - lastProcessTime;
				if (processingTime > maxProcessPeriod)
					processingTime = maxProcessPeriod;

				// update dot counters
				int dotsEaten = lastDotCount - dots.size();
				lastDotCount = dots.size();
				if (dotsEaten > 0 && globalDotCounter == -1) {
					boolean found = false;
					for (Ghost g : ghosts) {
						if (!found && g.getHouseStatus() == HouseStatus.Idling) {
							g.setUsingPersonalDotCounter(true);
							g.increasePersonalDotCounter(dotsEaten);
							if (g.getHouseStatus() == HouseStatus.Idling)
								found = true;
						} else
							g.setUsingPersonalDotCounter(false);
					}
					for (Ghost g : ghosts)
						g.increasePersonalDotCounter(dotsEaten);
				}

				// check modes
				// check frightened
				if (currentGhostMode == GhostMode.FRIGHTENED) {
					frightenedTimeRemaining -= processingTime;
					if (frightenedTimeRemaining > 0)
						lastModeSwitchTime += processingTime;
					else {
						if (currentGhostModePeriod % 2 == 0)
							currentGhostMode = initialGhostMode;
						else {
							switch (initialGhostMode) {
							case CHASE:
								currentGhostMode = GhostMode.SCATTER;
								break;
							case SCATTER:
								currentGhostMode = GhostMode.CHASE;
								break;
							default:
								break;
							}
						}
						for (Ghost g : ghosts)
							g.setFrightened(false);
						frightenedTimeRemaining = frightenedPeriod;
					}
				}
				// switch mode
				if (!reachedFinalGhostModePeriod
						&& currentTime - lastModeSwitchTime >= ghostModePeriods[currentGhostModePeriod]) {
					currentGhostModePeriod++;

					if (currentGhostModePeriod == ghostModePeriods.length) {
						currentGhostModePeriod--;
						reachedFinalGhostModePeriod = true;
					} else {

						switch (currentGhostMode) {
						case CHASE:
							currentGhostMode = GhostMode.SCATTER;
							break;
						case SCATTER:
							currentGhostMode = GhostMode.CHASE;
							break;
						default:
							break;
						}

						for (Ghost g : ghosts) {
							if (g.getHouseStatus() == HouseStatus.Active)
								g.setDirection(g.getDirection().getOpposite());
						}

						lastModeSwitchTime = currentTime;
					}
				}

				// allow level to process
				// processTime(processingTime);

				// process ghosts
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<LongCoordinate> locations = new ArrayList<LongCoordinate>();
				ArrayList<Direction> directions = new ArrayList<Direction>();
				names.add("Pacman");
				locations.add(pacman.getPosition().asLongCoordinate());
				directions.add(pacman.getDirection());
				for (Ghost g : ghosts) {
					names.add(g.getSpriteName());
					locations.add(g.getPosition().asLongCoordinate());
					directions.add(g.getDirection());
				}
				for (int i = 0; i < ghosts.size(); i++) {
					switch (currentGhostMode) {
					case CHASE:
						ghosts.get(i).setTargetTile(
								ghostScripts.get(i).getChaseTarget(names,
										locations, directions));
						break;
					case SCATTER:
						ghosts.get(i).setTargetTile(
								ghostScripts.get(i).getScatterTarget());
						break;
					default:
						break;
					}
				}

				// process movement
				for (int i = 0; i < sprites.size(); i++)
					sprites.get(i).processMovement(processingTime);

				// check collisions
				ArrayList<Dot> deadDots = new ArrayList<Dot>();
				for (Dot dot : dots) {
					if (pacman.getPosition().asLongCoordinate()
							.equals(dot.getPosition().asLongCoordinate())) {
						dot.eaten(this);
						this.remove(dot);
						deadDots.add(dot);
					}
				}
				dots.removeAll(deadDots);

				lastProcessTime = currentTime;
			}

			// repaint
			if (currentTime - lastRepaintTime >= repaintPeriod) {
				repaintCount++;

				repaint();

				lastRepaintTime = currentTime;
			}

			// process FPS
			if (enableFPSCounter && currentTime - lastFPSTime >= FPSPeriod) {
				double timeElapsed = ((double) currentTime - (double) lastFPSTime) / 1000.0;
				double processesPS = processCount / timeElapsed;
				double repaintsPS = repaintCount / timeElapsed;

				processCount = 0;
				repaintCount = 0;

				System.out.println((long) processesPS
						+ " processes per second, " + (long) repaintsPS
						+ " repaints PS");

				lastFPSTime = currentTime;
			}
		}
	}

	public void setFrightened() {
		this.currentGhostMode = GhostMode.FRIGHTENED;
		for (Ghost g : ghosts)
			g.setFrightened(true);
	}
}
