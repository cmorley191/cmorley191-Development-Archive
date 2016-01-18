package pacman.finals;

import java.awt.Dimension;
import java.util.ArrayList;

import pacman.abstracts.TriggerSpace;
import pacman.finals.Ghost.HouseStatus;
import pacman.finals.GhostScript.GhostMode;
import pacman.finals.ResourceManager.ImageKey;

public final class LevelParameters {

	private ImageKey backgroundImage;
	private Dimension levelSize;
	private boolean[][] legalSpaces;
	private ArrayList<GhostParameters> ghostParameters;
	private ArrayList<GhostScript> ghostScripts;
	private long[] ghostModePeriods;
	private GhostMode initialGhostMode;
	private long frightenedPeriod;
	private Coordinate pacmanStartingLocation;
	private ArrayList<TriggerSpace> triggerSpaces;
	private ArrayList<LongCoordinate> pointDotPositions;
	private ArrayList<LongCoordinate> energizerDotPositions;
	private ArrayList<Coordinate> startingPositions;
	private ArrayList<boolean[][]> idlingLegalSpaces;
	private ArrayList<boolean[][]> leavingLegalSpaces;
	private ArrayList<LongCoordinate> exitPositions;
	private ArrayList<HouseStatus> initialHouseStatuses;
	private ArrayList<Direction> initialDirections;

	public LevelParameters(ImageKey backgroundImage, Dimension levelSize,
			boolean[][] legalSpaces,
			ArrayList<GhostParameters> ghostParameters,
			ArrayList<GhostScript> ghostScripts, long[] ghostModePeriods,
			GhostMode initialGhostMode, long frightenedPeriod,
			Coordinate pacmanStartingLocation,
			ArrayList<TriggerSpace> triggerSpaces,
			ArrayList<LongCoordinate> pointDotPositions,
			ArrayList<LongCoordinate> energizerDotPositions,
			ArrayList<Coordinate> startingPositions,
			ArrayList<boolean[][]> idlingLegalSpaces,
			ArrayList<boolean[][]> leavingLegalSpaces,
			ArrayList<LongCoordinate> exitPositions,
			ArrayList<HouseStatus> initialHouseStatuses,
			ArrayList<Direction> initialDirections) {
		this.backgroundImage = backgroundImage;

		this.levelSize = levelSize;

		this.legalSpaces = new boolean[levelSize.width][levelSize.height];
		// Crop legalSpaces to levelSize
		int x;
		int y;
		for (x = 0; x < legalSpaces.length; x++) {
			// If there are more columns in legalSpaces than in levelSize, cut
			// off the copy.
			if (x >= levelSize.width)
				break;
			for (y = 0; y < legalSpaces[x].length; y++) {
				// If there are more rows in legalSpaces than in levelSize, cut
				// off the copy for this column.
				if (y >= levelSize.height)
					break;
				this.legalSpaces[x][y] = legalSpaces[x][y];
			}
			// If there were fewer rows in legalSpaces than in levelSize, fill
			// them with defaults for this column.
			for (; y < levelSize.height; y++) {
				this.legalSpaces[x][y] = false;
			}
		}
		// If there were fewer columns in legalSpaces than in levelSize, fill
		// them with defaults.
		for (; x < levelSize.width; x++) {
			for (y = 0; y < levelSize.height; y++) {
				this.legalSpaces[x][y] = false;
			}
		}

		this.ghostParameters = new ArrayList<GhostParameters>();
		this.ghostParameters.addAll(ghostParameters);

		this.ghostScripts = new ArrayList<GhostScript>();
		this.ghostScripts.addAll(ghostScripts);

		this.ghostModePeriods = new long[ghostModePeriods.length + 1];
		for (int i = 0; i < ghostModePeriods.length; i++)
			this.ghostModePeriods[i] = ghostModePeriods[i];
		ghostModePeriods[ghostModePeriods.length - 1] = 0;

		this.initialGhostMode = initialGhostMode;

		this.frightenedPeriod = frightenedPeriod;

		this.pacmanStartingLocation = pacmanStartingLocation;

		this.triggerSpaces = new ArrayList<TriggerSpace>();
		this.triggerSpaces.addAll(triggerSpaces);

		this.pointDotPositions = new ArrayList<LongCoordinate>();
		this.pointDotPositions.addAll(pointDotPositions);

		this.energizerDotPositions = new ArrayList<LongCoordinate>();
		this.energizerDotPositions.addAll(energizerDotPositions);

		this.startingPositions = new ArrayList<Coordinate>();
		this.startingPositions.addAll(startingPositions);

		this.idlingLegalSpaces = new ArrayList<boolean[][]>();
		this.idlingLegalSpaces.addAll(idlingLegalSpaces);

		this.leavingLegalSpaces = new ArrayList<boolean[][]>();
		this.leavingLegalSpaces.addAll(leavingLegalSpaces);

		this.exitPositions = new ArrayList<LongCoordinate>();
		this.exitPositions.addAll(exitPositions);

		this.initialHouseStatuses = new ArrayList<HouseStatus>();
		this.initialHouseStatuses.addAll(initialHouseStatuses);

		this.initialDirections = new ArrayList<Direction>();
		this.initialDirections.addAll(initialDirections);
	}

	public ImageKey getBackgroundImage() {
		return backgroundImage;
	}

	public Dimension getLevelSize() {
		return levelSize;
	}

	public boolean[][] getLegalSpaces() {
		return legalSpaces;
	}

	public ArrayList<GhostParameters> getGhostParameters() {
		ArrayList<GhostParameters> returnList = new ArrayList<GhostParameters>();
		returnList.addAll(ghostParameters);
		return returnList;
	}

	public ArrayList<GhostScript> getGhostScripts() {
		ArrayList<GhostScript> returnList = new ArrayList<GhostScript>();
		returnList.addAll(ghostScripts);
		return returnList;
	}

	public long[] getGhostModePeriods() {
		return ghostModePeriods;
	}

	public GhostMode getInitialGhostMode() {
		return initialGhostMode;
	}

	public long getFrightenedPeriod() {
		return frightenedPeriod;
	}

	public Coordinate getPacmanStartingLocation() {
		return pacmanStartingLocation;
	}

	public ArrayList<TriggerSpace> getTriggerSpaces() {
		return triggerSpaces;
	}

	public ArrayList<LongCoordinate> getPointDotPositions() {
		ArrayList<LongCoordinate> returnList = new ArrayList<LongCoordinate>();
		returnList.addAll(pointDotPositions);
		return returnList;
	}

	public ArrayList<LongCoordinate> getEnergizerDotPositions() {
		ArrayList<LongCoordinate> returnList = new ArrayList<LongCoordinate>();
		returnList.addAll(energizerDotPositions);
		return returnList;
	}

	public ArrayList<Coordinate> getStartingPositions() {
		ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
		returnList.addAll(startingPositions);
		return returnList;
	}

	public ArrayList<boolean[][]> getIdlingLegalSpaces() {
		ArrayList<boolean[][]> returnList = new ArrayList<boolean[][]>();
		returnList.addAll(idlingLegalSpaces);
		return returnList;
	}

	public ArrayList<boolean[][]> getLeavingLegalSpaces() {
		ArrayList<boolean[][]> returnList = new ArrayList<boolean[][]>();
		returnList.addAll(leavingLegalSpaces);
		return returnList;
	}

	public ArrayList<LongCoordinate> getExitPositions() {
		ArrayList<LongCoordinate> returnList = new ArrayList<LongCoordinate>();
		returnList.addAll(exitPositions);
		return returnList;
	}

	public ArrayList<HouseStatus> getInitialHouseStatuses() {
		ArrayList<HouseStatus> returnList = new ArrayList<HouseStatus>();
		returnList.addAll(initialHouseStatuses);
		return returnList;
	}

	public ArrayList<Direction> getInitialDirections() {
		ArrayList<Direction> returnList = new ArrayList<Direction>();
		returnList.addAll(initialDirections);
		return returnList;
	}
}
